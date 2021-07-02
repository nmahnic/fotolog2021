package com.nicomahnic.dadm.fotolog2021.ui.home.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.nicomahnic.dadm.fotolog2021.R
import com.nicomahnic.dadm.fotolog2021.core.Resource
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.data.remote.HomeScreenDataSource
import com.nicomahnic.dadm.fotolog2021.databinding.FragmentHomeScreenBinding
import com.nicomahnic.dadm.fotolog2021.domain.HomeScreenRepoImpl
import com.nicomahnic.dadm.fotolog2021.presentation.HomeScreenViewMModelFactory
import com.nicomahnic.dadm.fotolog2021.presentation.HomeScreenViewModel
import com.nicomahnic.dadm.fotolog2021.ui.home.adapter.HomeScreenAdapter


class HomeScreenFragment :
    Fragment(R.layout.fragment_home_screen),
    HomeScreenAdapter.OnLikeClickListener
{

    private val firebaseAuth by lazy { FirebaseAuth.getInstance()}

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by viewModels<HomeScreenViewModel>{
        HomeScreenViewMModelFactory(
            HomeScreenRepoImpl(
                HomeScreenDataSource()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeScreenBinding.bind(view)

        viewModel.fetchLatestPosts().observe(viewLifecycleOwner,  Observer { result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvHome.adapter = HomeScreenAdapter(result.data, this)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),"Ocurrio un error: ${result.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onLikeClick(position: Int, post: Post, isChecked: Boolean) {
        Log.d("NM","Post: ${post.profileName} $position is checked $isChecked")

        firebaseAuth.currentUser?.let { user ->
            if(!isChecked) {
                viewModel.updatePost(post.postID, post.postLikes.minus(user.displayName.toString()))
            }else {
                viewModel.updatePost(post.postID,post.postLikes.plus(user.displayName.toString()))
            }
        }

        viewModel.fetchLatestPosts().observe(viewLifecycleOwner,  Observer { result ->
            when(result){
                is Resource.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvHome.adapter = HomeScreenAdapter(result.data, this)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),"Ocurrio un error: ${result.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}