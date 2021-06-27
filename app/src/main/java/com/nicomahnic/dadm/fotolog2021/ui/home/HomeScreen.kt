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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nicomahnic.dadm.fotolog2021.R
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.databinding.FragmentHomeScreenBinding
import com.nicomahnic.dadm.fotolog2021.ui.home.adapter.HomeScreenAdapter
import java.io.ByteArrayOutputStream
import java.util.*


class HomeScreen : Fragment(R.layout.fragment_home_screen) {

    private lateinit var binding: FragmentHomeScreenBinding
    private var downloadUrl = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeScreenBinding.bind(view)

        setupFireStore()
        setupHarcodedAdaptaer()
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

    private fun setupHarcodedAdaptaer(){
        val postList = listOf(
            Post(nicoImage, "nico", Timestamp.now(), nicoImage),
            Post(mcoloresImage, "luli", Timestamp.now(), mcoloresImage),
            Post(topitoTerremotoImage, "luli", Timestamp.now(), topitoTerremotoImage)
        )
        binding.rvHome.adapter = HomeScreenAdapter(postList)
    }

    private fun setupFireStore(){
        //Consultar info al iniciar la app
        val db = FirebaseFirestore.getInstance()
        db.collection("ciudades").document("LA").get()
            .addOnSuccessListener { document ->
                document?.let {
                    val ciudad = document.toObject(Ciudad::class.java)
                    Log.d("NM", document.data.toString())
                    val color = document.getString("color")
                    val population = document.getLong("population")
                    Log.d("NM", color.toString())
                    Log.d("NM", population.toString())
                    Log.d("NM", ciudad.toString())
                }
            }
            .addOnFailureListener{ error ->
                Log.e("FirebaseError", error.toString())
            }

        //Consultar info listener
        db.collection("ciudades").document("NY")
            .addSnapshotListener { value, error ->
                value?.let{ document ->
                    document?.let {
                        val ciudad = document.toObject(Ciudad::class.java)
                        Log.d("NM", ciudad.toString())
                    }
                }
                error?.let{
                    Log.e("FirebaseError", error.toString())
                }
            }

        //Insertar informacion
        db.collection("ciudades").document("NY")
            .set(Ciudad(20, "red"))
            .addOnSuccessListener {
                Log.e("NM", "Se guardo en db")
            }
            .addOnFailureListener { error ->
                Log.e("FirebaseError", error.toString())
            }
    }

    val nicoImage = "https://yt3.ggpht.com/ytc/AAUvwnigE2XbXLQMkJNuNnJEYvdUixTSMUQWTT_qLoxh6tQ=s900-c-k-c0x00ffffff-no-rj"
    val mcoloresImage = "https://i.pinimg.com/564x/72/15/fc/7215fc1ce849f06fb23264855a353177.jpg"
    val topitoTerremotoImage = "https://i.pinimg.com/564x/f6/d1/a2/f6d1a299a6783c3922f425a31f78e07c.jpg"

}

data class Ciudad(val population: Int = 0, val color: String = "", val pc: Int = 0, val imageUrl: String = "")