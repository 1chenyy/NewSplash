package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class CoverPhoto(
    @SerializedName("alt_description")
    var altDescription: String,
    @SerializedName("categories")
    var categories: List<Any>,
    @SerializedName("color")
    var color: String,
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("current_user_collections")
    var currentUserCollections: List<Any>,
    @SerializedName("description")
    var description: Any?,
    @SerializedName("height")
    var height: Int,
    @SerializedName("id")
    var id: String,
    @SerializedName("liked_by_user")
    var likedByUser: Boolean,
    @SerializedName("likes")
    var likes: Int,
    @SerializedName("links")
    var links: LinksXXXX,
    @SerializedName("sponsored")
    var sponsored: Boolean,
    @SerializedName("sponsored_by")
    var sponsoredBy: Any?,
    @SerializedName("sponsored_impressions_id")
    var sponsoredImpressionsId: Any?,
    @SerializedName("updated_at")
    var updatedAt: String,
    @SerializedName("urls")
    var urls: UrlsX,
    @SerializedName("user")
    var user: UserXX,
    @SerializedName("width")
    var width: Int
)