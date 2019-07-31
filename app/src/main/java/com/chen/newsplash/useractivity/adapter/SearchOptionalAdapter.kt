package com.chen.newsplash.useractivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.chen.newsplash.R

class SearchOptionalAdapter(var context:Context):BaseAdapter() {
    var title:MutableList<Int>
    var icon:MutableList<Int>
    init {
        title = mutableListOf(R.string.search_none,R.string.search_portait,R.string.search_landscape,R.string.search_square)
        icon = mutableListOf(R.drawable.ic_swap_calls_black_24dp,R.drawable.ic_stay_current_portrait_black_24dp,
            R.drawable.ic_stay_current_landscape_black_24dp,R.drawable.ic_view_module_black_24dp)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = LayoutInflater.from(context).
                inflate(R.layout.item_search_optional,null)
        var tv = v.findViewById<TextView>(R.id.tv)
        tv.setText(title[position])
        var iv = v.findViewById<ImageView>(R.id.iv)
        iv.setImageResource(icon[position])
        return v
    }

    override fun getItem(position: Int) = title[position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getCount() = title.size
}