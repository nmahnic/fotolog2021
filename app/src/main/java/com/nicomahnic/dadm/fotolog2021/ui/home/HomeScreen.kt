package com.nicomahnic.dadm.fotolog2021.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.nicomahnic.dadm.fotolog2021.R
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.databinding.FragmentHomeScreenBinding
import com.nicomahnic.dadm.fotolog2021.ui.home.adapter.HomeScreenAdapter


class HomeScreen : Fragment(R.layout.fragment_home_screen) {

    private lateinit var binding: FragmentHomeScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeScreenBinding.bind(view)

        //Consultar info al iniciar la app
        val db = FirebaseFirestore.getInstance()
        db.collection("ciudades").document("LA").get()
            .addOnSuccessListener { document ->
                document?.let {
                    val ciudad = document.toObject(Ciudad::class.java)
                    Log.d("NM",document.data.toString())
                    val color = document.getString("color")
                    val population = document.getLong("population")
                    Log.d("NM",color.toString())
                    Log.d("NM",population.toString())
                    Log.d("NM",ciudad.toString())
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
                        Log.d("NM",ciudad.toString())
                    }
                }
                error?.let{
                    Log.e("FirebaseError", error.toString())
                }
            }

        //Insertar informacion
        db.collection("ciudades").document("NY")
            .set(Ciudad(20,"red"))
            .addOnSuccessListener {
                Log.e("NM", "Se guardo en db")
            }
            .addOnFailureListener { error ->
                Log.e("FirebaseError", error.toString())
            }

        val postList = listOf(
            Post(nicoImage,"nico",Timestamp.now(),nicoImage),
            Post(mcoloresImage,"luli",Timestamp.now(),mcoloresImage),
            Post(topitoTerremotoImage,"luli",Timestamp.now(),topitoTerremotoImage)
        )
        binding.rvHome.adapter = HomeScreenAdapter(postList)
    }

    val nicoImage = "https://yt3.ggpht.com/ytc/AAUvwnigE2XbXLQMkJNuNnJEYvdUixTSMUQWTT_qLoxh6tQ=s900-c-k-c0x00ffffff-no-rj"
    val mcoloresImage = "https://i.pinimg.com/564x/72/15/fc/7215fc1ce849f06fb23264855a353177.jpg"
    val topitoTerremotoImage = "https://i.pinimg.com/564x/f6/d1/a2/f6d1a299a6783c3922f425a31f78e07c.jpg"

}

data class Ciudad(val population: Int = 0, val color: String = "")