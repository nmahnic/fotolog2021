package com.nicomahnic.dadm.fotolog2021.ui.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.nicomahnic.dadm.fotolog2021.core.BaseViewHolder
import com.nicomahnic.dadm.fotolog2021.data.model.Post
import com.nicomahnic.dadm.fotolog2021.databinding.PostItemViewBinding
import java.util.*


class HomeScreenAdapter(
        private val postList: List<Post>,
        private val itemLikeClickListener: OnLikeClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance()}

    interface OnLikeClickListener {
        fun onLikeClick(position: Int, post: Post, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = PostItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

        var LocaleBylanguageTag: Locale = Locale.forLanguageTag("es")
        var messages = TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build()

        override fun bind(item: Post, position: Int) {
            Glide.with(context).load(item.profilePicture).centerCrop().into(binding.profilePicture)
            Glide.with(context).load(item.postImage).centerCrop().into(binding.postImage)
            binding.profileName.text = item.profileName
            item.postTimestamp?.toDate()?.time?.let { timestamp ->
                binding.postTimestamp.text = TimeAgo.using(timestamp,messages)
            }
            binding.likesCounter.text = item.postLikes.size.toString()
            firebaseAuth.currentUser?.let { user ->
                binding.cbPostLike.isChecked = user.displayName in item.postLikes
            }
            binding.cbPostLike.setOnCheckedChangeListener { buttonView, isChecked ->
                if(!isChecked)
                    binding.likesCounter.text = (binding.likesCounter.text.toString().toInt() - 1).toString()
                else
                    binding.likesCounter.text = (binding.likesCounter.text.toString().toInt() + 1).toString()
                itemLikeClickListener.onLikeClick(position, item, isChecked)
            }
        }
    }
}