package com.nicomahnic.dadm.fotolog2021.data.model

import com.google.firebase.Timestamp

data class Post(
    val profilePicture: String = "",
    val profileName: String = "",
    val postTimestamp: Timestamp? = null,
    val postImage: String = "",
    val postLikes: MutableList<String> = mutableListOf(),
    var postID: String = ""
)