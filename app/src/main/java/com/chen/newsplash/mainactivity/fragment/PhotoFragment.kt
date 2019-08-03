package com.chen.newsplash.mainactivity.fragment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.chen.newsplash.R
import com.chen.newsplash.mainactivity.adapter.PhotoItem
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
import com.tencent.mmkv.MMKV
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PhotoFragment : BaseFragment() {

    private var kv = MMKV.defaultMMKV()
    private var type = -1
    private var pos = -1
    private var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getInt(Const.ARG_TYPE, -1) ?: -1
        pos = arguments?.getInt(Const.ARG_POS, -1) ?: -1
    }

    override fun upSwipeLoad() {
        page++
        LogUtil.d(this.javaClass, "${type}:开始上拉加载第${page}页图片数据")
        load()
    }

    override fun onPhotoClick(item: GenericItem, pos: Int) {
        if(item is PhotoItem){
            item.iv.transitionName = Utils.getString(R.string.shared_photo)
            var opt = ActivityOptions.makeSceneTransitionAnimation(activity,item.iv,item.iv.transitionName)
            var i = Intent(context,PhotoActivity::class.java)
            i.putExtra(Const.ARG_PHOTO,item.getData())
            context?.startActivity(i,opt.toBundle())
        }
    }

    override fun downSwipeLoad() {
        LogUtil.d(this.javaClass, "${type}:开始下拉加载图片")
        page = 1
        load()
    }

    override fun addEventHook(adapter: GenericFastAdapter) {
        adapter.addEventHook(PhotoItem.UserClickEvent(activity as Activity))
    }

    override fun firstLoad() {
        LogUtil.d(this.javaClass, "${type}:开始初始查询图片")
        load()
    }

    override fun modeChange(event: ModeChangeEvent) {
        if (event.pos == pos && event.type == type) {
            LogUtil.d(this.javaClass, "${type}:开始根据事件初始查询图片")
            swipeRefresh.isRefreshing = true
            page = 1
            load()
        }
    }

    private fun load() {
        var mode = kv.decodeInt(Utils.generateID(pos, type), 0)
        var list: Maybe<List<Photo>>? = null
        if (mode != Const.MODEL_RANDOM) {
            if (type == Const.TYPE_COMMON) {
                list = RetrofitManager.getAPI().getPhotos(page, Const.ARG_PAGE_COUNT, Utils.getModeArg(mode))
            } else if (type == Const.TYPE_CURATED) {
                list = RetrofitManager.getAPI().getCurated(page, Const.ARG_PAGE_COUNT, Utils.getModeArg(mode))
            }
        } else {

        }

        disposable = list!!.subscribeOn(Schedulers.io()!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ l -> handleSuccess(l, page) },
                { t -> handleFailed(t) })
    }



    private fun handleSuccess(list: List<Photo>, page: Int) {
        LogUtil.d(this.javaClass, "${type}:获取到${list.size}条图片数据")
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
        data.state.value = LoadingState.LOADING_SUCCESS
        var items = mutableListOf<PhotoItem>()
        list.forEach {
            items.add(PhotoItem().setData(it))
        }
        if (page == 1) {
            errAdapter.clear()
            itemAdapter.clear()
        }
        itemAdapter.add(items)
        if (page == 1)
            rv.scrollToPosition(0)
    }

    private fun handleFailed(t: Throwable) {
        LogUtil.d(this.javaClass, "Throwable:${t.message}")
        if (swipeRefresh.isRefreshing)
            swipeRefresh.isRefreshing = false
        if (page == 1) {
            data.state.value = LoadingState.LOADING_SUCCESS
            if(itemAdapter.adapterItemCount==0){
                errAdapter.clear()
                errAdapter.add(ErrorOrNoDataItem(LoadingState.LOADING_FAILED))
            }
        }else
            footerAdapter.clear()
    }


    companion object {
        @JvmStatic
        fun newInstance(type: Int, pos: Int) = PhotoFragment().apply {
            LogUtil.d(this.javaClass,"创建PhotoFragment")
            arguments = Bundle().apply {
                putInt(Const.ARG_TYPE, type)
                putInt(Const.ARG_POS, pos)
            }
        }
    }
}