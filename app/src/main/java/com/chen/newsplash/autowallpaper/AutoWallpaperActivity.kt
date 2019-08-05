package com.chen.newsplash.autowallpaper

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.*
import com.chen.newsplash.NewSplash
import com.chen.newsplash.R
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.LogUtil
import com.tencent.mmkv.MMKV
import java.util.concurrent.TimeUnit

class AutoWallpaperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.menu_auto_wallpaper)
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {
        lateinit var function: SwitchPreference
        lateinit var wifi: CheckBoxPreference
        lateinit var time: Preference
        lateinit var shape: Preference
        lateinit var clip: Preference
        lateinit var screen: Preference
        var isChange = false;

        val preferences = mutableListOf<Preference>()
        val kv = MMKV.defaultMMKV()
        val arrays_interval = NewSplash.context!!.resources.getStringArray(R.array.auto_interval)
        val arrays_shape = NewSplash.context!!.resources.getStringArray(R.array.auto_shape)
        val arrays_clip = NewSplash.context!!.resources.getStringArray(R.array.auto_clip)
        val arrays_screen = NewSplash.context!!.resources.getStringArray(R.array.auto_screen)


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.autowallpaper_preference, rootKey)
            function = findPreference("on_off") as SwitchPreference
            function.setOnPreferenceChangeListener(this)

            wifi = findPreference("wifi") as CheckBoxPreference
            wifi.setOnPreferenceChangeListener(this)

            time = findPreference("time")
            time.onPreferenceClickListener = this

            shape = findPreference("shape")
            shape.onPreferenceClickListener = this

            clip = findPreference("clip")
            clip.onPreferenceClickListener = this

            screen = findPreference("screen")
            screen.onPreferenceClickListener = this

            preferences.add(wifi)
            preferences.add(time)
            preferences.add(shape)
            preferences.add(clip)
            preferences.add(screen)
        }

        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            if ("on_off".equals(preference?.key)) {
                if (!(newValue as Boolean)) {
                    LogUtil.d(this.javaClass, "已关闭自动更换壁纸")
                    WorkManager.getInstance(context!!).cancelAllWorkByTag(Const.TAG)
                }
                kv.encode(Const.AUTO_WALLPAPER, newValue as Boolean)
                loadState(newValue as Boolean)
                isChange = true
            }
            if ("wifi".equals(preference?.key)) {
                kv.encode(Const.AUTO_WIFI, newValue as Boolean)
                isChange = true
            }
            return true
        }

        override fun onDestroy() {
            super.onDestroy()
            if (isChange) {
                LogUtil.d(this.javaClass, "配置发生改变，重新启动自动更换壁纸")
                startAutoWorker()
            }

        }

        override fun onPreferenceClick(preference: Preference?): Boolean {
            showListDialog(preference?.key)
            return true
        }

        private fun showListDialog(key: String?) {
            if (TextUtils.isEmpty(key))
                return
            var opts = arrayOf<String>()
            var checkedItem = 0;
            var title = ""
            when (key) {
                "time" -> {
                    opts = arrays_interval;checkedItem = kv.decodeInt(Const.AUTO_INTERVAL, 2)
                    title = getString(R.string.auto_interval)
                }
                "shape" -> {
                    opts = arrays_shape;checkedItem = kv.decodeInt(Const.AUTO_SHAPE, 1)
                    title = getString(R.string.auto_shape)
                }
                "clip" -> {
                    opts = arrays_clip;checkedItem = kv.decodeInt(Const.AUTO_CLIP, 1)
                    title = getString(R.string.auto_clip)
                }
                "screen" -> {
                    opts = arrays_screen;checkedItem = kv.decodeInt(Const.AUTO_SCREEN, 0)
                    title = getString(R.string.auto_screen)
                }
            }
            var build = androidx.appcompat.app.AlertDialog.Builder(context!!)
            build.setTitle(title)
            build.setCancelable(false)
            build.setSingleChoiceItems(opts, checkedItem, { d, w -> checkedItem = w })
            build.setPositiveButton(R.string.bt_ok) { d, w ->
                when (key) {
                    "time" -> {
                        kv.encode(Const.AUTO_INTERVAL, checkedItem)
                    }
                    "shape" -> {
                        kv.encode(Const.AUTO_SHAPE, checkedItem)
                    }
                    "clip" -> {
                        kv.encode(Const.AUTO_CLIP, checkedItem)
                    }
                    "screen" -> {
                        kv.encode(Const.AUTO_SCREEN, checkedItem)
                    }
                }
                loadSummary()
                isChange = true
            }
            build.setNegativeButton(R.string.bt_return, null)
            build.create().show()
        }

        override fun onResume() {
            super.onResume()
            var isEnable = kv.decodeBool(Const.AUTO_WALLPAPER, false)
            function.isChecked = isEnable
            loadState(isEnable)
            loadSummary()
            if (Build.VERSION.SDK_INT < 24)
                clip.isVisible = false
        }

        private fun loadSummary() {
            time.summary = arrays_interval[kv.decodeInt(Const.AUTO_INTERVAL, 2)]
            shape.summary = arrays_shape[kv.decodeInt(Const.AUTO_SHAPE, 1)]
            clip.summary = arrays_clip[kv.decodeInt(Const.AUTO_CLIP, 1)]
            screen.summary = arrays_screen[kv.decodeInt(Const.AUTO_SCREEN, 0)]
        }

        private fun loadState(isEnable: Boolean) {

            preferences.forEach {
                it.isEnabled = isEnable
            }
        }

        private fun startAutoWorker() {
            if (function.isChecked) {
                LogUtil.d(this.javaClass, "启动自动更换壁纸")
                WorkManager.getInstance(context!!).cancelAllWorkByTag(Const.TAG)
                var constraints: Constraints
                if (wifi.isChecked) {
                    constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()
                } else {
                    constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                }
                var time = kv.decodeInt(Const.AUTO_INTERVAL, 2).toLong()
                when (time) {
                    0L -> {
                        time = 1
                    }
                    1L -> {
                        time = 3
                    }
                    2L -> {
                        time = 6
                    }
                    3L -> {
                        time = 12
                    }
                    4L -> {
                        time = 24
                    }
                }
                var data = Data.Builder()
                    .putInt(Const.AUTO_SHAPE, kv.decodeInt(Const.AUTO_SHAPE, 1))
                    .putInt(Const.AUTO_CLIP, kv.decodeInt(Const.AUTO_CLIP, 1))
                    .putInt(Const.AUTO_SCREEN, kv.decodeInt(Const.AUTO_SCREEN, 0))
                    .build()
                var request = PeriodicWorkRequest.Builder(AutoWallpaperWorker::class.java, time, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS
                    )
                    .setInputData(data)
                    .addTag(Const.TAG)
                    .build()
                WorkManager.getInstance(context!!).enqueue(request)
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.getItemId() == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}