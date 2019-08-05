package com.chen.newsplash.autowallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.net.RetrofitManager
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import com.tencent.mmkv.MMKV
import io.reactivex.Maybe
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.util.concurrent.TimeUnit

class AutoWallpaperWorker(var context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    var shape = -1
    var clip = -1
    var screen = -1
    override fun doWork(): Result {
        LogUtil.d(this.javaClass, "开始更换壁纸")
        shape = inputData.getInt(Const.AUTO_SHAPE, 1)
        clip = inputData.getInt(Const.AUTO_CLIP, 1)
        screen = inputData.getInt(Const.AUTO_SCREEN, 0)

        var randomResult: Call<Photo>
        if (shape == 0) {
            randomResult = RetrofitManager.getAPI().getRandomPhotoNoFilter()
        } else {
            randomResult = RetrofitManager.getAPI().getRandomPhotoWithFilter(Const.LIST_ORIENTATION[shape])
        }

        return handleCall(randomResult)
    }

    fun handleCall(call:Call<Photo>):Result{
        var input:InputStream? = null
        var out:FileOutputStream? = null
        var result = Result.success()
        try {
            var response = call.execute()
            if (response.isSuccessful){
                var bean = response.body()!!
                LogUtil.d(this.javaClass, "随机获取图片成功${bean.id}")

                var f = File(Const.DIR_WALLPAPER, "wallpaper.jpg")
                var dir = Const.DIR_WALLPAPER
                if (!dir.exists())
                    dir.mkdirs()
                if (f.exists())
                    f.delete()
                var exceptURL = exceptWallpaperURL(bean.urls.regular, bean.width, bean.height)
                LogUtil.d(this.javaClass, "开始下载壁纸${exceptURL}")
                var request = Request.Builder()
                    .url(exceptURL).build();
                var client = OkHttpClient.Builder()
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build()
                var response = client.newCall(request).execute()
                input = response.body()?.byteStream()!!
                out = FileOutputStream(f)
                var len = 0
                var buf = ByteArray(2048)
                while (true){
                    len = input!!.read(buf)
                    if (len!=-1){
                        out.write(buf,0,len)
                    }else{
                        break
                    }
                }
                out.flush()
                if (f.exists()){
                    LogUtil.d(this.javaClass, "开始设置壁纸")
                    var wm = WallpaperManager.getInstance(context)
                    var bit = BitmapFactory.decodeFile(f.absolutePath)
                    var w = bit.width
                    var h = bit.height
                    var l = 0
                    var t = 0
                    var r = w
                    var b = h
                    if (w>Utils.SCREEN_WIDTH) {
                        if (clip == 0){
                            l = 0
                            r = Utils.SCREEN_WIDTH
                        }else if(clip == 1){
                            l = (w - Utils.SCREEN_WIDTH) / 2
                            r = l+Utils.SCREEN_WIDTH
                        }else if(clip == 2){
                            l = w-Utils.SCREEN_WIDTH
                            r = w
                        }
                    }
                    if (h>Utils.SCREEN_HEIGHT) {
                        if (clip == 0){
                            t = 0
                            b = Utils.SCREEN_HEIGHT
                        }else if(clip == 1){
                            t = (h - Utils.SCREEN_HEIGHT) / 2
                            b = t + Utils.SCREEN_HEIGHT
                        }else{
                            t = h - Utils.SCREEN_HEIGHT
                            b = h
                        }

                    }
                    if (Build.VERSION.SDK_INT>=24) {
                        var flag :Int
                        if (screen == 0){
                            flag = WallpaperManager.FLAG_SYSTEM
                        }else if(screen == 1){
                            flag = WallpaperManager.FLAG_LOCK
                        }else{
                            flag = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                        }
                        wm.setBitmap(bit, Rect(l, t, r, b), false, flag)
                    }else
                        wm.setBitmap(bit)
                }else{
                    throw Exception("file is not exist")
                }
            }else{
                throw Exception("get random photo failed")
            }

        }catch (e:Exception){
            LogUtil.d(this.javaClass, "自动设置壁纸出错${e.message}，尝试自动重试")
            var mkv = MMKV.defaultMMKV()
            var retry = mkv.decodeInt(Const.AUTO_RETRY,0)
            if (retry==5){
                LogUtil.d(this.javaClass, "超过最大重试次数")
                result = Result.success()
            }else{
                retry++
                mkv.encode(Const.AUTO_RETRY,retry)
                result = Result.retry()
            }
        }finally {
            Utils.closeIO(input)
            Utils.closeIO(out)
        }
        if(result is Result.Success) {
            LogUtil.d(this.javaClass, "设置成功，重试次数归0")
            MMKV.defaultMMKV().encode(Const.AUTO_RETRY, 0)
        }
        return result
    }

    fun handleSuuceed(bean: Photo) {
        LogUtil.d(this.javaClass, "随机获取图片成功${bean.id}")
        var input:InputStream? = null
        var out:FileOutputStream? = null
        var err = false
        var f = File(Const.DIR_WALLPAPER, "wallpaper.jpg")
        try {
            var dir = Const.DIR_WALLPAPER
            if (!dir.exists())
                dir.mkdirs()
            if (f.exists())
                f.delete()
            var exceptURL = exceptWallpaperURL(bean.urls.regular, bean.width, bean.height)
            LogUtil.d(this.javaClass, "开始下载壁纸${exceptURL}")
            var request = Request.Builder()
                .url(exceptURL).build();
            var client = OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()
            var response = client.newCall(request).execute()
            input = response.body()?.byteStream()!!
            out = FileOutputStream(f)
            var len = 0
            var buf = ByteArray(2048)
            while (true){
                len = input!!.read(buf)
                if (len!=-1){
                    out.write(buf,0,len)
                }else{
                    break
                }
            }
            out.flush()
        }catch (e:Exception){
            LogUtil.d(this.javaClass, "下载壁纸：${e.message}")
            err = true
        }finally {
            Utils.closeIO(input)
            Utils.closeIO(out)
        }
        if (!err && f.exists()){
            LogUtil.d(this.javaClass, "开始设置壁纸")
            try {
                var wm = WallpaperManager.getInstance(context)
                var bit = BitmapFactory.decodeFile(f.absolutePath)
                var w = bit.width
                var h = bit.height
                var l = 0
                var t = 0
                var r = w
                var b = h
                if (w>Utils.SCREEN_WIDTH) {
                    if (clip == 0){
                        l = 0
                        r = Utils.SCREEN_WIDTH
                    }else if(clip == 1){
                        l = (w - Utils.SCREEN_WIDTH) / 2
                        r = l+Utils.SCREEN_WIDTH
                    }else if(clip == 2){
                        l = w-Utils.SCREEN_WIDTH
                        r = w
                    }
                }
                if (h>Utils.SCREEN_HEIGHT) {
                    if (clip == 0){
                        t = 0
                        b = Utils.SCREEN_HEIGHT
                    }else if(clip == 1){
                        t = (h - Utils.SCREEN_HEIGHT) / 2
                        b = t + Utils.SCREEN_HEIGHT
                    }else{
                        t = h - Utils.SCREEN_HEIGHT
                        b = h
                    }

                }
                if (Build.VERSION.SDK_INT>=24) {
                    var flag :Int
                    if (screen == 0){
                        flag = WallpaperManager.FLAG_SYSTEM
                    }else if(screen == 1){
                        flag = WallpaperManager.FLAG_LOCK
                    }else{
                        flag = WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                    }
                    wm.setBitmap(bit, Rect(l, t, r, b), false, flag)
                }else
                    wm.setBitmap(bit)
            }catch (e:Exception){
                LogUtil.d(this.javaClass, "设置壁纸失败${e.message}")
            }
        }else{
            LogUtil.d(this.javaClass, "设置壁纸失败${f.exists()}")
        }
    }

    fun handleFailed(t: Throwable) {
        LogUtil.d(this.javaClass, "获取壁纸：${t.message}")
    }

    fun exceptWallpaperURL(url: String, w: Int, h: Int): String {
        if (w >= h) {
            var r = h.toDouble() / Utils.SCREEN_HEIGHT.toDouble()
            var expectW = (w.toDouble() / r).toInt()
            return url.replace("w=1080", "w=${expectW}")
        } else {
            return url.replace("w=1080", "w=${Utils.SCREEN_WIDTH}")
        }
    }
}