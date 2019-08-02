package com.chen.newsplash.settingsactivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chen.newsplash.NewSplash
import com.chen.newsplash.R
import com.chen.newsplash.utils.Const
import com.chen.newsplash.utils.Utils
import com.tencent.mmkv.MMKV
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.lang.Exception
import java.text.DecimalFormat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.menu_settings)
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {
        lateinit var clear:Preference
        lateinit var quality:Preference
        var kv = MMKV.defaultMMKV()
        var disposables = CompositeDisposable()
        var opts = NewSplash.context!!.resources.getStringArray(R.array.qulaity_entries)

        override fun onPreferenceClick(preference: Preference?): Boolean {
            when(preference?.key){
                "clear"->{showDeleteCacheDialog() }
                "download"->{openDir()}
                "quality"->{showSelect()}
            }
            return true;
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey)
            clear = findPreference("clear")
            clear.onPreferenceClickListener = this
            findPreference("download").onPreferenceClickListener = this
            quality = findPreference("quality")
            quality.onPreferenceClickListener = this
        }

        override fun onResume() {
            super.onResume()
            clear.summary = Utils.getString(R.string.loading)
            loadSize()
            setQualitySummary()
        }

        override fun onDestroy() {
            super.onDestroy()
            disposables.clear()
        }

        private fun loadSize() {
            disposables.add(
                Maybe.just(context!!.cacheDir)
                    .subscribeOn(Schedulers.io())
                    .map { formatSize(getFolderSize(it)) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({clear.summary = it},{clear.summary = Utils.getString(R.string.none)})
            )
        }

        private fun getFolderSize(dir:File):Long{
            var size = 0L
            try {
                var list = dir.listFiles()
                list.forEach {
                    if (it.isDirectory){
                        size = getFolderSize(it)
                    }else{
                        size+=it.length()
                    }
                }
            }catch (e:Exception){

            }
            return size
        }

        private fun formatSize(size:Long):String{
            var format = DecimalFormat("####.00")
            if(size<1024)
                return "${size} bytes"
            else if (size<1024*1024)
                return format.format(size/1024f)+"KB"
            else if (size<1024*1024*1024)
                return format.format(size/1024f/1024f)+"MB"
            else
                return format.format(size/1024f/1024f/1024f)+"GB"
        }

        private fun deleteFiles(file:File){
            if (file.isDirectory){
                var files = file.listFiles()
                files.forEach {
                    deleteFiles(it)
                }
            }
            file.delete()
        }

        private fun showDeleteCacheDialog(){
            var build = AlertDialog.Builder(context!!)
            build.setTitle(R.string.download_alert_title)
            build.setMessage(R.string.setting_cache_msg)
            build.setCancelable(true)
            build.setPositiveButton(R.string.bt_ok,{d,w->deleteCache()})
            build.setNegativeButton(R.string.bt_cancel,null)
            build.create().show()
        }

        private fun deleteCache(){
            clear.isEnabled = false
            clear.title = Utils.getString(R.string.setting_clearing)
            disposables.add(
                Maybe.just(context!!.cacheDir)
                    .subscribeOn(Schedulers.io())
                    .map {
                        var caches = it.listFiles()
                        caches.forEach { deleteFiles(it) }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnEvent { t1, t2 -> clear.isEnabled = true;loadSize();clear.title = Utils.getString(R.string.setting_clear) }
                    .subscribe()
            )
        }

        private fun openDir(){
            if (!Const.DIR_DOWNLAOD.exists())
                Const.DIR_DOWNLAOD.mkdirs()
            var i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_DEFAULT)
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            var uri = FileProvider.getUriForFile(context!!,"com.chen.code.newsplash.FileProvider",
                Const.DIR_DOWNLAOD
            )

            i.data  =uri
            i.type = "*/*"
            startActivity(i)
        }

        private fun setQualitySummary(){
            var v =kv.decodeInt(Const.DOWNLOAD_QUALITY, -1)
            if (v == -1)
                v = 4
            quality.summary = opts[v]
        }

        private fun showSelect(){
            var build = androidx.appcompat.app.AlertDialog.Builder(context!!)
            build.setCancelable(true)
            build.setTitle(R.string.quality_title)
            var checkedItem = kv.decodeInt(Const.DOWNLOAD_QUALITY, -1)
            if (checkedItem == -1)
                checkedItem = 4
            build.setCancelable(false)
            build.setSingleChoiceItems(opts, checkedItem, { d, w -> checkedItem = w })
            build.setPositiveButton(R.string.bt_ok, { d, w ->
                var v = if (checkedItem == 4) -1 else checkedItem
                kv.encode(Const.DOWNLOAD_QUALITY, v)
                setQualitySummary()
            })
            build.setNegativeButton(R.string.bt_cancel,null)
            build.create().show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.getItemId() == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}