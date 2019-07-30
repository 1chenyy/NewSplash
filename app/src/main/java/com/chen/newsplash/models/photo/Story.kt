package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class Story(
    @SerializedName("description")
    var description: Any?,
    @SerializedName("title")
    var title: Any?
)