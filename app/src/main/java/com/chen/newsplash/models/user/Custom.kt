package com.chen.newsplash.models.user


import com.google.gson.annotations.SerializedName

data class Custom(
    @SerializedName("title")
    var title: String
)