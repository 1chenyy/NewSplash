package com.chen.newsplash.models.user


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("accepted_tos")
    var acceptedTos: Boolean,
    @SerializedName("allow_messages")
    var allowMessages: Boolean,
    @SerializedName("badge")
    var badge: Badge,
    @SerializedName("bio")
    var bio: String,
    @SerializedName("downloads")
    var downloads: Int,
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("followed_by_user")
    var followedByUser: Boolean,
    @SerializedName("followers_count")
    var followersCount: Int,
    @SerializedName("following_count")
    var followingCount: Int,
    @SerializedName("id")
    var id: String,
    @SerializedName("instagram_username")
    var instagramUsername: String,
    @SerializedName("last_name")
    var lastName: String,
    @SerializedName("links")
    var links: Links,
    @SerializedName("location")
    var location: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("numeric_id")
    var numericId: Int,
    @SerializedName("photos")
    var photos: List<Photo>,
    @SerializedName("portfolio_url")
    var portfolioUrl: String,
    @SerializedName("profile_image")
    var profileImage: ProfileImage,
    @SerializedName("tags")
    var tags: Tags,
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