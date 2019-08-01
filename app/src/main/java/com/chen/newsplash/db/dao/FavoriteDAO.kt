package com.chen.newsplash.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chen.newsplash.models.photos.Photo
import io.reactivex.Maybe

@Dao
interface FavoriteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotos(vararg photo:Photo):Maybe<List<Long>>

    @Query("select * from photos order by p_id desc limit 10 offset :offset")
    fun queryPhoto(offset:Int):Maybe<List<Photo>>

    @Query("select * from photos where id = :id")
    fun queryPhotoByID(id:String):Maybe<List<Photo>>

    @Query("delete from photos where id = :id")
    fun deletePhotoByID(id:String):Maybe<Int>

    @Query("delete from photos")
    fun deleteAll():Maybe<Int>
}