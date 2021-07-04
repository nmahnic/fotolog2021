package com.nicomahnic.dadm.fotolog2021.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class PostDataSource {

    suspend fun getLastestPosts(): Resource<List<Post>>{
        val postList = mutableListOf<Post>()

        withContext(Dispatchers.IO){
            val querySnapshot = FirebaseFirestore
                    .getInstance()
                    .collection("posts")
                    .orderBy("postTimestamp",Query.Direction.DESCENDING)
                    .get()
                    .await()
            for(post in querySnapshot.documents){
                post.toObject(Post::class.java)?.let { postList.add(it) }
            }
        }
        return Resource.Success(postList)
    }

    suspend fun insertNewPost(post: Post){
        Log.d("NM", "Post: ${post.toString()}")
        val postID = UUID.randomUUID().toString()
        post.postID = postID
        FirebaseFirestore.getInstance().collection("posts")
            .document(postID).set(post)
            .addOnSuccessListener {
                Log.d("NM","Insert Success")
            }
            .addOnFailureListener {
                Log.d("NM", "Insert Failure")
            }
            .await()
    }

    suspend fun updatePost(postID: String, likes: List<String>){
        FirebaseFirestore.getInstance().collection("posts")
            .document(postID)
            .update(mapOf("postLikes" to likes))
            .await()
    }
}