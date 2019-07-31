package com.chen.newsplash.models.user


import com.google.gson.annotations.SerializedName

data class Badge(
    @SerializedName("link")
    var link: String,
    @SerializedName("primary")
    var primary: Boolean,
    @SerializedName("slug")
    var slug: String,
    @SerializedName("title")
    var title: String
)