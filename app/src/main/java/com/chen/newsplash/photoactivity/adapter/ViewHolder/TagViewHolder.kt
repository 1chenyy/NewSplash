package com.chen.newsplash.photoactivity.adapter.ViewHolder

import android.view.View
import android.widget.TextView
import com.chen.newsplash.R
import com.chen.newsplash.photoactivity.adapter.TagItem
import com.mikepenz.fastadapter.FastAdapter

class TagViewHolder(item: View) : FastAdapter.ViewHolder<TagItem>(item) {
    override fun bindView(item: TagItem, payloads: MutableList<Any>) {
        tag.text = item.content
    }

    override fun unbindView(item: TagItem) {
        tag.text = null
    }

    var tag: TextView

    init {
        tag = item.findViewById(R.id.tv_tag)
    }
}