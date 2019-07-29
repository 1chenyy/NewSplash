package com.chen.newsplash.net

import com.chen.newsplash.utils.Const
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitManager {
    companion object{
        lateinit var unsplashAPI:UnsplashAPI

        fun isInit()=::unsplashAPI.isInitialized

        fun getAPI():UnsplashAPI {
            if (!isInit()){
                var builder = OkHttpClient.Builder()
                builder.writeTimeout(30,TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS)
                    .connectTimeout(15,TimeUnit.SECONDS)
                var client = builder.addInterceptor {
                    var request = it.request()
                        .newBuilder()
                        .addHeader("Authorization","Client-ID ece4fa5efbe76bcec26bf982d7b7ef8b27bd80d832dbe8a40bd954448fc4320a")
                        .build();
                    it.proceed(request)
                }.build()
                unsplashAPI = Retrofit.Builder()
                    .baseUrl(Const.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
                    .create(UnsplashAPI::class.java)
            }
            return unsplashAPI
        }
    }
}