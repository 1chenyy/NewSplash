package com.chen.newsplash.autowallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LogUtil
import com.tencent.mmkv.MMKV
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    lateinit var kv:MMKV
    lateinit var context:Context
    override fun onReceive(c: Context, intent: Intent) {
        kv = MMKV.defaultMMKV()
        context = c
        var isAuto = kv.decodeBool(Const.AUTO_WALLPAPER,false)
        LogUtil.d(this.javaClass,"开机了,auto = ${isAuto}")
        if (isAuto){
            startAutoWorker()
        }
    }
    private fun startAutoWorker(){
        LogUtil.d(this.javaClass,"启动自动更换壁纸")
        WorkManager.getInstance(context).cancelAllWorkByTag(Const.TAG)
        var constraints: Constraints
        var wifi = kv.decodeBool(Const.AUTO_WIFI,false)
        if (wifi){
            constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
        }else{
            constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        }
        var time = kv.decodeInt(Const.AUTO_INTERVAL, 2).toLong()
        when(time){
            0L->{time = 1}
            1L->{time = 3}
            2L->{time = 6}
            3L->{time = 12}
            4L->{time = 24}
        }
        var data = Data.Builder()
            .putInt(Const.AUTO_SHAPE,kv.decodeInt(Const.AUTO_SHAPE, 1))
            .putInt(Const.AUTO_CLIP,kv.decodeInt(Const.AUTO_CLIP, 1))
            .putInt(Const.AUTO_SCREEN,kv.decodeInt(Const.AUTO_SCREEN, 0))
            .build()
        var request = PeriodicWorkRequest.Builder(AutoWallpaperWorker::class.java,time, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR,PeriodicWorkRequest.MIN_BACKOFF_MILLIS,TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(Const.TAG)
            .build()
        WorkManager.getInstance(context!!).enqueue(request)

    }
}
