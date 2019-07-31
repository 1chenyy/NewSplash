package com.chen.newsplash.searchactivity.databinding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chen.newsplash.utils.LoadingState

class SearchActivityViewModel:ViewModel() {
    var state = MutableLiveData<LoadingState>()
    var query = MutableLiveData<String>()
    init {
        state.value = LoadingState.LOADING_NO_DATA
    }
}