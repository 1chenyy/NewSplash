package com.chen.newsplash.utils

import android.os.Environment
import com.chen.newsplash.R
import java.io.File

object Const {
    val TYPE_COMMON = 0
    val TYPE_CURATED = 1
    val TYPE_LIKE = 2
    val POS_PHOTO = 0
    val POS_COLLECTION = 1
    val ARG_TYPE = "type"
    val ARG_POS = "pos"
    val ORDER_LATEST = "latest"
    val ORDER_OLDEST = "oldest"
    val ORDER_POPULAR = "popular"
    val BASE_URL = "https://api.unsplash.com/"
    val HEAD_LINK = "link"
    val LINK_FIRST = "rel=\"first\""
    val LINK_PREV = "rel=\"prev\""
    val LINK_LAST = "rel=\"last\""
    val LINK_NEXT = "rel=\"next\""
    val ARG_PAGE_COUNT = 10
    val MODEL_LATEST = 0
    val MODEL_OLDEST = 1
    val MODEL_POPULAR = 2
    val MODEL_RANDOM = 3
    val MODEL_LATEST_ARG = "latest"
    val MODEL_OLDEST_ARG = "oldest"
    val MODEL_POPULAR_ARG = "popular"
    val MODEL_RANDOM_ARG = "random"
    val TAG_PHOTO_COMMON = "photo"
    val TAG_PHOTO_CURATED = "photo_curated"
    val TAG_COLLECTION_COMMON = "collection"
    val TAG_COLLECTION_CURATED = "collection_curated"
    val ARG_PHOTO = "photo"
    val ARG_ARG = "arg"
    val LIST_EXIF_ICON = mutableListOf(
        R.drawable.ic_store_mall_directory_black_24dp, R.drawable.ic_photo_camera_black_24dp,
        R.drawable.ic_aspect_ratio_black_24dp, R.drawable.ic_center_focus_strong_black_24dp,
        R.drawable.ic_filter_tilt_shift_black_24dp, R.drawable.ic_timelapse_black_24dp,
        R.drawable.ic_exposure_black_24dp, R.drawable.ic_color_lens_black_24dp
    )
    val LIST_EXIF_TITLE = mutableListOf(
        R.string.exif_manufacturer,R.string.exif_model,R.string.exif_size,R.string.exif_focal,
        R.string.exif_aperture,R.string.exif_exposure,R.string.exif_iso,R.string.exif_color
    )
    val ARG_NAME = "name"
    val ARG_USERNAME = "username"
    val LIST_ORIENTATION = mutableListOf("","portrait","landscape","squarish")
    val DIR_DOWNLAOD = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"NewSplash")
    val DIR_DOWNLAOD_NAME = "NewSplash"
    val DOWNLOAD_RAW = "0"
    val DOWNLOAD_FULL = "1"
    val DOWNLOAD_REGULAR = "2"
    val DOWNLOAD_SMALL = "3"
    val DOWNLOAD_QUALITY = "quality"
    val DOWNLOAD_NET_TYPE = "net_type"
}