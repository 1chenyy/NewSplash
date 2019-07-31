package com.chen.newsplash.models.user


import com.google.gson.annotations.SerializedName

data class Aggregated(
    @SerializedName("title")
    var title: String
)