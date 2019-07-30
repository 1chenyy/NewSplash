package com.chen.newsplash.models.photo


import com.google.gson.annotations.SerializedName

data class UserX(
    @SerializedName("accepted_tos")
    var acceptedTos: Boolean,
    @SerializedName("bio")
    var bio: Any?,
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("instagram_username")
    var instagramUsername: Any?,
    @SerializedName("last_name")
    var lastName: String,
    @SerializedName("links")
    var links: LinksXXX,
    @SerializedName("location")
    var location: Any?,
    @SerializedName("name")
    var name: String,
    @SerializedName("portfolio_url")
    var portfolioUrl: Any?,
    @SerializedName("profile_image")
    var profileImage: ProfileImageX,
    @SerializedName("total_collections")
    var totalCollections: Int,
    @SerializedName("total_likes")
    var totalLikes: Int,
    @SerializedName("total_photos")
    var totalPhotos: Int,
    @SerializedName("twitter_username")
    var twitterUsername: Any?,
    @SerializedName("updated_at")
    var updatedAt: String,
    @SerializedName("username")
    var username: String
)