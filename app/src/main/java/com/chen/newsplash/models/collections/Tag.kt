package com.chen.newsplash.models.collections


import com.google.gson.annotations.SerializedName

data class Tag(
    @SerializedName("title")
    var title: String
)