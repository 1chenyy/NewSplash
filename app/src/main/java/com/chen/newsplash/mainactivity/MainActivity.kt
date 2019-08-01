package com.chen.newsplash.mainactivity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.chen.newsplash.R
import com.chen.newsplash.databinding.ActivityMainBinding
import com.chen.newsplash.favoriteactivity.FavoriteActivity
import com.chen.newsplash.mainactivity.adapter.PagerAdapter
import com.chen.newsplash.mainactivity.databinding.MainActivityViewModel
import com.chen.newsplash.mainactivity.fragment.FragmentFactory
import com.chen.newsplash.mainactivity.fragment.PhotoFragment
import com.chen.newsplash.models.event.ModeChangeEvent
import com.chen.newsplash.searchactivity.SearchActivity
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LogUtil
import com.chen.newsplash.utils.Utils
import com.github.clans.fab.FloatingActionMenu
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ViewPager.OnPageChangeListener, View.OnClickListener {


    lateinit var binding: ActivityMainBinding
    lateinit var data: MainActivityViewModel
    lateinit var navView: NavigationView
    var current: Int = 0
    lateinit var vp: ViewPager
    lateinit var adapter: PagerAdapter
    lateinit var fbm: FloatingActionMenu
    var kv = MMKV.defaultMMKV()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        data = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        binding.lifecycleOwner = this
        binding.data = data
        configDrawerLayoutAndToolBar()
        initPhotoViewPager()
        data.mode.value = kv.decodeInt(generateKey(), 0)
    }

    private fun initPhotoViewPager() {
        vp = binding.main.vpContent
        adapter = PagerAdapter(supportFragmentManager)
        setPhotoFragment()
        vp.adapter = adapter
        binding.main.tab.setupWithViewPager(vp)
        vp.addOnPageChangeListener(this)

    }

    private fun setPhotoFragment() {
        LogUtil.d(this.javaClass, "开始显示图片")
        adapter.clear()
        adapter.addItem(FragmentFactory.getFragment(Const.TAG_PHOTO_COMMON), getString(R.string.page_normal))
        adapter.addItem(FragmentFactory.getFragment(Const.TAG_PHOTO_CURATED), getString(R.string.page_curated))
        adapter.notifyDataSetChanged()
    }

    private fun setCollectionFragment() {
        LogUtil.d(this.javaClass, "开始显示集合")
        adapter.clear()
        adapter.addItem(FragmentFactory.getFragment(Const.TAG_COLLECTION_COMMON), getString(R.string.page_normal))
        adapter.addItem(FragmentFactory.getFragment(Const.TAG_COLLECTION_CURATED), getString(R.string.page_curated))
        adapter.notifyDataSetChanged()
    }

    private fun configDrawerLayoutAndToolBar() {
        val toolbar: Toolbar = binding.main.toolbar
        setSupportActionBar(toolbar)

        fbm = binding.main.fab
        fbm.setClosedOnTouchOutside(true)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        binding.main.fabLatest.setOnClickListener(this)
        binding.main.fabOldest.setOnClickListener(this)
        binding.main.fabPopular.setOnClickListener(this)
        binding.main.fabRandom.setOnClickListener(this)


        navView = binding.navView
        navView.itemIconTintList = null
        navView.itemTextColor = ColorStateList.valueOf(Color.BLACK)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

    }

    override fun onResume() {
        super.onResume()
        navView.menu.getItem(current).isChecked = true
        RxPermissions(this)
            .request(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .subscribe({
                if (!it) {
                    Toast.makeText(this, R.string.permission_error, Toast.LENGTH_SHORT).show();finish()
                }
            })
    }

    fun generateKey(): String = Utils.generateID(Utils.findPos(navView.checkedItem?.itemId ?: 0), vp.currentItem)

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_photo -> {
                current = 0
                data.pos.value = 0
                setPhotoFragment()
            }
            R.id.nav_album -> {
                current = 1
                data.pos.value = 1
                setCollectionFragment()
            }
            R.id.nav_search -> {

                startActivity(Intent(this, SearchActivity::class.java))

            }
            R.id.nav_settings -> {

            }
            R.id.nav_favorite -> {
                startActivity(Intent(this, FavoriteActivity::class.java))
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        data.mode.value = kv.decodeInt(generateKey(), 0)
    }

    override fun onClick(v: View?) {

        fbm.close(true)
        when (v?.id) {
            R.id.fab_latest -> {
                kv.encode(generateKey(), 0)
                data.mode.value = 0
            }
            R.id.fab_oldest -> {
                kv.encode(generateKey(), 1)
                data.mode.value = 1
            }
            R.id.fab_popular -> {
                kv.encode(generateKey(), 2)
                data.mode.value = 2
            }
            R.id.fab_random -> {
                kv.encode(generateKey(), 3)
                data.mode.value = 3
            }
        }
        EventBus.getDefault().post(ModeChangeEvent(Utils.findPos(navView.checkedItem?.itemId ?: 0), vp.currentItem))
    }
}
