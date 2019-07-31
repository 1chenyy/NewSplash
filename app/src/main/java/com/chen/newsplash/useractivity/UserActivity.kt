package com.chen.newsplash.useractivity

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chen.newsplash.R
import com.chen.newsplash.databinding.ActivityUserBinding
import com.chen.newsplash.models.user.User
import com.chen.newsplash.net.RetrofitManager
import com.chen.newsplash.useractivity.databinding.UserActivityViewModel
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_user.*
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chen.newsplash.useractivity.fragments.CollectionFragment
import com.chen.newsplash.useractivity.fragments.PhotoFragment
import com.chen.newsplash.mainactivity.adapter.PagerAdapter
import com.chen.newsplash.photoactivity.adapter.TagItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter


class UserActivity : AppCompatActivity() {
    var actionBar: ActionBar? = null
    lateinit var binding: ActivityUserBinding
    lateinit var data: UserActivityViewModel
    lateinit var disposable: Disposable
    fun isInit() = ::disposable.isInitialized
    var name: String = ""
    var username: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseUser()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)
        binding.lifecycleOwner = this
        data = ViewModelProviders.of(this).get(UserActivityViewModel::class.java)
        binding.data = data
        configToolBar()
        loadUserInfo()
        loadVP()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isInit() && disposable != null && !disposable.isDisposed)
            disposable.dispose()
    }

    private fun loadVP() {
        var vp = binding.vpContent
        vp.offscreenPageLimit = 3
        var adapter = PagerAdapter(supportFragmentManager)
        adapter.addItem(PhotoFragment.newInstance(username, Const.TYPE_COMMON), getString(R.string.page_photo))
        adapter.addItem(PhotoFragment.newInstance(username, Const.TYPE_LIKE), getString(R.string.page_like))
        adapter.addItem(CollectionFragment.newInstance(username), getString(R.string.page_collection))
        vp.adapter = adapter
        binding.tab.setupWithViewPager(vp)
    }

    private fun loadUserInfo() {
        LogUtil.d(this.javaClass, "开始加载${username}数据")
        disposable = RetrofitManager.getAPI().getUser(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ u -> handleSuccess(u) }, { t -> handleFailed(t) })
    }

    private fun handleSuccess(user: User) {
        LogUtil.d(this.javaClass, "获取成功")
        Glide.with(this)
            .load(user.profileImage.medium)
            .placeholder(R.drawable.ic_user_default_small_grey)
            .error(R.drawable.ic_user_default_small_grey)
            .fallback(R.drawable.ic_user_default_small_grey)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.ivUser)
        binding.tvLocation.isSelected = true
        binding.tvWebsite.isSelected = true
        data.location.value = if (TextUtils.isEmpty(user.location)) Utils.getString(R.string.none) else user.location
        val str = if (TextUtils.isEmpty(user.portfolioUrl)) user.links.html else user.portfolioUrl
        var span = SpannableString(str)
        span.setSpan(URLSpan(str), 0, str.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
        binding.tvWebsite.text = span
        binding.tvWebsite.movementMethod = LinkMovementMethod.getInstance()
        data.bio.value = user.bio
        configTag(user)
    }

    private fun configTag(user: User) {
        var rvTag = binding.rvTag
        rvTag.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        var tags = mutableListOf<TagItem>()
        user.tags.custom.forEach {
            tags.add(TagItem(it.title))
        }
        val itemAdapter = GenericItemAdapter()
        var adapter: GenericFastAdapter = FastAdapter.with(itemAdapter)
        rvTag.adapter = adapter
        itemAdapter.add(tags)
    }

    private fun handleFailed(t: Throwable) {
        LogUtil.d(this.javaClass, "${t.message}")
    }

    private fun parseUser() {
        name = intent.getStringExtra(Const.ARG_NAME)
        username = intent.getStringExtra(Const.ARG_USERNAME)
        if (TextUtils.isEmpty(username))
            finish()
    }

    private fun configToolBar() {
        window.statusBarColor = Color.parseColor("#55919191")
        setSupportActionBar(toolbar)
        actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.title = name
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.getItemId() == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
