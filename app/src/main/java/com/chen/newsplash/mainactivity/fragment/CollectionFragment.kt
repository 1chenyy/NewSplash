package com.chen.newsplash.mainactivity.fragment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.View
import com.chen.newsplash.R
import com.chen.newsplash.collectionactivity.CollectionActicity
import com.chen.newsplash.mainactivity.adapter.CollectionItem
import com.chen.newsplash.models.collections.Collection
import com.chen.newsplash.models.event.ModeChangeEvent
import com.chen.newsplash.ui.ErrorOrNoDataItem
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

class CollectionFragment : BaseFragment() {
    private var kv = MMKV.defaultMMKV()
    private var type = -1
    private var pos = -1
    private var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getInt(Const.ARG_TYPE, -1) ?: -1
        pos = arguments?.getInt(Const.ARG_POS, -1) ?: -1
    }

    override fun onPhotoClick(item: GenericItem, pos: Int) {
        if (item is CollectionItem) {
            item.ivUser.transitionName = Utils.getString(R.string.shared_user)
            var opts = ActivityOptions.makeSceneTransitionAnimation(activity,item.ivUser,item.ivUser.transitionName)
            var i = Intent(context, CollectionActicity::class.java)
            i.putExtra(Const.ARG_PHOTO, item.getData())
            context?.startActivity(i,opts.toBundle())
        }
    }

    override fun firstLoad() {
        LogUtil.d(this.javaClass, "${type}:开始初始查询集合")
        load()
    }

    override fun downSwipeLoad() {
        LogUtil.d(this.javaClass, "${type}:开始下拉加载集合")
        page = 1
        load()
    }

    override fun addEventHook(adapter: GenericFastAdapter) {
        adapter.addEventHook(CollectionItem.UserClickEvent(activity as Activity))
    }

    override fun upSwipeLoad() {
        page++
        LogUtil.d(this.javaClass, "${type}:开始上拉加载第${page}页集合数据")
        load()
    }

    override fun modeChange(event: ModeChangeEvent) {

    }

    private fun load() {
        var list: Maybe<List<Collection>>? = null
        if (type == Const.TYPE_COMMON) {
            list = RetrofitManager.getAPI().getCollection(page, Const.ARG_PAGE_COUNT)
        } else if (type == Const.TYPE_CURATED) {
            list = RetrofitManager.getAPI().getFeatured(page, Const.ARG_PAGE_COUNT)
        }
        disposable = list!!.subscribeOn(Schedulers.io()!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ l -> handleSuccess(l, page) },
                { t -> handleFailed(t) })
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

    private fun handleSuccess(list: List<Collection>, page: Int) {
        LogUtil.d(this.javaClass, "${type}:获取到${list.size}条集合数据")
        if (swipeRefresh.isRefreshing)
            swipeRefresh.isRefreshing = false
        if (list.size == 0) {
            if (data.state.value == LoadingState.LOADING) {
                data.state.value = LoadingState.LOADING_SUCCESS
                errAdapter.add(ErrorOrNoDataItem(LoadingState.LOADING_NO_DATA))
            } else if (data.state.value == LoadingState.LOADING_SUCCESS)
                footerAdapter.clear()
            return
        }
        if (page == 1) {
            data.state.value = LoadingState.LOADING_SUCCESS
        }
        data.state.value = LoadingState.LOADING_SUCCESS
        var items = mutableListOf<CollectionItem>()
        list.forEach {
            items.add(CollectionItem().setData(it))
        }
        if (page == 1)
            itemAdapter.clear()
        itemAdapter.add(items)
        if (page == 1)
            rv.scrollToPosition(0)
    }

    companion object {
        @JvmStatic
        fun newInstance(type: Int, pos: Int) = CollectionFragment().apply {
            LogUtil.d(this.javaClass, "创建CollectionFragment")
            arguments = Bundle().apply {
                putInt(Const.ARG_TYPE, type)
                putInt(Const.ARG_POS, pos)
            }
        }
    }
}