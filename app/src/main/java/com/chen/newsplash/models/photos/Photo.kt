package com.chen.newsplash.models.photos


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true)
    var p_id:Int,

    @SerializedName("color")
    var color: String,


    @SerializedName("height")
    var height: Int,
    @SerializedName("width")
    var width: Int,

    @SerializedName("id")
    var id: String,

    @Embedded
    @SerializedName("urls")
    var urls: Urls,
    @Embedded
    @SerializedName("user")
    var user: User

) : Serializable