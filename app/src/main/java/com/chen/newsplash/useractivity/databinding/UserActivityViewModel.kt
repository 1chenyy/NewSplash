package com.chen.newsplash.useractivity.databinding

import android.text.SpannableString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chen.newsplash.R
import com.chen.newsplash.utils.LoadingState
import com.chen.newsplash.utils.Utils

class UserActivityViewModel:ViewModel() {
    var state = MutableLiveData<LoadingState>()
    var location = MutableLiveData<String>()
    var bio = MutableLiveData<String>()
    init {
        state.value = LoadingState.LOADING
        location.value = Utils.getString(R.string.loading)
    }
}