package com.nicomahnic.dadm.fotolog2021.ui.home.fragments

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.example.cameraxexample.CameraUtility
import com.example.cameraxexample.Constants
import com.example.cameraxexample.Constants.FILENAME_FORMAT
import com.example.cameraxexample.Constants.TAG
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.nicomahnic.dadm.fotolog2021.R
import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.data.remote.PostDataSource
import com.nicomahnic.dadm.fotolog2021.databinding.FragmentAddPostBinding
import com.nicomahnic.dadm.fotolog2021.domain.PostRepoImpl
import com.nicomahnic.dadm.fotolog2021.presentation.HomeScreenViewMModelFactory
import com.nicomahnic.dadm.fotolog2021.presentation.HomeScreenViewModel
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AddPostFragment : Fragment(R.layout.fragment_add_post) {

    private lateinit var binding: FragmentAddPostBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var savedUri: Uri

    private val firebaseAuth by lazy { FirebaseAuth.getInstance()}
    private val viewModel by viewModels<HomeScreenViewModel>{
        HomeScreenViewMModelFactory(
            PostRepoImpl(
                PostDataSource()
            )
        )
    }

    private var downloadUrl = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()

        binding = FragmentAddPostBinding.bind(view)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        var messages = TimeAgoMessages.Builder().withLocale(Locale.forLanguageTag("es")).build()

        firebaseAuth.currentUser?.let { user ->
            binding.profileName.text = user.displayName
            Glide.with(requireContext()).load(user.photoUrl).centerCrop().into(binding.profilePicture)
            Date().time.let { timestamp ->
                binding.postTimestamp.text = TimeAgo.using(timestamp, messages)
            }
        }

        setupCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startCamera()
    }

    private fun requestPermission() {

        if (CameraUtility.hasCameraPermissions(requireContext())) {
            startCamera()
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept the camera permission to use this app",
                Constants.REQUEST_CODE_CAMERA_PERMISSION,
                Manifest.permission.CAMERA
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept the camera permission to use this app",
                Constants.REQUEST_CODE_CAMERA_PERMISSION,
                Manifest.permission.CAMERA

            )
        }

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun setupCamera(){
        binding.btnTakePicture.setOnClickListener {
            Log.d("NM", "press button")
            takePhoto()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
                    binding.viewFinder.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    uploadPicture()
                }
            }
        )
    }


    private fun uploadPicture(){
        viewModel.insertNewImage(savedUri).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    downloadUrl = result.data
                    Log.d("NM", "uploadedPicture: $downloadUrl")
                    firebaseAuth.currentUser?.let { user ->
                        viewModel.insertNewPost(
                            Post(
                                profilePicture = user.photoUrl.toString(),
                                profileName = user.displayName.toString(),
                                postTimestamp = Timestamp.now(),
                                postImage = downloadUrl
                            )
                        )
                        loadTakedPicture(downloadUrl)
                    }
                }
                is Resource.Failure -> {

                }
            }
        })
    }

    private fun loadTakedPicture(downloadUrl: String){
        Glide.with(requireContext()).load(downloadUrl).centerCrop().into(binding.postImage)
        binding.postImage.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.heartEmpty.visibility = View.GONE
        binding.heartSolid.visibility = View.VISIBLE
        binding.likesCounter.text = "1"
        binding.txtLike.text = "I like it"
    }
}