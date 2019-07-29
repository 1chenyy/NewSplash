package com.chen.newsplash.mainactivity

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.chen.newsplash.R
import com.chen.newsplash.databinding.ActivityMainBinding
import com.chen.newsplash.mainactivity.adapter.PagerAdapter
import com.chen.newsplash.mainactivity.databinding.MainActivityViewModel
import com.chen.newsplash.mainactivity.fragment.PhotoFragment
import com.chen.newsplash.models.event.ModeChangeEvent
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.Utils
import com.github.clans.fab.FloatingActionMenu
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ViewPager.OnPageChangeListener, View.OnClickListener {


    lateinit var binding:ActivityMainBinding
    lateinit var data:MainActivityViewModel
    lateinit var navView: NavigationView
    var current:Int = 0
    lateinit var vp:ViewPager
    lateinit var adapter: PagerAdapter
    lateinit var fbm:FloatingActionMenu
    var kv = MMKV.defaultMMKV()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        data = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        binding.lifecycleOwner = this
        binding.data = data
        configDrawerLayoutAndToolBar()
        initPhotoViewPager()
        data.mode.value = kv.decodeInt(generateKey(),0)
    }

    private fun initPhotoViewPager() {
        vp = binding.main.vpContent
        adapter = PagerAdapter(supportFragmentManager)
        adapter.addItem(PhotoFragment.newInstance(Const.TYPE_COMMON,Const.POS_PHOTO),getString(R.string.page_normal))
        adapter.addItem(PhotoFragment.newInstance(Const.TYPE_CURATED,Const.POS_PHOTO),getString(R.string.page_curated))
        vp.adapter = adapter
        binding.main.tab.setupWithViewPager(vp)
        vp.addOnPageChangeListener(this)

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

    fun generateKey():String = Utils.generateID(Utils.findPos(navView.checkedItem?.itemId?:0),vp.currentItem)

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        current = item.itemId
        when (current) {
            R.id.nav_photo -> {

            }
            R.id.nav_album -> {

            }
            R.id.nav_search -> {

            }
            R.id.nav_settings -> {

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
        data.mode.value =  kv.decodeInt(generateKey(),0)
    }

    override fun onClick(v: View?) {

        fbm.close(true)
        when (v?.id) {
            R.id.fab_latest -> {
                kv.encode(generateKey(),0)
                data.mode.value = 0
            }
            R.id.fab_oldest -> {
                kv.encode(generateKey(),1)
                data.mode.value = 1
            }
            R.id.fab_popular -> {
                kv.encode(generateKey(),2)
                data.mode.value = 2
            }
            R.id.fab_random -> {
                kv.encode(generateKey(),3)
                data.mode.value = 3
            }
        }
        EventBus.getDefault().post(ModeChangeEvent(Utils.findPos(navView.checkedItem?.itemId?:0),vp.currentItem))
    }
}
