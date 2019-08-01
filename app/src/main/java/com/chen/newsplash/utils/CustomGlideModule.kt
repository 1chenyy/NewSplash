package com.chen.newsplash.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
@GlideModule
class CustomGlideModule : AppGlideModule(){

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        var size = 1024*1024*200
        builder.setDiskCache(InternalCacheDiskCacheFactory(context,"glidecache",size.toLong()))
    }
}