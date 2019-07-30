package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class LinksXX(
    @SerializedName("html")
    var html: String,
    @SerializedName("photos")
    var photos: String,
    @SerializedName("related")
    var related: String,
    @SerializedName("self")
    var self: String
)