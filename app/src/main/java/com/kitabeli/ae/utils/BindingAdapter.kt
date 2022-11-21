package com.kitabeli.ae.utils

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.google.android.material.textfield.TextInputLayout
import com.kitabeli.ae.model.AppError
import com.kitabeli.ae.utils.ext.stringRes
import com.kitabeli.ae.utils.ext.toApiError

@BindingAdapter("errorText")
fun TextInputLayout.setTextInputLayoutError(error: Int?) {
    when (error) {
        null -> {
            isErrorEnabled = false
        }

        0 -> {
            isErrorEnabled = false
        }

        else -> {
            setError(context.getString(error))
        }
    }
}


@BindingAdapter("isVisible")
fun View.showGone(show: Boolean) {
    isVisible = show
}

@BindingAdapter("isVisibleWithAnimation")
fun View.showGoneWithAnimation(show: Boolean) {
    val transition = Fade()
    transition.duration = 300
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(parent as ViewGroup, transition)
    isVisible = show
}

@BindingAdapter("isInvisible")
fun View.showHide(invisible: Boolean) {
    isInvisible = invisible
}

@BindingAdapter("errorMessage")
fun TextView.errorMessage(appError: AppError?) {
    appError ?: return
    text = when (appError) {
        is AppError.ApiException.BadRequestException -> {
            val apiError = appError.toApiError()
            apiError.message
        }

        else -> {
            context.getString(appError!!.stringRes())
        }
    }
}