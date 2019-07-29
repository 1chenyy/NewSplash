package com.chen.newsplash.mainactivity.databinding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    var mode:MutableLiveData<Int> = MutableLiveData()
    var pos:MutableLiveData<Int> = MutableLiveData()

    init {
        mode.value = 0
        pos.value = 0
    }
}