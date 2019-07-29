package com.chen.newsplash.mainactivity.adapter.viewholder

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.ViewPropertyTransition
import com.chen.newsplash.NewSplash
import com.chen.newsplash.R
import com.chen.newsplash.mainactivity.adapter.PhotoItem
import com.chen.newsplash.utils.Utils
import com.mikepenz.fastadapter.FastAdapter
import jp.wasabeef.glide.transformations.BitmapTransformation
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoViewHolder(itemView: View): FastAdapter.ViewHolder<PhotoItem>(itemView) {
    override fun bindView(item: PhotoItem, payloads: MutableList<Any>) {
        userName.text = item.photo.user.name

        Glide.with(this.itemView.context)
            .load(item.photo.user.profileImage.medium)
            .placeholder(R.drawable.ic_user_default_small)
            .error(R.drawable.ic_user_default_small)
            .fallback(R.drawable.ic_user_default_small)
            .apply(RequestOptions.circleCropTransform())
            .into(ibUserImage)

        var hight:Float = (Utils.SCREEN_WIDTH * item.photo.height) / (item.photo.width.toFloat())
        ivPreview.minimumHeight = hight.toInt()
        ivPreview.setBackgroundColor(Color.parseColor(item.photo.color))
        Glide.with(this.itemView.context)
            .load(item.photo.urls.regular)
            .transition(GenericTransitionOptions.with(
                ViewPropertyTransition.Animator {
                var fade =ObjectAnimator.ofFloat(it,"alpha",0f,1f)
                fade.duration = 400
                fade.start()

            }))
            .into(ivPreview)

    }

    override fun unbindView(item: PhotoItem) {
        userName.text = null
        Glide.with(this.itemView.context).clear(ibUserImage)
        Glide.with(this.itemView.context).clear(ivPreview)
    }

    var userName:TextView
    var ivPreview:ImageView
    var ibUserImage:ImageButton
    init {
        userName = itemView.findViewById(R.id.tv_username)
        ivPreview = itemView.findViewById(R.id.iv_preview)
        ibUserImage = itemView.findViewById(R.id.ib_user)
    }
}