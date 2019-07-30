package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class UrlsXX(
    @SerializedName("full")
    var full: String,
    @SerializedName("raw")
    var raw: String,
    @SerializedName("regular")
    var regular: String,
    @SerializedName("small")
    var small: String,
    @SerializedName("thumb")
    var thumb: String
)