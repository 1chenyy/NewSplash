package com.chen.newsplash.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ScrollingFABBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<FloatingActionButton>(context, attrs) {
    private var toolbarHeight = 0f;
    init {
        toolbarHeight = Utils.getToolbarHeight(context!!)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        dependency: View
    ): Boolean {
        if (dependency is AppBarLayout){
            var lp = child.layoutParams as CoordinatorLayout.LayoutParams
            var fabBottomMargin = lp.bottomMargin
            var distanceToScroll = child.height + fabBottomMargin
            var ratio:Float = dependency.y/toolbarHeight
            child.translationY = -distanceToScroll*ratio
        }
        return true
    }
}