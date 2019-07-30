package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class PreviewPhoto(
    @SerializedName("id")
    var id: String,
    @SerializedName("urls")
    var urls: Urls
)