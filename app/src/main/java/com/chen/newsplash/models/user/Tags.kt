package com.chen.newsplash.models.user


import com.google.gson.annotations.SerializedName

data class Tags(
    @SerializedName("aggregated")
    var aggregated: List<Aggregated>,
    @SerializedName("custom")
    var custom: List<Custom>
)