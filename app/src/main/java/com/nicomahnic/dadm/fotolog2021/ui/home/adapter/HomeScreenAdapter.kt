package com.nicomahnic.dadm.fotolog2021.ui.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nicomahnic.dadm.fotolog2021.core.BaseViewHolder
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.databinding.PostItemViewBinding

class HomeScreenAdapter(
    private val postList: List<Post>,
    private val itemLikeClickListener: OnLikeClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnLikeClickListener {
        fun onLikeClick(position: Int, post: Post, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = PostItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomeScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is HomeScreenViewHolder -> holder.bind(postList[position], position)
        }
    }

    override fun getItemCount(): Int = postList.size

    private inner class HomeScreenViewHolder(
        val binding: PostItemViewBinding,
        val context: Context
    ): BaseViewHolder<Post>(binding.root) {

        override fun bind(item: Post, position: Int) {
            Glide.with(context).load(item.profilePicture).centerCrop().into(binding.profilePicture)
            Glide.with(context).load(item.postImage).centerCrop().into(binding.postImage)
            binding.profileName.text = item.profileName
            binding.postTimestamp.text = "Hace 2 horas"//item.postTimestamp.toString()
            binding.cbPostLike.setOnCheckedChangeListener { buttonView, isChecked ->
                itemLikeClickListener.onLikeClick(position, item, isChecked)
            }
        }
    }
}