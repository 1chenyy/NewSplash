package com.chen.newsplash.mainactivity.fragment

import android.os.Bundle
import com.chen.newsplash.mainactivity.adapter.PhotoItem
import com.chen.newsplash.models.event.ModeChangeEvent
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.net.RetrofitManager
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
import kotlinx.android.synthetic.main.fragment_photo.view.*

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
        LogUtil.d(this.javaClass, "${type}:开始上拉加载第${page}页数据")
        load()
    }

    override fun onPhotoClick(item: GenericItem, pos: Int) {
        LogUtil.d(this.javaClass, "onPhotoClick")

    }

    override fun downSwipeLoad() {
        LogUtil.d(this.javaClass, "${type}:开始下拉加载")
        page = 1
        load()

    }

    override fun addEventHook(adapter: GenericFastAdapter) {
        adapter.addEventHook(PhotoItem.UserClickEvent())
    }

    override fun loadPhotos() {
        LogUtil.d(this.javaClass, "${type}:开始初始查询")
        load()
    }

    override fun modeChange(event: ModeChangeEvent) {
        if (event.pos == pos && event.type == type) {
            LogUtil.d(this.javaClass, "${type}:开始根据事件初始查询")
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

        list!!.subscribeOn(Schedulers.io()!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ l -> handleSuccess(l, page) },
                { t -> handleFailed(t) })
    }

    private fun handleSuccess(list: List<Photo>, page: Int) {
        LogUtil.d(this.javaClass, "${type}:获取到${list.size}条数据")
        if (swipeRefresh.isRefreshing)
            swipeRefresh.isRefreshing = false
        data.state.value = LoadingState.LOADING_SUCCESS
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
        LogUtil.d(this.javaClass, "Throwable:${t.message}")
        if (swipeRefresh.isRefreshing)
            swipeRefresh.isRefreshing = false
        data.state.value = LoadingState.LOADING_FAILED
    }


    companion object {
        @JvmStatic
        fun newInstance(type: Int, pos: Int) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putInt(Const.ARG_TYPE, type)
                putInt(Const.ARG_POS, pos)
            }
        }
    }
}