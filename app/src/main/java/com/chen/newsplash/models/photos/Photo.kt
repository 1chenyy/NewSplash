package com.chen.newsplash.models.photos


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Photo(
    @SerializedName("color")
    var color: String,


    @SerializedName("height")
    var height: Int,
    @SerializedName("width")
    var width: Int,

    @SerializedName("id")
    var id: String,

    @SerializedName("urls")
    var urls: Urls,
    @SerializedName("user")
    var user: User

) : Serializable