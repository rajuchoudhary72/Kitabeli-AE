package com.kitabeli.ae.utils

import android.text.TextUtils
import android.util.Patterns

fun String?.isValidEmailAddress(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}