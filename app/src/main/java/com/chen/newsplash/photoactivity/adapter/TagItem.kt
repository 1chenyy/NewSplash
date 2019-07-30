package com.chen.newsplash.photoactivity.adapter

import android.view.View
import com.chen.newsplash.R
import com.chen.newsplash.photoactivity.adapter.ViewHolder.TagViewHolder
import com.mikepenz.fastadapter.items.AbstractItem

class TagItem(val content:String) : AbstractItem<TagViewHolder>() {
    override val layoutRes = R.layout.item_tag
    override val type = R.id.tv_tag
    override fun getViewHolder(v: View) = TagViewHolder(v)


}