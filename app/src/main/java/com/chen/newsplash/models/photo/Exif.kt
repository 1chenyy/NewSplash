package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class Exif(
    @SerializedName("aperture")
    var aperture: String,
    @SerializedName("exposure_time")
    var exposureTime: String,
    @SerializedName("focal_length")
    var focalLength: String,
    @SerializedName("iso")
    var iso: Int,
    @SerializedName("make")
    var make: String,
    @SerializedName("model")
    var model: String
)