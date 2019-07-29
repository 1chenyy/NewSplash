package com.chen.newsplash.mainactivity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragmentList = mutableListOf<Fragment>()
    private val titleList = mutableListOf<String>()

    override fun getItem(position: Int) = fragmentList.get(position)

    override fun getPageTitle(position: Int) = titleList.get(position)

    override fun getCount() = fragmentList.size

    fun addItem(fragment:Fragment,title:String){
        fragmentList.add(fragment)
        titleList.add(title)
    }
}