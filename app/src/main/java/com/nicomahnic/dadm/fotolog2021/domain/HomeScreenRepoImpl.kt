package com.nicomahnic.dadm.fotolog2021.domain

import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.data.remote.HomeScreenDataSource

class HomeScreenRepoImpl(private val dataSource: HomeScreenDataSource) : HomeScreenRepo{

    override suspend fun getLatestPosts(): Resource<List<Post>> = dataSource.getLastestPosts()

    override suspend fun insertNewPost(post: Post) {
        dataSource.insertNewPost(post)
    }

}