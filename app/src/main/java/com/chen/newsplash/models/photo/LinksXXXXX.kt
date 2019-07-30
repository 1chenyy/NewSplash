package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class LinksXXXXX(
    @SerializedName("download")
    var download: String,
    @SerializedName("download_location")
    var downloadLocation: String,
    @SerializedName("html")
    var html: String,
    @SerializedName("self")
    var self: String
)