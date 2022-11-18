package com.kitabeli.ae.utils

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

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