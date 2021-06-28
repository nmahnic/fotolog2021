package com.nicomahnic.dadm.fotolog2021.domain

import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post

interface HomeScreenRepo {
    suspend fun getLatestPosts(): Resource<List<Post>>
    suspend fun insertNewPost(post: Post)
}