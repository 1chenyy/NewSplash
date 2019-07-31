package com.chen.newsplash.ui

import android.view.View
import android.widget.TextView
import com.chen.newsplash.R
import com.chen.newsplash.utils.LoadingState
import com.chen.newsplash.utils.Utils
import com.mikepenz.fastadapter.FastAdapter

class ErrorOrNoDataViewHolder(item: View) : FastAdapter.ViewHolder<ErrorOrNoDataItem>(item) {
    override fun bindView(item: ErrorOrNoDataItem, payloads: MutableList<Any>) {
        if (item.state == LoadingState.LOADING_FAILED)
            tv.text = Utils.getString(R.string.loading_err_swipe_retry)
        else if (item.state == LoadingState.LOADING_NO_DATA)
            tv.text = Utils.getString(R.string.loading_nodata)
    }

    override fun unbindView(item: ErrorOrNoDataItem) {

    }

    var tv: TextView

    init {
        tv = item.findViewById(R.id.tv_error)
    }
}