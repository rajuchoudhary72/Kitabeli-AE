package com.kitabeli.ae.ui.addcheckStock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.DialogOtpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpDialog : DialogFragment(R.layout.dialog_otp) {

    private var _binding: DialogOtpBinding? = null
    private val binding get() = _binding!!

    private var codeInputListener: ((String) -> Unit)? = null

    fun setCodeInputListener(listener: (String) -> Unit): OtpDialog {
        codeInputListener = listener
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogOtpBinding.bind(view)

        binding.icClose.setOnClickListener {
            dismiss()
        }

        binding.btnSudahTerima.setOnClickListener {
            codeInputListener?.invoke("234")
            dismiss()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        codeInputListener = null
    }
}