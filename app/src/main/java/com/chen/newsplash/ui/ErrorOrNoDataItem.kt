package com.chen.newsplash.ui

import android.view.View
import com.chen.newsplash.R
import com.chen.newsplash.utils.LoadingState
import com.mikepenz.fastadapter.items.AbstractItem

class ErrorOrNoDataItem(var state:LoadingState):AbstractItem<ErrorOrNoDataViewHolder>() {
    override val layoutRes = R.layout.item_error_or_nodata
    override val type = R.id.tv_error

    override fun getViewHolder(v: View) = ErrorOrNoDataViewHolder(v)
}