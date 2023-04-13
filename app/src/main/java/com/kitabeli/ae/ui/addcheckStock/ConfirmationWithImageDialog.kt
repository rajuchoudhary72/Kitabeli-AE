package com.kitabeli.ae.ui.addcheckStock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.DialogMaxPaymentWarningBinding
import com.kitabeli.ae.utils.showGone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmationWithImageDialog : DialogFragment(R.layout.dialog_max_payment_warning) {

    private var _binding: DialogMaxPaymentWarningBinding? = null
    private val binding get() = _binding!!

    private var onButtonClick: (() -> Unit)? = null
    private var title: String? = null
    private var subTitle: String? = null
    private var cancelButtonText: String? = null
    private var confirmButtonText: String? = null
    private var showCancelButton: Boolean = false

    fun setOnButtonClickListener(listener: () -> Unit): ConfirmationWithImageDialog {
        onButtonClick = listener
        return this
    }

    fun setContent(
        title: String?,
        subTitle: String?,
        cancelButtonText: String?,
        confirmButtonText: String?,
        showCancelButton: Boolean
    ): ConfirmationWithImageDialog {
        this.title = title
        this.subTitle = subTitle
        this.cancelButtonText = cancelButtonText
        this.confirmButtonText = confirmButtonText
        this.showCancelButton = showCancelButton
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        isCancelable = false
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogMaxPaymentWarningBinding.bind(view)

        title?.let { binding.tvTitle.text = it }
        subTitle?.let { binding.tvSubtitle.text = it }
        cancelButtonText?.let { binding.btnCancel.text = it }
        confirmButtonText?.let { binding.btnConfirm.text = it }

        binding.btnCancel.showGone(showCancelButton)

        binding.btnConfirm.setOnClickListener {
            onButtonClick?.invoke()
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        onButtonClick = null
    }
}