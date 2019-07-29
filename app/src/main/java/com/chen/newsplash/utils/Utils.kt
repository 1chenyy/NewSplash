package com.chen.newsplash.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.chen.newsplash.NewSplash
import android.os.Process
import android.text.TextUtils
import com.chen.newsplash.R
import java.io.Closeable
import java.lang.Exception

object Utils {
    val METRICS = NewSplash.context!!.resources.displayMetrics
    val SCREEN_DENSITY = METRICS.density
    val SCREEN_WIDTH = METRICS.widthPixels
    val SCREEN_HEIGHT = METRICS.heightPixels
    val CONNECTIVITY_MANAGER: ConnectivityManager =
        NewSplash.context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isRunOnUiThread() = NewSplash.mainThread == Process.myTid()

    fun runOnUiThread(runnable: Runnable) {
        if (isRunOnUiThread())
            runnable.run()
        else
            NewSplash.handler!!.post(runnable)
    }

    fun getString(res: Int) = NewSplash.context!!.getString(res)

    fun getDrawable(res: Int) = NewSplash.context!!.getDrawable(res)

    fun dpToPx(value: Float) = (value / SCREEN_DENSITY + 0.5f).toInt()

    fun pxToDp(value: Float) = (value / SCREEN_DENSITY + 0.5f).toInt()

    fun getCurrentNetworkState() = CONNECTIVITY_MANAGER.activeNetworkInfo?.state

    fun isConnected() = getCurrentNetworkState() == NetworkInfo.State.CONNECTED

    fun closeIO(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (e: Exception) {

        }
    }

    fun getToolbarHeight(context: Context):Float{
        var style = context.theme.obtainStyledAttributes(
            intArrayOf(R.attr.actionBarSize)
        )
        var height = style.getDimension(0,0f)
        style.recycle()
        return height
    }

    fun parseHeads(heads:okhttp3.Headers?){
        var link = heads?.get("link")?:""
        var nextLink = ""
        if (!TextUtils.isEmpty(link)){
            var links = link.split(",")
            links.forEach {
                if (it.contains(Const.LINK_NEXT))
                    nextLink = it.trim().split(";")[0]
                        .replace("<","")
                        .replace(">","")
            }
        }
        if (nextLink.startsWith("https://")){

        }
    }

    fun generateID(pos:Int,type:Int):String{
        return "id${pos}-${type}"
    }

    fun findPos(id:Int):Int{
        if (id == R.id.nav_photo)
            return 0
        else if(id == R.id.nav_album)
            return 1
        else
            return -1
    }

    fun getModeArg(mode:Int) : String= when(mode){
        Const.MODEL_LATEST -> Const.MODEL_LATEST_ARG
        Const.MODEL_OLDEST -> Const.MODEL_OLDEST_ARG
        Const.MODEL_POPULAR -> Const.MODEL_POPULAR_ARG
        else->Const.MODEL_LATEST_ARG
    }
}