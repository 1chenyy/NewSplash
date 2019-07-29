package com.chen.newsplash.models.collections


import com.google.gson.annotations.SerializedName

data class ProfileImage(
    @SerializedName("large")
    var large: String,
    @SerializedName("medium")
    var medium: String,
    @SerializedName("small")
    var small: String
)