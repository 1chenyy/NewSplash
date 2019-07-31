package com.chen.newsplash.photoactivity.databinding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chen.newsplash.utils.DownloadState
import com.chen.newsplash.utils.LoadingState

class PhotoActivityViewModel : ViewModel(){
    var state = MutableLiveData<LoadingState>()
    var time = MutableLiveData<String>()
    var eye = MutableLiveData<String>()
    var download = MutableLiveData<String>()
    var favorite = MutableLiveData<String>()
    var downloadState = MutableLiveData<DownloadState>()

    init {
        state.value = LoadingState.LOADING
        downloadState.value = DownloadState.NO_DOWNLOAD
    }
}