package com.nicomahnic.dadm.fotolog2021.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import kotlinx.coroutines.tasks.await
import java.util.*

class HomeScreenDataSource {

    suspend fun getLastestPosts(): Resource<List<Post>>{
        val postList = mutableListOf<Post>()
        val querySnapshot = FirebaseFirestore.getInstance().collection("posts").get().await()
        for(post in querySnapshot.documents){
            post.toObject(Post::class.java)?.let { postList.add(it) }
        }
        return Resource.Success(postList)
    }

    suspend fun insertNewPost(post: Post){
        Log.d("NM", "Post: ${post.toString()}")
        FirebaseFirestore.getInstance().collection("posts")
            .document(UUID.randomUUID().toString()).set(post)
            .addOnSuccessListener {
                Log.d("NM","Insert Success")
            }
            .addOnFailureListener {
                Log.d("NM", "Insert Failure")
            }
            .await()
    }
}