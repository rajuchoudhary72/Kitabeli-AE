package com.kitabeli.ae.utils.ext

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImageFromURL(url: String?, placeholder: Int? = null) {
    if (placeholder != null) {
        Glide.with(this).load(url).thumbnail(
            Glide.with(this.context).load(placeholder)
        ).fitCenter().into(this)
    } else {
        Glide.with(this).load(url).fitCenter()
            .into(this)
    }
}