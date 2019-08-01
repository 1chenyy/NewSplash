package com.chen.newsplash.models.db

import androidx.room.PrimaryKey

data class Favorite(
    @PrimaryKey(autoGenerate = true)
    var id:Int
) {
}