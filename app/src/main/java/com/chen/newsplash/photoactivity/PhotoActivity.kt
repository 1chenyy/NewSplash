package com.chen.newsplash.photoactivity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chen.newsplash.NewSplash
import com.chen.newsplash.R
import com.chen.newsplash.databinding.ActivityPhotoBinding
import com.chen.newsplash.models.photos.Photo
import com.chen.newsplash.models.photos.Urls
import com.chen.newsplash.net.RetrofitManager
import com.chen.newsplash.photoactivity.adapter.CollectionAdapter
import com.chen.newsplash.photoactivity.adapter.ExifAdapter
import com.chen.newsplash.photoactivity.adapter.TagItem
import com.chen.newsplash.photoactivity.databinding.PhotoActivityViewModel
import com.chen.newsplash.searchactivity.SearchActivity
import com.chen.newsplash.utils.*
import com.github.piasy.biv.loader.ImageLoader
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.mmkv.MMKV
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.lang.Exception

class PhotoActivity : AppCompatActivity() {
    var actionBar: ActionBar? = null
    lateinit var photo: Photo
    lateinit var binding: ActivityPhotoBinding
    lateinit var data: PhotoActivityViewModel
    var downloadManger: DownloadManager =
        NewSplash.context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    lateinit var request: DownloadManager.Request
    lateinit var completeReceiver: BroadcastReceiver
    val kv = MMKV.defaultMMKV()
    var hasPermissions: Boolean = false
    var quality = -1;
    lateinit var downloadingProgress:ProgressDialog
    private val disposables = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseBundle(intent)
        checkPermission()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo)
        data = ViewModelProviders.of(this).get(PhotoActivityViewModel::class.java)
        binding.lifecycleOwner = this
        binding.data = data
        configToolbar()
        configTopView()
        binding.ivError.setOnClickListener { data.state.value = LoadingState.LOADING;loadData() }
        loadData()
        configClickEvent()
    }

    override fun onResume() {
        super.onResume()

        disposables.add(
            Maybe.just("${photo.id}.jpg")
//                .map { checkCursor(kv.decodeLong(it, -1)) }
//                .map {
//                    if (it == DownloadState.DOWNLOADED) {
//                        LogUtil.d(this.javaClass, "cursor检查结果为完成，现在检查目录")
//                        if (hasPermissions) {
//                            if (!Utils.checkFile("${photo.id}.jpg")) {
//                                LogUtil.d(this.javaClass, "cursor检查结果为完成，但未找到该文件")
//                                DownloadState.NO_DOWNLOAD
//                            } else {
//                                it
//                            }
//                        } else {
//                            it
//                        }
//                    } else {
//                        it
//                    }
//                }
                .map { checkDir(it) }
                .map { checkDownloadManager(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data.downloadState.value = it }, { data.downloadState.value = DownloadState.NO_DOWNLOAD })
        )

        disposables.add(
            NewSplash.dbPhoto!!.getFavorite().queryPhotoByID(photo.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data.favorited.value = it?.size != 0 ?: false }, { data.favorited.value = false })
        )
    }

    private fun checkDownloadManager(it: DownloadState):DownloadState {

        if (it == DownloadState.DOWNLOADED){
            LogUtil.d(this.javaClass, "在目录中找到该文件，在DownloadManager中确认信息")
            var result = checkCursor(kv.decodeLong("${photo.id}.jpg", -1))
            if (result == DownloadState.DOWNLOADING) {
                LogUtil.d(this.javaClass, "在DownloadManager中查到正在下载")
                return DownloadState.DOWNLOADING
            }else {
                if (result == DownloadState.DOWNLOADED)
                    kv.removeValueForKey("${photo.id}.jpg")
                return DownloadState.DOWNLOADED
            }
        }else{
            return it;
        }
    }

    private fun checkCursor(id: Long): DownloadState {
        LogUtil.d(this.javaClass, "在cursor中查找下载信息")
        if (id == -1L) {
            LogUtil.d(this.javaClass, "没有${id}记录")
            return DownloadState.NO_DOWNLOAD
        } else {
            var query = DownloadManager.Query()
            query.setFilterById(id)
            var cursor = downloadManger.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                var status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    LogUtil.d(this.javaClass, "${id}下载成功")
                    return DownloadState.DOWNLOADED
                } else if (status == DownloadManager.STATUS_RUNNING
                    || status == DownloadManager.STATUS_PAUSED
                    || status == DownloadManager.STATUS_PENDING
                ) {
                    LogUtil.d(this.javaClass, "${id}正在下载")
                    return DownloadState.DOWNLOADING
                } else {
                    LogUtil.d(this.javaClass, "${id}下载失败")
                    return DownloadState.NO_DOWNLOAD
                }
            } else {
                LogUtil.d(this.javaClass, "${id}没有找到")
                return DownloadState.NO_DOWNLOAD
            }
        }
    }

    private fun checkDir(name:String):DownloadState{
        LogUtil.d(this.javaClass, "在下载目录中查找下载信息")
        var f = File(Const.DIR_DOWNLAOD,name)
        if (f.exists())
            return DownloadState.DOWNLOADED
        return DownloadState.NO_DOWNLOAD
    }

    override fun onStart() {
        super.onStart()
        var filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        completeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                LogUtil.d(this.javaClass, "收到下载广播")
                var id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
                if (id != -1L && id == kv.decodeLong("${photo.id}.jpg", -1)) {
                    data.downloadState.value = checkCursor(id)
                }
                if (id != -1L && id  ==kv.decodeLong("wallpaper_${photo.id}.jpg", -1)){
                    downloadingProgress.dismiss()
                    var f = File(Const.DIR_WALLPAPER,"${photo.id}.jpg")
                    setWallpaperFromFile(f)
                }
            }
        }
        registerReceiver(completeReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(completeReceiver)
    }

    private fun checkPermission() {
        RxPermissions(this)
            .request(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .subscribe({ if (it) hasPermissions = true else hasPermissions = false })
    }

    private fun configClickEvent() {
        binding.ivEye.setOnClickListener {
            var i = Intent(this, FullPhotoActivity::class.java)
            i.putExtra(Const.ARG_ARG, photo.urls.regular)
            startActivity(i)
        }
        binding.ivDownload.setOnClickListener { onDownloadClick() }
        binding.ivFavorite.setOnClickListener { onFavoriteClick() }
        binding.wallpaper.setOnClickListener { onWallPaperClick() }
    }

    private fun onWallPaperClick() {
        if (!Const.DIR_WALLPAPER.exists())
            Const.DIR_WALLPAPER.mkdirs()
        var f = File(Const.DIR_WALLPAPER,"${photo.id}.jpg")
        if (f.exists()){
            setWallpaperFromFile(f)
        }else{
            downloadPhoto(Const.WALLPAPER_QUALITY)
        }
    }

    private fun setWallpaperFromFile(f:File){
        var uri = FileProvider.getUriForFile(this,Const.FILE_PROVIDER_AUTH,f)
        val wallpaperIntent = WallpaperManager.getInstance(this).getCropAndSetWallpaperIntent(uri)
        wallpaperIntent.setDataAndType(uri, "image/*")
        wallpaperIntent.putExtra("mimeType", "image/*")
        startActivity(wallpaperIntent)
    }

    private fun onFavoriteClick() {

        if (data.favorited.value ?: false) {
            saveOrDeleteFavoriteDispose =
                NewSplash.dbPhoto!!.getFavorite().deletePhotoByID(photo.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            data.favorited.value = false
        } else {
            saveOrDeleteFavoriteDispose =
                NewSplash.dbPhoto!!.getFavorite().insertPhotos(photo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            data.favorited.value = true
        }
        disposables.add(saveOrDeleteFavoriteDispose)
    }

    private fun onDownloadClick() {
        when (data.downloadState.value) {
            DownloadState.NO_DOWNLOAD -> {
                checkNet()
            }
            DownloadState.DOWNLOADED -> {
                showDeleteDialog()
            }
            DownloadState.DOWNLOADING -> {

            }
        }
    }

    private fun showDeleteDialog() {
        var build = AlertDialog.Builder(this)
        build.setTitle(R.string.download_alert_title)
        build.setMessage(R.string.delete_alert_msg)
        build.setCancelable(true)
        build.setPositiveButton(R.string.bt_delete) { d, w-> deletePhoto() }
        build.setNeutralButton(R.string.bt_view) { d, w->jumpAlbum()}
        build.setNegativeButton(R.string.bt_return,null)
        build.create().show()
    }

    private fun jumpAlbum(){
        var f = File(Const.DIR_DOWNLAOD, "${photo.id}.jpg")
        if (f.exists()){
            var intent = Intent(Intent.ACTION_VIEW);
            var uri = FileProvider.getUriForFile(this,Const.FILE_PROVIDER_AUTH,f)
            intent.setDataAndType(uri, "image/jpg")
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            var chooser = Intent.createChooser(intent,getString(R.string.title_select))
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            chooser.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            chooser.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivity(chooser);
        }else{
            Toast.makeText(this, R.string.junp_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deletePhoto() {
        var f = File(Const.DIR_DOWNLAOD, "${photo.id}.jpg")
        try {
            if (f.exists())
                f.delete()
        } catch (e: Exception) {

        }
        if (!f.exists()) {
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
            data.downloadState.value = DownloadState.NO_DOWNLOAD
        } else {
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show()
            data.downloadState.value = DownloadState.DOWNLOADED
        }
    }

    private fun checkNet() {
        downloadPhoto(Const.DOWNLOAD_QUALITY)
//        if (kv.decodeBool(Const.DOWNLOAD_NET_TYPE,false) &&
//            !Utils.CONNECTIVITY_MANAGER.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected){
//            var build = androidx.appcompat.app.AlertDialog.Builder(this)
//            build.setCancelable(false)
//            build.setTitle(R.string.download_alert_title)
//            build.setTitle(R.string.download_alert_content)
//            build.setPositiveButton(R.string.bt_only,{b,w->downloadPhoto()})
//            build.setNeutralButton(R.string.bt_cancel,null)
//            build.setNegativeButton(R.string.bt_no_ask,{b,w->kv.encode(Const.DOWNLOAD_NET_TYPE,true);downloadPhoto()})
//            build.create().show()
//        }else{
//            downloadPhoto()
//        }
    }

    private fun downloadPhoto(type:String) {

        if (kv.decodeInt(type, -1) == -1) {
            var build = androidx.appcompat.app.AlertDialog.Builder(this)
            build.setCancelable(true)
            build.setTitle(if(type.equals(Const.DOWNLOAD_QUALITY)) R.string.quality_title else  R.string.wallpaper_quality_title)
            var checkedItem = 2;
            var qualities = arrayOf(
                getString(R.string.qulaity_raw),
                getString(R.string.quality_full),
                getString(R.string.quality_reqular),
                getString(R.string.quality_small)
            )
            build.setSingleChoiceItems(qualities, 2) { d, w -> checkedItem = w }
            build.setPositiveButton(R.string.bt_only) { d, w -> quality = checkedItem;download(type) }
            build.setNegativeButton(R.string.bt_no_ask
            ) { d, w ->
                kv.encode(type, checkedItem);download(type)
            }
            build.create().show()
        } else {
            download(type)
        }

    }

    private fun download(type:String) {
        LogUtil.d(this.javaClass, "开始下载${photo.id}.jpg")
        if (type.equals(Const.WALLPAPER_QUALITY)){
            showProgressDialog()
        }
        if (type.equals(Const.DOWNLOAD_QUALITY))
            data.downloadState.value = DownloadState.DOWNLOADING
        request = DownloadManager.Request(Uri.parse(getUri(photo.urls)))
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_PICTURES,
            if (type.equals(Const.DOWNLOAD_QUALITY)) "${Const.DIR_DOWNLAOD_NAME}/${photo.id}.jpg" else "${Const.DIR_WALLPAPER_NAME}/${photo.id}.jpg"
        )
        request.setTitle(getString(R.string.downloading_title))
        request.setDescription("${photo.id}.jpg")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setMimeType("image/jpeg")
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or  DownloadManager.Request.NETWORK_MOBILE)
        request.setAllowedOverMetered(true)
        request.setAllowedOverRoaming(true)
        var id = downloadManger.enqueue(request)
        LogUtil.d(this.javaClass, "开始下载ID${id}")
        if (type.equals(Const.DOWNLOAD_QUALITY)){
            kv.encode("${photo.id}.jpg", id)
        }else{
            kv.encode("wallpaper_${photo.id}.jpg", id)
        }

    }

    private fun showProgressDialog() {
        downloadingProgress  = ProgressDialog(this)
        downloadingProgress.setCancelable(false)
        downloadingProgress.setMessage(getString(R.string.wallpaper_download))
        downloadingProgress.isIndeterminate = true
        downloadingProgress.setTitle(R.string.download_alert_title)
        downloadingProgress.setButton(AlertDialog.BUTTON_NEGATIVE,getString(R.string.bt_cancel)){d,w->
            var id = kv.decodeLong("wallpaper_${photo.id}.jpg", -1)
            if (id!=-1L){
                downloadManger.remove(id)
            }
        }
        downloadingProgress.show()
    }

    private fun getUri(urls: Urls) = when (kv.decodeInt(Const.DOWNLOAD_QUALITY, -1)) {
        0 -> urls.raw
        1 -> urls.full
        2 -> urls.regular
        3 -> urls.small
        else -> when (quality) {
            0 -> urls.raw
            1 -> urls.full
            2 -> urls.regular
            3 -> urls.small
            else -> urls.regular
        }
    }

    private fun checkAndDownload() {
        var dir = File(Environment.getExternalStorageDirectory(), "NewSplash")
        if (!dir.exists())
            dir.mkdirs()
        if (dir.list().contains("${photo.id}.jpg")) {

        }
        println(dir.absoluteFile)
    }

    private lateinit var saveOrDeleteFavoriteDispose: Disposable
    private fun loadData() {
        LogUtil.d(this.javaClass, "开始加载${photo.id}信息")

        disposables.add(
            RetrofitManager.getAPI().getPhoto(photo.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ p -> handleSuccess(p) }, { t -> handleFailed(t) })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun handleSuccess(bean: com.chen.newsplash.models.photo.Photo) {
        LogUtil.d(this.javaClass, "加载成功")
        Glide.with(this)
            .load(bean.user.profileImage.medium)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .placeholder(R.drawable.ic_user_default_small)
            .error(R.drawable.ic_user_default_small)
            .fallback(R.drawable.ic_user_default_small)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.ibUser)
        data.state.value = LoadingState.LOADING_SUCCESS
        data.time.value = "${Utils.getString(R.string.photo_create)}${bean.createdAt.substring(0, 10)}"
        data.eye.value = "${bean.views}"
        data.download.value = "${bean.downloads}"
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
        rvTag.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        var tags = mutableListOf<TagItem>()
        bean.tags.forEach {
            tags.add(TagItem(it.title))
        }
        val itemAdapter = GenericItemAdapter()
        var adapter: GenericFastAdapter = FastAdapter.with(itemAdapter)
        rvTag.adapter = adapter
        adapter.onClickListener = {v, adapter, item, position->
            if (item is TagItem){
                var i = Intent(this,SearchActivity::class.java)
                i.putExtra(Const.ARG_ARG,item.content)
                startActivity(i)
            }

            true
        }
        itemAdapter.add(tags)
    }

    private fun configExif(bean: com.chen.newsplash.models.photo.Photo) {
        var contents = ArrayList<String>()
        contents.add(bean.exif.make ?: Utils.getString(R.string.none))
        contents.add(bean.exif.model ?: Utils.getString(R.string.none))
        contents.add("${bean.width} x ${bean.height}")
        contents.add("${bean.exif.focalLength ?: Utils.getString(R.string.none)} mm")
        contents.add("f/${bean.exif.aperture ?: Utils.getString(R.string.none)}")
        contents.add("${bean.exif.exposureTime ?: Utils.getString(R.string.none)} s")
        contents.add("${if (bean.exif.iso != 0) bean.exif.iso else Utils.getString(R.string.none)}")
        contents.add("${bean.color}")
        var exifAdapter = ExifAdapter()
        var rvExif = binding.rvExif
        rvExif.layoutManager = GridLayoutManager(this, 2)
        rvExif.adapter = exifAdapter
        exifAdapter.initData(contents)
    }

    private fun handleFailed(t: Throwable) {
        LogUtil.d(this.javaClass, "加载失败${t.message}")
        data.state.value = LoadingState.LOADING_FAILED
    }

    private fun configTopView() {
        binding.scl.setBackgroundColor(Color.parseColor(photo.color))
        binding.ivTop.showImage(Uri.parse(Utils.exceptURL(photo.urls.regular,photo.width,photo.height)))
        binding.ibUser.setOnClickListener { v ->
            Utils.startUserActivity(
                this, photo.user.username, photo.user.name,
                ActivityOptions.makeSceneTransitionAnimation(this, binding.ibUser, binding.ibUser.transitionName)
            )
        }
    }

    private fun configToolbar() {
        setSupportActionBar(binding.toolbar)
        actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.title = photo.user.name
    }

    private fun parseBundle(intent: Intent) {
        if (intent.getSerializableExtra(Const.ARG_PHOTO) == null) {
            finish()
        } else {
            photo = intent.getSerializableExtra(Const.ARG_PHOTO) as Photo
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.getItemId() == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
