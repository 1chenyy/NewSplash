package com.chen.newsplash.favoriteactivity.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chen.newsplash.R
import com.chen.newsplash.favoriteactivity.adapter.ViewHolder.FavoriteViewHolder
import com.chen.newsplash.mainactivity.adapter.viewholder.PhotoViewHolder
import com.chen.newsplash.models.photos.Photo
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

class FavoriteItem(var photo:Photo) : AbstractItem<FavoriteViewHolder>() {
    override val layoutRes = R.layout.item_favorite
    override val type = R.id.iv
    override fun getViewHolder(v: View) = FavoriteViewHolder(v)

    class DeleteClickEvent():ClickEventHook<FavoriteItem>(){
        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<FavoriteItem>, item: FavoriteItem) {

        }

    }
}