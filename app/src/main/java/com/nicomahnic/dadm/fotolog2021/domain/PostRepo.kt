package com.nicomahnic.dadm.fotolog2021.domain

import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post

interface PostRepo {
    suspend fun getLatestPosts(): Resource<List<Post>>
    suspend fun insertNewPost(post: Post)
    suspend fun updatePost(postID: String, likes: List<String>)
}