package com.nicomahnic.dadm.fotolog2021.domain

import android.net.Uri
import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post

interface PostRepo {
    suspend fun getLatestPosts(): Resource<List<Post>>
    suspend fun insertNewPost(post: Post)
    suspend fun insertNewImage(uri: Uri): Resource<String>
    suspend fun updatePost(postID: String, likes: List<String>)
}