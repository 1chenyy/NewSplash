package com.chen.newsplash.searchactivity

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.appcompat.app.ActionBar
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.SearchViewBindingAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chen.newsplash.R
import com.chen.newsplash.databinding.ActivitySearchBinding
import com.chen.newsplash.mainactivity.adapter.PhotoItem
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.models.search.SearchResult
import com.chen.newsplash.net.RetrofitManager
import com.chen.newsplash.photoactivity.PhotoActivity
import com.chen.newsplash.searchactivity.databinding.SearchActivityViewModel
import com.chen.newsplash.useractivity.adapter.SearchOptionalAdapter
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LoadingState
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.ui.items.ProgressItem
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.toolbar
import kotlinx.android.synthetic.main.activity_user.*

class SearchActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivitySearchBinding
    lateinit var data: SearchActivityViewModel
    lateinit var rvMain: RecyclerView
    var dispoable: Disposable? = null
    var currentFilter = 0
    var actionBar: ActionBar? = null
    var page = 1
    val itemAdapter = GenericItemAdapter()
    val footerAdapter = GenericItemAdapter()
    val fastAdapter: GenericFastAdapter = FastAdapter.with(mutableListOf(itemAdapter, footerAdapter))
    var query = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.lifecycleOwner = this
        data = ViewModelProviders.of(this).get(SearchActivityViewModel::class.java)
        binding.data = data
        parseArg(intent)
        configToolbar()
        configSpinner()
        configRecyclerView()
        configClick()
    }

    private fun configClick() {
        binding.ivError.setOnClickListener {
            if (data.state.value == LoadingState.LOADING_FAILED ) {
                page = 1; search()
            }
        }
        binding.ivSearch.setOnClickListener {
            Utils.closeInput(binding.ivSearch)
            page = 1; query = data.query.value!!;search()
        }
        binding.et.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                Utils.closeInput(binding.ivSearch)
                page = 1; query = data.query.value!!;search()
                true
            }else{
                false
            }
        }
    }

    private fun configRecyclerView() {
        rvMain = binding.main
        rvMain.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvMain.setItemViewCacheSize(5)
        rvMain.addOnScrollListener(object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                footerAdapter.clear()
                val progressItem = ProgressItem()
                progressItem.isEnabled = false
                footerAdapter.add(progressItem)
                upSwipeLoad()
            }
        })

        fastAdapter.onClickListener = { v, adapter, item, position -> onPhotoClick(item, position);true }
        fastAdapter.addEventHook(PhotoItem.UserClickEvent(this))
        rvMain.adapter = fastAdapter
    }

    private fun onPhotoClick(item: IItem<out RecyclerView.ViewHolder>, position: Int) {
        if (item is PhotoItem) {
            item.iv.transitionName = getString(R.string.shared_photo)
            var i = Intent(this, PhotoActivity::class.java)
            i.putExtra(Const.ARG_PHOTO, item.getData())
            startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this,item.iv,item.iv.transitionName).toBundle())
        }
    }

    private fun upSwipeLoad() {
        page++
        search()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dispoable != null && !(dispoable?.isDisposed ?: true)) {
            dispoable?.dispose()
        }
    }

    private fun search() {
        if (TextUtils.isEmpty(query)) {
            return
        }
        LogUtil.d(this.javaClass, "开始搜索${data.query.value}")
        if (page == 1)
            data.state.value = LoadingState.LOADING
        var result: Maybe<SearchResult>
        if (currentFilter == 0) {
            result = RetrofitManager.getAPI().searchPhotosNoFilter(query, page, Const.ARG_PAGE_COUNT)
        } else {
            result = RetrofitManager.getAPI().searchPhotosWithFilter(
                data.query.value!!,
                page,
                Const.ARG_PAGE_COUNT,
                Const.LIST_ORIENTATION[currentFilter]
            )
        }
        result.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ l -> handleSuccess(l) }, { t -> handleFailed(t) })
    }

    private fun handleSuccess(result: SearchResult) {
        var size = result.results.size
        LogUtil.d(this.javaClass, "获取到${size}条图片数据")
        if (size == 0) {
            if (data.state.value == LoadingState.LOADING) {
                data.state.value = LoadingState.LOADING_NO_DATA
            } else {
                footerAdapter.clear()
                data.state.value = LoadingState.LOADING_SUCCESS
            }
            return
        }
        data.state.value = LoadingState.LOADING_SUCCESS
        var items = mutableListOf<PhotoItem>()
        result.results.forEach {
            items.add(PhotoItem().setData(it))
        }
        if (page == 1)
            itemAdapter.clear()
        itemAdapter.add(items)
        if (page == 1)
            rvMain.scrollToPosition(0)
    }

    private fun handleFailed(t: Throwable) {
        if (data.state.value == LoadingState.LOADING) {
            LogUtil.d(this.javaClass, "Throwable:${t.message}")
            data.state.value = LoadingState.LOADING_FAILED
        } else {
            footerAdapter.clear()
        }

    }

    private fun parseArg(intent: Intent) {
        data.query.value = intent.getStringExtra(Const.ARG_ARG) ?: ""
    }

    private fun configSpinner() {
        var sp = binding.spOptional
        var adapter = SearchOptionalAdapter(this)
        sp.adapter = adapter
        sp.onItemSelectedListener = this
    }

    private fun configToolbar() {
        window.statusBarColor = Color.parseColor("#55919191")
        setSupportActionBar(toolbar)
        actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.title = Utils.getString(R.string.search_menu_title)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.getItemId() == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentFilter = position
    }
}
