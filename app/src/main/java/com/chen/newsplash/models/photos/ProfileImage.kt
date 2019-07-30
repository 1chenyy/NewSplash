package com.chen.newsplash.models.photos


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfileImage(
    @SerializedName("large")
    var large: String,
    @SerializedName("medium")
    var medium: String,
    @SerializedName("small")
    var small: String
): Serializable