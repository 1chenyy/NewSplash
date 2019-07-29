package com.chen.newsplash.mainactivity.fragment

import androidx.fragment.app.Fragment
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LogUtil

object FragmentFactory {
    val fragments = mutableMapOf<String,Fragment>()

    fun getFragment(tag:String):Fragment{
        LogUtil.d(this.javaClass,"获取标签为${tag}的fragment")
        if (!fragments.containsKey(tag)){
            when(tag){
                Const.TAG_PHOTO_COMMON-> fragments.put(tag,PhotoFragment.newInstance(Const.TYPE_COMMON,Const.POS_PHOTO))
                Const.TAG_PHOTO_CURATED-> fragments.put(tag,PhotoFragment.newInstance(Const.TYPE_CURATED,Const.POS_PHOTO))
                Const.TAG_COLLECTION_COMMON->fragments.put(tag,CollectionFragment.newInstance(Const.TYPE_COMMON,Const.POS_COLLECTION))
                Const.TAG_COLLECTION_CURATED->fragments.put(tag,CollectionFragment.newInstance(Const.TYPE_CURATED,Const.POS_COLLECTION))
            }
        }
        return fragments.get(tag)!!
    }
}