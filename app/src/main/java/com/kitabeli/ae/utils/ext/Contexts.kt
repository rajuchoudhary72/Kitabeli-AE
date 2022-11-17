package com.kitabeli.ae.utils.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.provider.Settings.Secure
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.getSystemService
import androidx.core.content.res.use
import androidx.databinding.adapters.Converters
import androidx.fragment.app.Fragment

/**
 * Used to get current theme Color
 */
@ColorInt
fun Context.getThemeColor(
    @AttrRes themeAttrId: Int,
): Int {
    return obtainStyledAttributes(intArrayOf(themeAttrId))
        .use {
            it.getColor(0, Color.MAGENTA)
        }
}

/**
 * Used to get current theme Color Drawable
 */
fun Context.getThemeColorDrawable(
    @AttrRes themeAttrId: Int,
): Drawable {
    return obtainStyledAttributes(intArrayOf(themeAttrId))
        .use {
            it.getColor(0, Color.MAGENTA)
        }.let {
            Converters.convertColorToDrawable(it)
        }
}

/**
 * Check that current theme id Night mode or Not
 */
fun Context.isNightMode(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

/**
 * Used to Hide Keyboard
 */
fun Activity.hideSoftInput() {
    val imm: InputMethodManager? = getSystemService()
    val currentFocus = currentFocus
    if (currentFocus != null && imm != null) {
        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}

/**
 * Used to Hide Keyboard
 */
fun Activity.showSoftInput() {
    val imm: InputMethodManager? = getSystemService()
    val currentFocus = currentFocus
    if (currentFocus != null && imm != null) {
        imm.showSoftInput(currentFocus, InputMethodManager.SHOW_IMPLICIT)
    }
}

/**
 * Used to Hide Keyboard
 */
fun Fragment.hideSoftInput() = requireActivity().hideSoftInput()

fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

@SuppressLint("HardwareIds")
fun Context.getDeviceId(): String {
    return Secure.getString(contentResolver, Secure.ANDROID_ID)
}
