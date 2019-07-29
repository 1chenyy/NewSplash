package com.chen.newsplash.mainactivity.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.chen.newsplash.databinding.FragmentPhotoBinding
import com.chen.newsplash.mainactivity.adapter.PhotoItem
import com.chen.newsplash.mainactivity.databinding.PhotoViewModel
import com.chen.newsplash.models.event.ModeChangeEvent
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.net.RetrofitManager
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LoadingState
import com.chen.newsplash.utils.LogUtil
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.listeners.EventHook
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.ui.items.ProgressItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseFragment : Fragment() {
    private lateinit var binding:FragmentPhotoBinding
    lateinit var data:PhotoViewModel
    lateinit var rv:RecyclerView
    val itemAdapter = GenericItemAdapter()
    val footerAdapter = GenericItemAdapter()
    val fastAdapter : GenericFastAdapter= FastAdapter.with(mutableListOf(itemAdapter,footerAdapter))
    lateinit var listener: EndlessRecyclerOnScrollListener
    lateinit var swipeRefresh:SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        data = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        binding.data = data
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configRecyclerView()
        configSwipeRefresh()
        loadPhotos();
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe()
    fun onModeChange(event:ModeChangeEvent){
        modeChange(event)
    }

    private fun configSwipeRefresh() {
        swipeRefresh  = binding.refresh
        swipeRefresh.setOnRefreshListener {
            downSwipeLoad()
        }
    }


    private fun configRecyclerView() {
        rv = binding.rv
        rv.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        rv.setItemViewCacheSize(5)
        rv.addOnScrollListener(object : EndlessRecyclerOnScrollListener(footerAdapter){
            override fun onLoadMore(currentPage: Int) {
                footerAdapter.clear()
                val progressItem = ProgressItem()
                progressItem.isEnabled = false
                footerAdapter.add(progressItem)
                upSwipeLoad()
            }
        })

        fastAdapter.onClickListener = {v, adapter, item, position-> onPhotoClick(item,position);true}
        addEventHook(fastAdapter)

        rv.adapter = fastAdapter
    }

    abstract fun onPhotoClick(item:GenericItem,pos:Int)

    abstract fun loadPhotos()

    abstract fun downSwipeLoad()

    abstract fun addEventHook(adapter:GenericFastAdapter)

    abstract fun upSwipeLoad()

    abstract fun modeChange(event:ModeChangeEvent)
}
