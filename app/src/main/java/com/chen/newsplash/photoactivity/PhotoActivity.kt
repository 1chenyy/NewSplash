package com.chen.newsplash.photoactivity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chen.newsplash.R
import com.chen.newsplash.databinding.ActivityPhotoBinding
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.net.RetrofitManager
import com.chen.newsplash.photoactivity.adapter.CollectionAdapter
import com.chen.newsplash.photoactivity.adapter.ExifAdapter
import com.chen.newsplash.photoactivity.adapter.TagItem
import com.chen.newsplash.photoactivity.databinding.PhotoActivityViewModel
import com.chen.newsplash.utils.*
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.security.Permission

class PhotoActivity : AppCompatActivity() {
    var actionBar:ActionBar? = null
    lateinit var photo:Photo
    lateinit var binding:ActivityPhotoBinding
    lateinit var data:PhotoActivityViewModel
    var hasPermissions:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseBundle(intent)
        requestPermission()
        binding = DataBindingUtil.setContentView(this,R.layout.activity_photo)
        data = ViewModelProviders.of(this).get(PhotoActivityViewModel::class.java)
        binding.lifecycleOwner = this
        binding.data = data
        configToolbar()
        configTopView()
        binding.ivError.setOnClickListener { data.state.value = LoadingState.LOADING;loadData() }
        loadData()
        configClickEvent()
    }

    private fun requestPermission() {
        RxPermissions(this)
            .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe({ if (it) hasPermissions = true else hasPermissions = false})
    }

    private fun configClickEvent() {
        binding.ivEye.setOnClickListener {  }
        binding.ivDownload.setOnClickListener { onDownloadClick() }
        binding.ivFavorite.setOnClickListener {  }
    }

    private fun onDownloadClick() {
        when(data.downloadState.value){
            DownloadState.NO_DOWNLOAD->{}
            DownloadState.DOWNLOADED->{}
            DownloadState.DOWNLOADING->{}
        }
    }

    private fun checkAndDownload() {
        var dir = File(Environment.getExternalStorageDirectory(),"NewSplash")
        if(!dir.exists())
            dir.mkdirs()
        if (dir.list().contains("${photo.id}.jpg")){

        }
        println(dir.absoluteFile)
    }

    private lateinit var disposable:Disposable
    private fun loadData() {
        LogUtil.d(this.javaClass,"开始加载${photo.id}信息")
        disposable = RetrofitManager.getAPI().getPhoto(photo.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({p->handleSuccess(p)},{t->handleFailed(t)})
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable!=null&&!disposable.isDisposed)
            disposable.dispose()
    }

    private fun handleSuccess(bean:com.chen.newsplash.models.photo.Photo){
        LogUtil.d(this.javaClass,"加载成功")
        Glide.with(this)
            .load(bean.user.profileImage.medium)
            .placeholder(R.drawable.ic_user_default_small)
            .error(R.drawable.ic_user_default_small)
            .fallback(R.drawable.ic_user_default_small)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.ibUser)
        data.state.value = LoadingState.LOADING_SUCCESS
        data.time.value = "${Utils.getString(R.string.photo_create)}${bean.createdAt.substring(0,10)}"
        data.eye.value = "${bean.views}"
        data.download.value = "${bean.downloads}"
        data.downloadState.value = if(Utils.checkFile("${photo.id}.jpg"))DownloadState.DOWNLOADED else DownloadState.NO_DOWNLOAD
        data.favorite.value = "${bean.likes}"
        configExif(bean)
        configTag(bean)
        confiagVP(bean)
    }

    private fun confiagVP(bean: com.chen.newsplash.models.photo.Photo) {
        var vp = binding.vpCollection
        bean.relatedCollections
        var adapter = CollectionAdapter()
        adapter.results = bean.relatedCollections.results
        vp.adapter = adapter
        binding.indicator.setViewPager(vp)

    }

    private fun configTag(bean: com.chen.newsplash.models.photo.Photo) {
        var rvTag = binding.rvTag
        rvTag.layoutManager= LinearLayoutManager(this,RecyclerView.HORIZONTAL,false)
        var tags = mutableListOf<TagItem>()
        bean.tags.forEach {
            tags.add(TagItem(it.title))
        }
        val itemAdapter = GenericItemAdapter()
        var adapter : GenericFastAdapter = FastAdapter.with(itemAdapter)
        rvTag.adapter = adapter
        itemAdapter.add(tags)
    }

    private fun configExif(bean:com.chen.newsplash.models.photo.Photo) {
        var contents = ArrayList<String>()
        contents.add(bean.exif.make?:Utils.getString(R.string.none))
        contents.add(bean.exif.model?:Utils.getString(R.string.none))
        contents.add("${bean.width} x ${bean.height}")
        contents.add("${bean.exif.focalLength?:Utils.getString(R.string.none)} mm")
        contents.add("f/${bean.exif.aperture?:Utils.getString(R.string.none)}")
        contents.add("${bean.exif.exposureTime?:Utils.getString(R.string.none)} s")
        contents.add("${if(bean.exif.iso!=0)bean.exif.iso else Utils.getString(R.string.none)}")
        contents.add("${bean.color}")
        var exifAdapter = ExifAdapter()
        var rvExif = binding.rvExif
        rvExif.layoutManager = GridLayoutManager(this,2)
        rvExif.adapter = exifAdapter
        exifAdapter.initData(contents)
    }

    private fun handleFailed(t:Throwable){
        LogUtil.d(this.javaClass,"加载失败${t.message}")
        data.state.value = LoadingState.LOADING_FAILED
    }

    private fun configTopView() {
        binding.scl.setBackgroundColor(Color.parseColor(photo.color))
        binding.ivTop.showImage(Uri.parse(photo.urls.regular))
        binding.ibUser.setOnClickListener { v-> Utils.startUserActivity(this,photo.user.username,photo.user.name) }
    }

    private fun configToolbar() {
        setSupportActionBar(binding.toolbar)
        actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.title = photo.user.name
    }

    private fun parseBundle(intent: Intent) {
        if (intent.getSerializableExtra(Const.ARG_PHOTO) == null){
            finish()
        }else{
            photo = intent.getSerializableExtra(Const.ARG_PHOTO) as Photo
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.getItemId()==android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
