package com.chen.newsplash.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chen.newsplash.db.dao.FavoriteDAO
import com.chen.newsplash.models.photos.Photo

@Database(entities = [Photo::class], version = 1)
abstract class DBPhoto:RoomDatabase() {
    abstract fun getFavorite(): FavoriteDAO
}
