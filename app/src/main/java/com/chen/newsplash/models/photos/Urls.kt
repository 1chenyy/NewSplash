package com.chen.newsplash.models.photos


import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Urls(
    @SerializedName("full")
    var full: String,
    @SerializedName("raw")
    var raw: String,
    @SerializedName("regular")
    var regular: String,
    @ColumnInfo(name = "urlsmall")
    @SerializedName("small")
    var small: String,
    @SerializedName("thumb")
    var thumb: String
) : Serializable