package com.chen.newsplash.useractivity.fragments

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import com.chen.newsplash.R
import com.chen.newsplash.collectionactivity.CollectionActicity
import com.chen.newsplash.mainactivity.adapter.CollectionItem
import com.chen.newsplash.mainactivity.fragment.BaseFragment
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CollectionFragment : BaseFragment() {
    var id: String? = ""
    var page = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments?.getString(Const.ARG_USERNAME, "")
        if (id == null)
            activity?.onBackPressed()

    }

    override fun onPhotoClick(item: GenericItem, pos: Int) {
        if (item is CollectionItem) {
            item.ivUser.transitionName = Utils.getString(R.string.shared_user)
            var i = Intent(context, CollectionActicity::class.java)
            i.putExtra(Const.ARG_PHOTO, item.getData())
            context?.startActivity(i,ActivityOptions.makeSceneTransitionAnimation(activity,item.ivUser,item.ivUser.transitionName).toBundle())
        }
    }

    override fun firstLoad() {
        LogUtil.d(this.javaClass, "开始初始查询${id}集合")
        load()
    }

    override fun downSwipeLoad() {
        LogUtil.d(this.javaClass, "开始下拉加载${id}集合")
        page = 1
        load()
    }

    override fun addEventHook(adapter: GenericFastAdapter) {
    }

    override fun upSwipeLoad() {
        page++
        LogUtil.d(this.javaClass, "开始上拉加载${id}的第${page}页图片数据")
        load()
    }

    override fun modeChange(event: ModeChangeEvent) {
    }

    private fun load() {
        disposable = RetrofitManager.getAPI().getCollectionsOfUser(id!!, page, Const.ARG_PAGE_COUNT)
            .subscribeOn(Schedulers.io()!!)
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
            errAdapter.add(ErrorOrNoDataItem(LoadingState.LOADING_FAILED))
        } else
            footerAdapter.clear()
    }

    private fun handleSuccess(list: List<Collection>, page: Int) {
        LogUtil.d(this.javaClass, "获取到${id}的${list.size}条集合数据")
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
        if (page == 1)
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
        fun newInstance(username: String) = CollectionFragment().apply {
            arguments = Bundle().apply {
                putString(Const.ARG_USERNAME, username)
            }
        }
    }
}