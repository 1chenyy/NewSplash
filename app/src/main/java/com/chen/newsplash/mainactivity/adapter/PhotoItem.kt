package com.chen.newsplash.mainactivity.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Pair
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.chen.newsplash.NewSplash
import com.chen.newsplash.R
import com.chen.newsplash.mainactivity.adapter.viewholder.PhotoViewHolder
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.useractivity.UserActivity
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import com.github.piasy.biv.BigImageViewer
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

class PhotoItem() : AbstractItem<PhotoViewHolder>() {
    lateinit var photo:Photo
    lateinit var iv:ImageView
    lateinit var user:ImageButton

    override val type = R.id.iv_preview

    override val layoutRes: Int
        get() = R.layout.item_photo

    override fun getViewHolder(v: View) = PhotoViewHolder(v)

    override fun bindView(holder: PhotoViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        iv = holder.ivPreview
        user = holder.ibUserImage
    }

    fun setData(photo:Photo):PhotoItem{
        this.photo = photo
        return this
    }

    fun getData():Photo{
        return photo
    }

    class UserClickEvent(var context:Activity) : ClickEventHook<PhotoItem>(){
        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            if (viewHolder is PhotoViewHolder)
                return listOf(viewHolder.ibUserImage)
            return super.onBindMany(viewHolder)
        }
        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<PhotoItem>, item: PhotoItem) {
            item.user.transitionName = Utils.getString(R.string.shared_user)
            var opts = ActivityOptions.makeSceneTransitionAnimation(context,item.user,item.user.transitionName)
            Utils.startUserActivity(context,item.photo.user.username,item.photo.user.name,opts)
        }

    }
}