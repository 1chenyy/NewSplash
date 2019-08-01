package com.chen.newsplash.useractivity.fragments

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import com.chen.newsplash.R
import com.chen.newsplash.mainactivity.adapter.PhotoItem
import com.chen.newsplash.mainactivity.fragment.BaseFragment
import com.chen.newsplash.models.event.ModeChangeEvent
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.ui.ErrorOrNoDataItem
import com.chen.newsplash.net.RetrofitManager
import com.chen.newsplash.photoactivity.PhotoActivity
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LoadingState
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.GenericItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PhotoFragment : BaseFragment() {
    var id: String? = ""
    var type: Int? = 0
    var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments?.getString(Const.ARG_USERNAME, "")
        if (id == null)
            activity?.onBackPressed()
        type = arguments?.getInt(Const.ARG_TYPE, 0)
    }

    override fun onPhotoClick(item: GenericItem, pos: Int) {
        if (item is PhotoItem) {
            item.iv.transitionName = Utils.getString(R.string.shared_photo)
            var opt = ActivityOptions.makeSceneTransitionAnimation(activity,item.iv,item.iv.transitionName)
            var i = Intent(context, PhotoActivity::class.java)
            i.putExtra(Const.ARG_PHOTO, item.getData())
            context?.startActivity(i,opt.toBundle())
        }
    }

    override fun firstLoad() {
        LogUtil.d(this.javaClass, "${type}:开始初始查询${id}图片")
        load()
    }

    override fun downSwipeLoad() {
        LogUtil.d(this.javaClass, "${type}开始下拉加载${id}图片")
        page = 1
        load()
    }

    override fun addEventHook(adapter: GenericFastAdapter) {
        if (type == Const.TYPE_LIKE)
            adapter.addEventHook(PhotoItem.UserClickEvent(activity as Activity))
    }

    override fun upSwipeLoad() {
        page++
        LogUtil.d(this.javaClass, "${type}开始上拉加载${id}的第${page}页图片数据")
        load()
    }

    override fun modeChange(event: ModeChangeEvent) {

    }

    private fun load() {
        if (type == 0) {
            disposable = RetrofitManager.getAPI().getPhotosOfUser(id!!, page, Const.ARG_PAGE_COUNT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ l -> handleSuccess(l, page) },
                    { t -> handleFailed(t) })
        } else if (type == 2) {
            disposable = RetrofitManager.getAPI().getLikePhotosOfUser(id!!, page, Const.ARG_PAGE_COUNT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ l -> handleSuccess(l, page) },
                    { t -> handleFailed(t) })
        }

    }

    private fun handleSuccess(list: List<Photo>, page: Int) {
        LogUtil.d(this.javaClass, "${type}获取到${id}的${list.size}条图片数据")
        if (swipeRefresh.isRefreshing)
            swipeRefresh.isRefreshing = false
        if (list.size == 0){
            if (data.state.value == LoadingState.LOADING) {
                data.state.value = LoadingState.LOADING_SUCCESS
                errAdapter.add(ErrorOrNoDataItem(LoadingState.LOADING_NO_DATA))
            } else if(data.state.value == LoadingState.LOADING_SUCCESS)
                footerAdapter.clear()
            return
        }
        if (page == 1) {
            data.state.value = LoadingState.LOADING_SUCCESS
        }
        var items = mutableListOf<PhotoItem>()
        list.forEach {
            items.add(PhotoItem().setData(it))
        }
        if (page == 1)
            itemAdapter.clear()
        itemAdapter.add(items)
        if (page == 1)
            rv.scrollToPosition(0)
    }

    private fun handleFailed(t: Throwable) {
        LogUtil.d(this.javaClass, "${type}Throwable:${t.message}")
        if (swipeRefresh.isRefreshing)
            swipeRefresh.isRefreshing = false
        if (page == 1) {
            data.state.value = LoadingState.LOADING_SUCCESS
            errAdapter.add(ErrorOrNoDataItem(LoadingState.LOADING_FAILED))
        }else
            footerAdapter.clear()
    }

    companion object {
        @JvmStatic
        fun newInstance(username: String, type: Int) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putString(Const.ARG_USERNAME, username)
                putInt(Const.ARG_TYPE, type)
            }
        }
    }
}