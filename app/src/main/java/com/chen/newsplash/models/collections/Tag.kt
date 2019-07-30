package com.chen.newsplash.models.collections


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tag(
    @SerializedName("title")
    var title: String
): Serializable