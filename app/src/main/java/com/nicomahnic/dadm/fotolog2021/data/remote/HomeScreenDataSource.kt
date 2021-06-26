package com.nicomahnic.dadm.fotolog2021.data.remote

import com.google.firebase.Timestamp
import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post

class HomeScreenDataSource {

    suspend fun getLastestPosts(): Resource<List<Post>>{
        return Resource.Loading()
    }
}