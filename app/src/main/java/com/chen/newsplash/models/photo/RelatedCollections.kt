package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class RelatedCollections(
    @SerializedName("results")
    var results: List<Result>,
    @SerializedName("total")
    var total: Int,
    @SerializedName("type")
    var type: String
)