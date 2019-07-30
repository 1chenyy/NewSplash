package com.chen.newsplash.photoactivity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.chen.newsplash.R
import com.chen.newsplash.photoactivity.adapter.ViewHolder.ExifViewHolder
import com.chen.newsplash.photoactivity.databinding.ExifDataBinding
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.Utils

class ExifAdapter :RecyclerView.Adapter<ExifViewHolder>() {
    var contents:ArrayList<String>
    init {
        contents = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExifViewHolder(
        DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_exif,parent,false)
    )
    override fun getItemCount() = Const.LIST_EXIF_ICON.size

    override fun onBindViewHolder(holder: ExifViewHolder, position: Int) {
        holder.bind(ExifDataBinding(Utils.getDrawable(Const.LIST_EXIF_ICON[position]),Const.LIST_EXIF_TITLE[position],contents[position]))
    }

    fun initData(list:List<String>){
        contents.addAll(list)
        notifyDataSetChanged()
    }
}