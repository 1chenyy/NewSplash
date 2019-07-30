package com.chen.newsplash.photoactivity.adapter

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.ViewPropertyTransition
import com.chen.newsplash.R
import com.chen.newsplash.databinding.ItemRelatedCollectionBinding
import com.chen.newsplash.models.photo.Result
import jp.wasabeef.glide.transformations.internal.Utils


class CollectionAdapter: PagerAdapter() {
    lateinit var results :List<Result>

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
    override fun getCount() = results.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var result = results[position]
        var binding:ItemRelatedCollectionBinding = DataBindingUtil.inflate(LayoutInflater.from(container.context),R.layout.item_related_collection,container,false)
        binding.tvName.text = result.title
        binding.ivPreview.setBackgroundColor(Color.parseColor(result.coverPhoto.color))
        Glide.with(container.context)
            .load(result.coverPhoto.urls.regular)
            .transition(
                GenericTransitionOptions.with(
                    ViewPropertyTransition.Animator {
                        var fade = ObjectAnimator.ofFloat(it,"alpha",0f,1f)
                        fade.duration = 400
                        fade.start()

                    }))
            .into(binding.ivPreview)
        container.addView(binding.root)
        return binding.root
    }
}