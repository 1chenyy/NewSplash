package com.chen.newsplash.favoriteactivity.databinding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chen.newsplash.utils.LoadingState

class FavoriteActivityViewModel:ViewModel() {
    var state = MutableLiveData<LoadingState>()
    init {
        state.value = LoadingState.LOADING
    }
}