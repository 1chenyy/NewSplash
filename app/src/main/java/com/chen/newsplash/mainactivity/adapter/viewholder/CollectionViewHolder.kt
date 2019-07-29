package com.chen.newsplash.mainactivity.adapter.viewholder

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.ViewPropertyTransition
import com.chen.newsplash.R
import com.chen.newsplash.mainactivity.adapter.CollectionItem
import com.chen.newsplash.utils.Utils
import com.mikepenz.fastadapter.FastAdapter

class CollectionViewHolder(itemView: View): FastAdapter.ViewHolder<CollectionItem>(itemView) {
    override fun bindView(item: CollectionItem, payloads: MutableList<Any>) {
        userName.text = item.collection.user.name
        title.text = item.collection.title
        total.text = "${item.collection.totalPhotos}${Utils.getString(R.string.total_photos)}"

        Glide.with(this.itemView.context)
            .load(item.collection.user.profileImage.medium)
            .placeholder(R.drawable.ic_user_default_small)
            .error(R.drawable.ic_user_default_small)
            .fallback(R.drawable.ic_user_default_small)
            .apply(RequestOptions.circleCropTransform())
            .into(ibUserImage)

        var hight:Float = (Utils.SCREEN_WIDTH * item.collection.coverPhoto.height) / (item.collection.coverPhoto.width.toFloat())
        ivPreview.minimumHeight = hight.toInt()
        ivPreview.setBackgroundColor(Color.parseColor(item.collection.coverPhoto.color))
        Glide.with(this.itemView.context)
            .load(item.collection.coverPhoto.urls.regular)
            .transition(
                GenericTransitionOptions.with(
                    ViewPropertyTransition.Animator {
                        var fade = ObjectAnimator.ofFloat(it,"alpha",0f,1f)
                        fade.duration = 400
                        fade.start()

                    }))
            .into(ivPreview)
    }

    override fun unbindView(item: CollectionItem) {
        userName.text = null
        title.text = null
        title.text = null
        Glide.with(this.itemView.context).clear(ibUserImage)
        Glide.with(this.itemView.context).clear(ivPreview)
    }

    var userName: TextView
    var ibUserImage: ImageButton
    var ivPreview:ImageView
    var title:TextView
    var total:TextView
    init {
        userName = itemView.findViewById(R.id.tv_username)
        ibUserImage = itemView.findViewById(R.id.ib_user)
        title = itemView.findViewById(R.id.tv_title)
        total = itemView.findViewById(R.id.tv_total)
        ivPreview = itemView.findViewById(R.id.iv_preview)
    }
}