package com.chen.newsplash.favoriteactivity

import android.animation.Animator
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chen.newsplash.NewSplash
import com.chen.newsplash.R
import com.chen.newsplash.databinding.ActivityFavoriteBinding
import com.chen.newsplash.favoriteactivity.adapter.FavoriteItem
import com.chen.newsplash.favoriteactivity.databinding.FavoriteActivityViewModel
import com.chen.newsplash.mainactivity.adapter.PhotoItem
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.photoactivity.PhotoActivity
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LoadingState
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback
import com.mikepenz.fastadapter.ui.items.ProgressItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_favorite.toolbar

class FavoriteActivity : AppCompatActivity(), SimpleSwipeCallback.ItemSwipeCallback {
    lateinit var binding:ActivityFavoriteBinding
    lateinit var data:FavoriteActivityViewModel
    var actionBar: ActionBar?=null
    lateinit var rvMain:RecyclerView
    val itemAdapter = GenericItemAdapter()
    val footerAdapter = GenericItemAdapter()
    val fastAdapter : GenericFastAdapter= FastAdapter.with(mutableListOf(itemAdapter,footerAdapter))
    var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_favorite)
        binding.lifecycleOwner = this
        data = ViewModelProviders.of(this).get(FavoriteActivityViewModel::class.java)
        binding.data = data
        configToolbar()
        configMain()
    }

    override fun onResume() {
        super.onResume()
        offset = 0
        load()
    }

    private fun configMain() {
        rvMain = binding.main
        rvMain.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        rvMain.setItemViewCacheSize(5)
        rvMain.addOnScrollListener(object : EndlessRecyclerOnScrollListener(footerAdapter){
            override fun onLoadMore(currentPage: Int) {
                NewSplash.handler!!.post {
                    footerAdapter.clear()
                    val progressItem = ProgressItem()
                    progressItem.isEnabled = false
                    footerAdapter.add(progressItem)
                    upSwipeLoad()
                }
            }
        })
        fastAdapter.onClickListener = {v, adapter, item, position-> onPhotoClick(item,position);true}
        rvMain.adapter = fastAdapter
        rvMain.itemAnimator?.changeDuration = 0
        var touchCallback = SimpleSwipeCallback(this,
            getDrawable(R.drawable.ic_delete_forever_black_24dp),ItemTouchHelper.LEFT,Color.RED)

        var touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rvMain)

    }

    override fun itemSwiped(position: Int, direction: Int) {
        println("删除${position}")
        NewSplash.dbPhoto!!.getFavorite()
            .deletePhotoByID((itemAdapter.getAdapterItem(position) as FavoriteItem).photo.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        itemAdapter.remove(position)
        fastAdapter.notifyAdapterItemRemoved(position)
    }



    private fun onPhotoClick(item: IItem<out RecyclerView.ViewHolder>, position: Int) {
        if(item is FavoriteItem){
            item.iv.transitionName = getString(R.string.shared_photo)
            var i = Intent(this, PhotoActivity::class.java)
            i.putExtra(Const.ARG_PHOTO,item.photo)
            startActivity(i)
        }
    }

    private fun upSwipeLoad() {
        offset+=10
        load()
    }
    lateinit var queryDisposable:Disposable
    private fun load(){
        LogUtil.d(this.javaClass,"开始查询 offset=${offset}")
        queryDisposable = NewSplash.dbPhoto!!.getFavorite().queryPhoto(offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({l->handleSucceed(l)},{t->handleFailed(t)})
    }

    private fun handleSucceed(list:List<Photo>){
        LogUtil.d(this.javaClass,"查询到${list.size}条数据")
        if (list==null || list.size == 0){
            if (offset == 0){
                data.state.value = LoadingState.LOADING_NO_DATA
            }else{
                footerAdapter.clear()
            }
            return
        }
        data.state.value = LoadingState.LOADING_SUCCESS
        var items = arrayListOf<FavoriteItem>()
        list.forEach {
            items.add(FavoriteItem(it))
        }
        if (offset == 0)
            itemAdapter.clear()
        itemAdapter.add(items)

    }

    private fun handleFailed(t:Throwable){
        LogUtil.d(this.javaClass,"${t.message}")
        data.state.value = LoadingState.LOADING_FAILED
    }

    override fun onDestroy() {
        super.onDestroy()
        if (queryDisposable!=null&&!queryDisposable.isDisposed)
            queryDisposable.dispose()
    }

    private fun configToolbar() {
        window.statusBarColor = Color.parseColor("#55919191")
        setSupportActionBar(toolbar)
        actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.title = Utils.getString(R.string.title_favorite)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.getItemId()){
            android.R.id.home->finish()
            R.id.action_delete_all->deleteAll();
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAll() {
        var build = AlertDialog.Builder(this)
        build.setTitle(R.string.delete_all)
        build.setMessage(R.string.delete_all_msg)
        build.setCancelable(false)
        build.setPositiveButton(R.string.bt_ok,{d,w->
            NewSplash.dbPhoto!!.getFavorite().deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    itemAdapter.clear()
                    fastAdapter.notifyAdapterDataSetChanged()
                })
        })
        build.setNegativeButton(R.string.bt_cancel,null)
        build.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorite,menu)
        return true
    }

}
