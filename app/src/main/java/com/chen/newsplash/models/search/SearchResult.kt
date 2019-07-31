package com.chen.newsplash.models.search


import com.chen.newsplash.models.photos.Photo
import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("results")
    var results: List<Photo>,
    @SerializedName("total")
    var total: Int,
    @SerializedName("total_pages")
    var totalPages: Int
)