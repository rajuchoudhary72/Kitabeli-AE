package com.kitabeli.ae.ui.addcheckStock

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.DialogConfirmationBinding
import com.kitabeli.ae.utils.showGone
import com.kitabeli.ae.utils.showHide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmationDialog : DialogFragment(R.layout.dialog_confirmation) {

    private var _binding: DialogConfirmationBinding? = null
    private val binding get() = _binding!!

    private var onConfirm: (() -> Unit)? = null
    private var onCancel: (() -> Unit)? = null

    private var title: String? = null
    private var message: String? = null
    private var confirmButtonText: String? = null
    private var cancelButtonText: String? = null
    private var partialPaymentAmt: String? = null

    fun setCancelListener(listener: () -> Unit): ConfirmationDialog {
        onCancel = listener
        return this
    }

    fun setConfirmListener(listener: () -> Unit): ConfirmationDialog {
        onConfirm = listener
        return this
    }

    fun setContent(
        title: String? = null,
        message: String? = null,
        confirmButtonText: String? = null,
        cancelButtonText: String? = null,
        partialPaymentAmount: String? = null,
    ): ConfirmationDialog {
        this.title = title
        this.message = message
        this.confirmButtonText = confirmButtonText
        this.cancelButtonText = cancelButtonText
        this.partialPaymentAmt = partialPaymentAmount
        return this
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        isCancelable = false
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogConfirmationBinding.bind(view)

        title?.let { binding.title.text = it }
        message?.let { binding.message.text = it }
        confirmButtonText?.let { binding.btnSudahTerima.text = it }

        if (partialPaymentAmt.isNullOrBlank().not()) {
            binding.clPartialAmt.showGone(true)
            binding.tvPartialAmt.text = "Rp.${partialPaymentAmt}"
        } else {
            binding.clPartialAmt.showGone(false)
        }

        if (cancelButtonText.isNullOrBlank()) {
            binding.btnBelum.showHide(true)
        }
        cancelButtonText?.let { binding.btnBelum.text = it }

        binding.icClose.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }

        binding.btnSudahTerima.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
        binding.btnBelum.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        onConfirm = null
    }
}