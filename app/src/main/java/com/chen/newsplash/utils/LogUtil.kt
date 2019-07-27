package com.chen.newsplash.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object LogUtil {
    val TAG = "NewSplash"
    val SDF = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

    fun d(c:Class<Any>,msg:String){
        Log.d(TAG,"${SDF.format(Date())}(${c.simpleName}) : ${msg}")
    }
}