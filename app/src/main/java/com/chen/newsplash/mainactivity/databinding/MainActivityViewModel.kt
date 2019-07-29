package com.chen.newsplash.mainactivity.databinding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    var mode:MutableLiveData<Int> = MutableLiveData()
    var modeLabel:MutableLiveData<String> = MutableLiveData()


}