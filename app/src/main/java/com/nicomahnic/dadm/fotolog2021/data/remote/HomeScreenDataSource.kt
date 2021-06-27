package com.nicomahnic.dadm.fotolog2021.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import kotlinx.coroutines.tasks.await

class HomeScreenDataSource {

    suspend fun getLastestPosts(): Resource<List<Post>>{
        val postList = mutableListOf<Post>()
        val querySnapshot = FirebaseFirestore.getInstance().collection("posts").get().await()
        for(post in querySnapshot.documents){
            post.toObject(Post::class.java)?.let { postList.add(it) }
        }
        return Resource.Success(postList)
    }
}