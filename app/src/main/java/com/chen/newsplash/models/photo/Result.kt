package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("private")
    var `private`: Boolean,
    @SerializedName("cover_photo")
    var coverPhoto: CoverPhoto,
    @SerializedName("curated")
    var curated: Boolean,
    @SerializedName("description")
    var description: Any?,
    @SerializedName("featured")
    var featured: Boolean,
    @SerializedName("id")
    var id: Int,
    @SerializedName("links")
    var links: LinksXX,
    @SerializedName("preview_photos")
    var previewPhotos: List<PreviewPhoto>,
    @SerializedName("published_at")
    var publishedAt: String,
    @SerializedName("share_key")
    var shareKey: String,
    @SerializedName("tags")
    var tags: List<TagX>,
    @SerializedName("title")
    var title: String,
    @SerializedName("total_photos")
    var totalPhotos: Int,
    @SerializedName("updated_at")
    var updatedAt: String,
    @SerializedName("user")
    var user: UserX
)