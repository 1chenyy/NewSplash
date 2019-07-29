package com.chen.newsplash.mainactivity.databinding

import androidx.databinding.BindingAdapter
import com.chen.newsplash.R
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.Utils
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu


object MainActivtyBindAdapter {
    @JvmStatic
    @BindingAdapter("setFABContent")
    fun setFABContent(menu: FloatingActionMenu, mode: Int) {
        when (mode) {
            Const.MODEL_LATEST -> {
                menu.menuButtonLabelText = Utils.getString(R.string.label_latest)
                menu.menuIconView.setImageDrawable(Utils.getDrawable(R.drawable.ic_latest_white))
            }
            Const.MODEL_OLDEST -> {
                menu.menuButtonLabelText = Utils.getString(R.string.label_oldest)
                menu.menuIconView.setImageDrawable(Utils.getDrawable(R.drawable.ic_no_latest_white))
            }
            Const.MODEL_POPULAR -> {
                menu.menuButtonLabelText = Utils.getString(R.string.label_popular)
                menu.menuIconView.setImageDrawable(Utils.getDrawable(R.drawable.ic_popular_white))
            }
            Const.MODEL_RANDOM -> {
                menu.menuButtonLabelText = Utils.getString(R.string.label_random)
                menu.menuIconView.setImageDrawable(Utils.getDrawable(R.drawable.ic_random_white))
            }
        }
    }

    @JvmStatic
    @BindingAdapter("mode","datamode")
    fun setFABEnable(fab:FloatingActionButton,mode:Int,datamode: Int){
        fab.isEnabled = mode != datamode
    }
}
