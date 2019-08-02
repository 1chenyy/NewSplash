package com.chen.newsplash.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.chen.newsplash.NewSplash
import android.os.Process
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.chen.newsplash.R
import com.chen.newsplash.useractivity.UserActivity
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

    fun dpToPx(value: Float) = (value * SCREEN_DENSITY + 0.5f).toInt()

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

    fun startUserActivity(context:Context,username:String,name:String,opts:ActivityOptions){
        var intent = Intent(context, UserActivity::class.java)
        intent.putExtra(Const.ARG_NAME,name)
        intent.putExtra(Const.ARG_USERNAME,username)
        context.startActivity(intent,opts.toBundle())
    }

    fun closeInput(v:View){
        var imm:InputMethodManager = NewSplash.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken,0)
    }

    fun checkFile(name:String):Boolean{
        if (!Const.DIR_DOWNLAOD.exists())
            Const.DIR_DOWNLAOD.mkdirs()
        return Const.DIR_DOWNLAOD.list().contains(name)
    }

    fun exceptURL(url:String,w:Int,h:Int):String{
        if (w>=h){
            var r = h.toDouble() / SCREEN_HEIGHT.toDouble()
            var expectW = (w.toDouble()/r).toInt()
            return url.replace("w=1080","w=${expectW}")
        }else{
            return url
        }

    }
}