package com.kitabeli.ae.utils.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings.Secure
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.content.res.use
import androidx.databinding.adapters.Converters
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kitabeli.ae.R
import com.kitabeli.ae.ui.addcheckStock.AddCheckStockFragmentDirections
import com.kitabeli.ae.utils.Constants.CS_WHATSAPP_NUMBER

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

@SuppressLint("HardwareIds")
fun Context.getDeviceId(): String {
    return Secure.getString(contentResolver, Secure.ANDROID_ID)
}

fun Activity.openWhatsAppSupport() {
    val intentWhatsapp = Intent(Intent.ACTION_VIEW)
    intentWhatsapp.data = Uri.parse("whatsapp://send?phone=${CS_WHATSAPP_NUMBER}")
    intentWhatsapp.setPackage("com.whatsapp")
    try {
        this.startActivity(intentWhatsapp)
    } catch (exception: Exception) {
        this.toast("Whatsapp tidak terpasang")
    }
}

fun Context?.toast(text: String, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, text, duration).show() }

fun Context.copyToClipboard(text: String) {
    val clipBoard: ClipboardManager =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("kb-ae", text)
    clipBoard.setPrimaryClip(clipData)
}

fun Context.showLogoutDialog(onLogoutClicked: () -> Unit) {
    MaterialAlertDialogBuilder(this)
        .setTitle(getString(R.string.logout_))
        .setMessage(getString(R.string.logout_msg))
        .setBackground(ContextCompat.getDrawable(this, R.drawable.bg_dialog))
        .setPositiveButton(getString(R.string.logout_)) { _, _ ->
            onLogoutClicked.invoke()
        }
        .setNegativeButton(getString(R.string.cancel)) { _, _ ->

        }
        .show()
}