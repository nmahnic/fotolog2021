package com.nicomahnic.dadm.fotolog2021.ui.home

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nicomahnic.dadm.fotolog2021.R
import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.data.remote.HomeScreenDataSource
import com.nicomahnic.dadm.fotolog2021.databinding.FragmentHomeScreenBinding
import com.nicomahnic.dadm.fotolog2021.domain.HomeScreenRepoImpl
import com.nicomahnic.dadm.fotolog2021.presentation.HomeScreenViewMModelFactory
import com.nicomahnic.dadm.fotolog2021.presentation.HomeScreenViewModel
import com.nicomahnic.dadm.fotolog2021.ui.home.adapter.HomeScreenAdapter
import java.io.ByteArrayOutputStream
import java.util.*


class HomeScreen : Fragment(R.layout.fragment_home_screen) {

    private lateinit var binding: FragmentHomeScreenBinding
    private var downloadUrl = ""
    private val viewModel by viewModels<HomeScreenViewModel>{
        HomeScreenViewMModelFactory(
            HomeScreenRepoImpl(
                HomeScreenDataSource()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeScreenBinding.bind(view)

        viewModel.fetchLatestPosts().observe(viewLifecycleOwner,  { result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvHome.adapter = HomeScreenAdapter(result.data)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),"Ocurrio un error: ${result.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        })


        setupCamera()
    }




    private fun dispatchTakePictureIntent(){
        val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            resultLauncher.launch(takePicIntent)
        }catch (e: ActivityNotFoundException){
            Log.e("NM", "Picture Error: $e")
        }
    }

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadPicture(imageBitmap)
            Log.d("NM", "RETURN FROM INTENT $downloadUrl")
        }
    }

    private fun uploadPicture(bitmap: Bitmap){
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("imagenes/${UUID.randomUUID()}.jpeg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imagesRef.putBytes(data)

        uploadTask.continueWithTask { task ->
            if(!task.isSuccessful){
                task.exception?.let { exception ->
                    throw exception
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if(task.isSuccessful){
                downloadUrl = task.result.toString()
                Log.d("NM", "uploadedPicture: $downloadUrl")
                FirebaseFirestore.getInstance().collection("ciudades")
                    .document("LA")
                    .update(mapOf("imageUrl" to downloadUrl))
                Glide.with(requireContext()).load(Uri.parse(downloadUrl)).centerCrop().into(binding.imageView)
            }
        }
    }

    private fun setupCamera(){
        binding.btnTakePicture.setOnClickListener {
            Log.d("NM", "press button")
            dispatchTakePictureIntent()
        }
    }




}

data class Ciudad(val population: Int = 0, val color: String = "", val pc: Int = 0, val imageUrl: String = "")