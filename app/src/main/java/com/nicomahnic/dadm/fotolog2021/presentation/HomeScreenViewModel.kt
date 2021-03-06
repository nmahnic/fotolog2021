package com.nicomahnic.dadm.fotolog2021.presentation

import android.net.Uri
import androidx.lifecycle.*
import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.domain.PostRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val repo: PostRepo): ViewModel() {

    fun fetchLatestPosts() = liveData(viewModelScope.coroutineContext + Dispatchers.Main){
        emit(Resource.Loading())
        try {
            emit(repo.getLatestPosts())
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun insertNewPost(post: Post) {
        viewModelScope.launch {
            repo.insertNewPost(post)
        }
    }

    fun insertNewImage(uri: Uri) = liveData(viewModelScope.coroutineContext + Dispatchers.Main){
        emit(Resource.Loading())
        try {
            emit(repo.insertNewImage(uri))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun updatePost(postID: String, likes: List<String>) {
        viewModelScope.launch {
            repo.updatePost(postID, likes)
        }
    }
}

class HomeScreenViewMModelFactory(private val repo: PostRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PostRepo::class.java).newInstance(repo)
    }

}