package com.chen.newsplash.mainactivity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.chen.newsplash.utils.LogUtil

class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private var fragmentList = mutableListOf<Fragment>()
    private var titleList = mutableListOf<String>()

    override fun getItem(position: Int) = fragmentList.get(position)

    override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE
    override fun getPageTitle(position: Int) = titleList.get(position)

    override fun getCount() = fragmentList.size

    fun addItem(fragment:Fragment,title:String){
        fragmentList.add(fragment)
        titleList.add(title)
    }

    fun clear(){
        fragmentList.clear()
        titleList.clear()
    }
}