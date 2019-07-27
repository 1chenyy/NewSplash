package com.chen.newsplash.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.chen.newsplash.NewSplash
import android.os.Process
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
}