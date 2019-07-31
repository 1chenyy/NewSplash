package com.chen.newsplash.net

import com.chen.newsplash.models.collections.Collection
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.models.search.SearchResult
import com.chen.newsplash.models.user.User
import io.reactivex.Maybe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashAPI {
    @GET("photos")
    fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("order_by") order_by: String
    ): Maybe<List<Photo>>

    @GET("photos/curated")
    fun getCurated(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("order_by") order_by: String
    ): Maybe<List<Photo>>

    @GET("collections")
    fun getCollection(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Maybe<List<Collection>>

    @GET("collections/featured")
    fun getFeatured(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Maybe<List<Collection>>

    @GET("photos/{id}")
    fun getPhoto(@Path("id") id: String): Maybe<com.chen.newsplash.models.photo.Photo>

    @GET("collections/{id}/photos")
    fun getPhotosOfCollection(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Maybe<List<Photo>>

    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Maybe<User>

    @GET("/users/{username}/photos")
    fun getPhotosOfUser(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Maybe<List<Photo>>

    @GET("/users/{username}/likes")
    fun getLikePhotosOfUser(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Maybe<List<Photo>>

    @GET("/users/{username}/collections")
    fun getCollectionsOfUser(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Maybe<List<Collection>>

    @GET("/search/photos")
    fun searchPhotosNoFilter(
        @Query("query")query:String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Maybe<SearchResult>

    @GET("/search/photos")
    fun searchPhotosWithFilter(
        @Query("query")query:String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("orientation")orientation:String
    ): Maybe<SearchResult>
}