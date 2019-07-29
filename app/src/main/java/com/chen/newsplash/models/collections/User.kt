package com.chen.newsplash.models.collections


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    var name: String,
    @SerializedName("username")
    var username: String,
    @SerializedName("profile_image")
    var profileImage: ProfileImage
)