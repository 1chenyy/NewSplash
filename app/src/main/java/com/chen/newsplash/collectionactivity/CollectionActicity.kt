package com.chen.newsplash.collectionactivity

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.Dimension
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chen.newsplash.R
import com.chen.newsplash.collectionactivity.databinding.CollectionActivityViewModel
import com.chen.newsplash.databinding.ActivityCollectionBinding
import com.chen.newsplash.mainactivity.adapter.PhotoItem
import com.chen.newsplash.models.collections.Collection
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.net.RetrofitManager
import com.chen.newsplash.photoactivity.PhotoActivity
import com.chen.newsplash.photoactivity.adapter.TagItem
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LoadingState
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.ui.items.ProgressItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.BitmapTransformation
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropTransformation

import kotlinx.android.synthetic.main.activity_collection.*

class CollectionActicity : AppCompatActivity() {
    var actionBar: ActionBar? = null
    lateinit var collection:Collection
    lateinit var binding: ActivityCollectionBinding
    lateinit var data:CollectionActivityViewModel
    lateinit var dispoable:Disposable
    lateinit var rvMain:RecyclerView
    val itemAdapter = GenericItemAdapter()
    val footerAdapter = GenericItemAdapter()
    val fastAdapter : GenericFastAdapter= FastAdapter.with(mutableListOf(itemAdapter,footerAdapter))
    fun isInit()=::dispoable.isInitialized
    var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_collection)
        binding.lifecycleOwner = this
        data = ViewModelProviders.of(this).get(CollectionActivityViewModel::class.java)
        binding.data = data
        parseBundle(intent)
        configToolbar()
        configTopView()
        configMain()
        binding.ivError.setOnClickListener { v-> page=0;data.state.value = LoadingState.LOADING;load()}
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isInit()&&!dispoable.isDisposed){
            dispoable.dispose()
        }
    }

    private fun configMain() {
        rvMain = binding.main
        rvMain.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        rvMain.setItemViewCacheSize(5)
        rvMain.addOnScrollListener(object : EndlessRecyclerOnScrollListener(footerAdapter){
            override fun onLoadMore(currentPage: Int) {
                footerAdapter.clear()
                LogUtil.d(this.javaClass, "当前数据${itemAdapter.adapterItemCount}，总共数据${collection.totalPhotos}")
                if (itemAdapter.adapterItemCount == collection.totalPhotos)
                    return
                val progressItem = ProgressItem()
                progressItem.isEnabled = false
                footerAdapter.add(progressItem)
                upSwipeLoad()
            }
        })

        fastAdapter.onClickListener = {v, adapter, item, position-> onPhotoClick(item,position);true}
        fastAdapter.addEventHook(PhotoItem.UserClickEvent(this))
        rvMain.adapter = fastAdapter
        LogUtil.d(this.javaClass, "开始初始加载数据")
        load()
    }

    private fun load() {
        page++
        LogUtil.d(this.javaClass, "开始加载${collection.id}中的第${page}页数据")
        dispoable = RetrofitManager.getAPI().getPhotosOfCollection("${collection.id}", page, Const.ARG_PAGE_COUNT)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ d -> handleSuccess(d) }, { t -> handleFailed(t) })
    }

    private fun onPhotoClick(item: GenericItem, position: Int) {
        if(item is PhotoItem){
            item.iv.transitionName = getString(R.string.shared_photo)
            var i = Intent(this, PhotoActivity::class.java)
            i.putExtra(Const.ARG_PHOTO,item.getData())
            startActivity(i,ActivityOptions.makeSceneTransitionAnimation(this,item.iv,item.iv.transitionName).toBundle())
        }
    }

    private fun upSwipeLoad() {
        LogUtil.d(this.javaClass, "开始上拉加载数据")
        load()
    }

    private fun handleSuccess(list:List<Photo>){
        LogUtil.d(this.javaClass, "${collection.id}获取到${list.size}条图片数据")
        data.state.value = LoadingState.LOADING_SUCCESS
        var items = mutableListOf<PhotoItem>()
        list.forEach {
            items.add(PhotoItem().setData(it))
        }
        if (page == 1)
            itemAdapter.clear()
        itemAdapter.add(items)
        if (page == 1)
            rvMain.scrollToPosition(0)
    }

    private fun handleFailed(t:Throwable){
        LogUtil.d(this.javaClass, "Throwable:${t.message}")
        data.state.value = LoadingState.LOADING_FAILED
    }

    private fun configTopView() {
        binding.ibUser.setOnClickListener {
                v->Utils.startUserActivity(this,collection.user.username,collection.user.name
            , ActivityOptions.makeSceneTransitionAnimation(this,binding.ibUser,binding.ibUser.transitionName))
        }
        Glide.with(this)
            .load(collection.user.profileImage.medium)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .placeholder(R.drawable.ic_user_default_small)
            .error(R.drawable.ic_user_default_small)
            .fallback(R.drawable.ic_user_default_small)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.ibUser)
        binding.appBar.setBackgroundColor(Color.parseColor(collection.coverPhoto.color))
        var multi = MultiTransformation<Bitmap>(
            BlurTransformation(10, 3),
            CropTransformation(Utils.dpToPx(400f),Utils.dpToPx(200f))
        )
        Glide.with(this)
            .load(collection.coverPhoto.urls.regular)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(multi))
            .into(object :SimpleTarget<Drawable>(){
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    binding.appBar.background = resource
                }
            })
        data.userAndName.value = "${collection.user.name}${Utils.getString(R.string.user_create)}${collection.publishedAt.substring(0,10)}"
        configTag()
    }

    private fun configTag() {
        var rvTag = binding.rvTag
        rvTag.layoutManager= LinearLayoutManager(this, RecyclerView.HORIZONTAL,false)
        var tags = mutableListOf<TagItem>()
        collection.tags.forEach {
            tags.add(TagItem(it.title))
        }
        val itemAdapter = GenericItemAdapter()
        var adapter : GenericFastAdapter = FastAdapter.with(itemAdapter)
        rvTag.adapter = adapter
        itemAdapter.add(tags)
    }

    private fun configToolbar() {
        window.statusBarColor = Color.parseColor("#55919191")
        setSupportActionBar(binding.toolbar)
        actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.title = collection.title
    }

    private fun parseBundle(intent: Intent) {
        if (intent.getSerializableExtra(Const.ARG_PHOTO) == null){
            finish()
        }else{
            collection = intent.getSerializableExtra(Const.ARG_PHOTO) as Collection
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.getItemId()==android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
