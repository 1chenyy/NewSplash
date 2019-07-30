package com.chen.newsplash.collectionactivity.databinding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chen.newsplash.utils.LoadingState

class CollectionActivityViewModel:ViewModel() {
    var state = MutableLiveData<LoadingState>()
    var userAndName = MutableLiveData<String>()
    init {
        state.value = LoadingState.LOADING
    }
}