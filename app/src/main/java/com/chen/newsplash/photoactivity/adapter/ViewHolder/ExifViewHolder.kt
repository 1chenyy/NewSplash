package com.chen.newsplash.photoactivity.adapter.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chen.newsplash.databinding.ItemExifBinding
import com.chen.newsplash.photoactivity.databinding.ExifDataBinding

class ExifViewHolder(var bind: ItemExifBinding) : RecyclerView.ViewHolder(bind.root) {
    init {
        bind.tvContent.isSelected = true
    }
    fun bind(data: ExifDataBinding) {
        bind.data = data
        bind.executePendingBindings()
    }
}