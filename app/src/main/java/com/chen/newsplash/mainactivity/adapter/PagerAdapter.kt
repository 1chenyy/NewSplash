package com.chen.newsplash.mainactivity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.chen.newsplash.utils.LogUtil

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var fragmentList = mutableListOf<Fragment>()
    private var titleList = mutableListOf<String>()

    override fun getItem(position: Int) = fragmentList.get(position)

    override fun getItemId(position: Int): Long {
        return fragmentList.get(position).hashCode().toLong();
    }

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