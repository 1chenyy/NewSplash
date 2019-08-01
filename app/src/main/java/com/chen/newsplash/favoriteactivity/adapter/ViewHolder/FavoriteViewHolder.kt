package com.chen.newsplash.favoriteactivity.adapter.ViewHolder

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.ViewPropertyTransition
import com.chen.newsplash.R
import com.chen.newsplash.favoriteactivity.adapter.FavoriteItem
import com.chen.newsplash.utils.Utils
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class FavoriteViewHolder(var itemview: View) : FastAdapter.ViewHolder<FavoriteItem>(itemview) {
    override fun bindView(item: FavoriteItem, payloads: MutableList<Any>) {
        iv.setBackgroundColor(Color.parseColor(item.photo.color))
        var multi = MultiTransformation<Bitmap>(
            CropTransformation(Utils.SCREEN_WIDTH, Utils.dpToPx(100f)),
            RoundedCornersTransformation(25,0)

        )
        Glide.with(itemview.context)
            .load(item.photo.urls.regular)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(multi))
            .transition(
                GenericTransitionOptions.with(
                    ViewPropertyTransition.Animator {
                        var fade = ObjectAnimator.ofFloat(it,"alpha",0f,1f)
                        fade.duration = 400
                        fade.start()

                    }))
            .into(object : SimpleTarget<Drawable>(){
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    iv.setImageDrawable(resource)
                    iv.setBackgroundColor(Color.TRANSPARENT)
                }

            })
    }

    override fun unbindView(item: FavoriteItem) {

    }

    var iv: ImageView

    init {
        iv = itemview.findViewById(R.id.iv)
    }
}