package com.chen.newsplash.net

import com.chen.newsplash.models.photos.Photo
import io.reactivex.Maybe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashAPI {
    @GET("photos")
    fun getPhotos(@Query("page")page:Int,
                  @Query("per_page")per_page:Int,
                  @Query("order_by")order_by:String):Maybe<List<Photo>>
    @GET("photos/curated")
    fun getCurated(@Query("page")page:Int,
                   @Query("per_page")per_page:Int,
                   @Query("order_by")order_by:String):Maybe<List<Photo>>
}