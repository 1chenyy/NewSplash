package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class Sponsorship(
    @SerializedName("impressions_id")
    var impressionsId: String,
    @SerializedName("sponsor")
    var sponsor: Sponsor,
    @SerializedName("tagline")
    var tagline: String
)