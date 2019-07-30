package com.chen.newsplash.models.photos


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("username")
    var username: String,

    @SerializedName("profile_image")
    var profileImage: ProfileImage,

    @SerializedName("name")
    var name: String
) : Serializable