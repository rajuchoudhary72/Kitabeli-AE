package com.kitabeli.ae.ui.addcheckStock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.DialogConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmationDialog : DialogFragment(R.layout.dialog_confirmation) {

    private var _binding: DialogConfirmationBinding? = null
    private val binding get() = _binding!!

    private var submitReport: (() -> Unit)? = null


    private var onCancel: (() -> Unit)? = null

    fun setCancelListener(listener: () -> Unit): ConfirmationDialog {
        onCancel = listener
        return this
    }

    fun setSubmitReportListener(listener: () -> Unit): ConfirmationDialog {
        submitReport = listener
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        isCancelable = false
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogConfirmationBinding.bind(view)

        binding.icClose.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }

        binding.btnSudahTerima.setOnClickListener {
            submitReport?.invoke()
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
        submitReport = null
    }
}