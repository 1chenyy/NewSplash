package com.chen.newsplash.mainactivity.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.chen.newsplash.R
import com.chen.newsplash.mainactivity.adapter.viewholder.PhotoViewHolder
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.utils.LogUtil
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

class PhotoItem() : AbstractItem<PhotoViewHolder>() {
    lateinit var photo:Photo

    override val type = R.id.iv_preview

    override val layoutRes: Int
        get() = R.layout.item_photo

    override fun getViewHolder(v: View) = PhotoViewHolder(v)

    fun setData(photo:Photo):PhotoItem{
        this.photo = photo
        return this
    }

    class UserClickEvent : ClickEventHook<PhotoItem>(){
        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            if (viewHolder is PhotoViewHolder)
                return listOf(viewHolder.ibUserImage)
            return super.onBindMany(viewHolder)
        }
        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<PhotoItem>, item: PhotoItem) {
            LogUtil.d(this.javaClass,"ClickEventHook")
        }

    }
}