package com.chen.newsplash

import android.app.Application
import android.content.Context
import android.os.Handler
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.github.piasy.biv.view.BigImageView
import com.tencent.mmkv.MMKV

class NewSplash : Application() {
    companion object{
        var context: Context? = null
            private set
        var mainThread:Int = 0
            private set
        var handler: Handler? = null
            private set
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        mainThread = android.os.Process.myTid()
        handler = Handler()
        MMKV.initialize(applicationContext)
        BigImageViewer.initialize(GlideImageLoader.with(context))
    }


}