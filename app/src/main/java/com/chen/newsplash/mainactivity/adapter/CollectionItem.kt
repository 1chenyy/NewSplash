package com.chen.newsplash.mainactivity.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chen.newsplash.R
import com.chen.newsplash.mainactivity.adapter.viewholder.CollectionViewHolder
import com.chen.newsplash.models.collections.Collection
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook

class CollectionItem() : AbstractItem<CollectionViewHolder>() {
    lateinit var collection:Collection
    override val layoutRes: Int
        get() = R.layout.item_collection
    override val type: Int
        get() = R.id.tv_title

    override fun getViewHolder(v: View) = CollectionViewHolder(v)

    fun setData(collection:Collection):CollectionItem{
        this.collection = collection
        return this
    }

    fun getData() = collection

    class UserClickEvent(var context: Context) : ClickEventHook<CollectionItem>(){
        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<CollectionItem>, item: CollectionItem) {
            Utils.startUserActivity(context,item.collection.user.username,item.collection.user.name)
        }

        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            if (viewHolder is CollectionViewHolder)
                return listOf(viewHolder.ibUserImage)
            return super.onBindMany(viewHolder)
        }


    }
}