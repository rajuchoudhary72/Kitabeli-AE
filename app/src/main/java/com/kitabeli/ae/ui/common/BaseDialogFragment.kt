package com.kitabeli.ae.ui.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.model.AppError
import com.kitabeli.ae.utils.SingleLiveObserver
import com.kitabeli.ae.utils.ext.stringRes
import com.kitabeli.ae.utils.ext.toApiError

abstract class BaseDialogFragment<M : BaseViewModel>(layoutId: Int) : DialogFragment(layoutId) {

    abstract fun getViewModel(): M

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCommonObserver()
    }

    private fun setupCommonObserver() {
        getViewModel().errorLiveData.observe(
            viewLifecycleOwner,
            SingleLiveObserver { appError ->
                when (appError) {
                    is AppError.ApiException.BadRequestException -> {
                        val apiError = appError.toApiError()
                        showMessage(apiError.message ?: getString(R.string.error_unknown))
                    }

                    else -> {
                        showMessage(getString(appError.stringRes()))
                    }
                }
            }
        )

        getViewModel().messageLiveData.observe(
            viewLifecycleOwner,
            SingleLiveObserver { message ->
                showMessage(message)
            }
        )

        getViewModel().loadingLiveData.observe(
            viewLifecycleOwner,
            SingleLiveObserver { visible ->
                showProgress(visible)
            }
        )
    }


    fun showMessage(message: String) {
        getBaseActivity().showMessage(message)
    }

    private fun getBaseActivity(): BaseActivity {
        if (requireActivity() is BaseActivity) {
            return requireActivity() as BaseActivity
        } else {
            throw IllegalArgumentException("Your parent activity is not base activity, Please extend Base Activity.")
        }
    }

    fun onError(appError: AppError) {
        getViewModel().onError(appError)
    }

    fun showProgress(visible: Boolean) {
        getBaseActivity().showProgress(visible)
    }

    fun hideProgressBar() {
        getBaseActivity().hideProgressBar()
    }

    fun showToast(message: String?) {
        getBaseActivity().showToast(message)
    }
}