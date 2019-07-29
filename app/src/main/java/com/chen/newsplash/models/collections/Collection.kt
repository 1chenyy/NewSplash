package com.chen.newsplash.models.collections


import com.google.gson.annotations.SerializedName

data class Collection(
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var title: String,
    @SerializedName("total_photos")
    var totalPhotos: Int,
    @SerializedName("user")
    var user: User,
    @SerializedName("tags")
    var tags: List<Tag>,
    @SerializedName("cover_photo")
    var coverPhoto: CoverPhoto
)