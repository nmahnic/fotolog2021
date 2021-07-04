package com.nicomahnic.dadm.fotolog2021.domain

import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.data.remote.PostDataSource

class PostRepoImpl(private val dataSource: PostDataSource) : PostRepo{

    override suspend fun getLatestPosts(): Resource<List<Post>> = dataSource.getLastestPosts()

    override suspend fun insertNewPost(post: Post) {
        dataSource.insertNewPost(post)
    }

    override suspend fun updatePost(postID: String, likes: List<String>) {
        dataSource.updatePost(postID, likes)
    }
}