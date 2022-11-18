package com.kitabeli.ae.ui.common

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.kitabeli.ae.R
import com.kitabeli.ae.utils.ext.hideSoftInput


abstract class BaseActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    fun showMessage(message: String) {
        hideSoftInput()
        Snackbar.make(
            findViewById(R.id.nav_host_fragment_content_main),
            message,
            Snackbar.LENGTH_SHORT
        )
            .show()
    }

    fun showProgress(visible: Boolean) {
        if (visible) {
            hideSoftInput()
            hideProgressBar()
            progressDialog = showLoadingDialog(this)
        } else {
            hideProgressBar()
        }
    }

    fun hideProgressBar() {
        progressDialog?.cancel()
    }

    fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    private fun showLoadingDialog(context: Context?): ProgressDialog? {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        progressDialog.setContentView(R.layout.spin_view)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
    }
}
