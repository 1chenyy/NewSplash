package com.chen.newsplash.mainactivity.databinding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chen.newsplash.utils.LoadingState

class PhotoViewModel : ViewModel() {
    var state : MutableLiveData<LoadingState> = MutableLiveData()
    init {
        state.value = LoadingState.LOADING
    }
}