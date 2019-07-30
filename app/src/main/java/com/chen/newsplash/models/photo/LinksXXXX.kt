package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class LinksXXXX(
    @SerializedName("followers")
    var followers: String,
    @SerializedName("following")
    var following: String,
    @SerializedName("html")
    var html: String,
    @SerializedName("likes")
    var likes: String,
    @SerializedName("photos")
    var photos: String,
    @SerializedName("portfolio")
    var portfolio: String,
    @SerializedName("self")
    var self: String
)