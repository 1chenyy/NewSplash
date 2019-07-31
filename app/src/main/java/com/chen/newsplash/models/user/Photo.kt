package com.chen.newsplash.models.user


import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("id")
    var id: String,
    @SerializedName("urls")
    var urls: Urls
)