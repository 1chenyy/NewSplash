package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("accepted_tos")
    var acceptedTos: Boolean,
    @SerializedName("bio")
    var bio: String,
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("instagram_username")
    var instagramUsername: String,
    @SerializedName("last_name")
    var lastName: String,
    @SerializedName("links")
    var links: LinksX,
    @SerializedName("location")
    var location: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("portfolio_url")
    var portfolioUrl: String,
    @SerializedName("profile_image")
    var profileImage: ProfileImage,
    @SerializedName("total_collections")
    var totalCollections: Int,
    @SerializedName("total_likes")
    var totalLikes: Int,
    @SerializedName("total_photos")
    var totalPhotos: Int,
    @SerializedName("twitter_username")
    var twitterUsername: String,
    @SerializedName("updated_at")
    var updatedAt: String,
    @SerializedName("username")
    var username: String
)