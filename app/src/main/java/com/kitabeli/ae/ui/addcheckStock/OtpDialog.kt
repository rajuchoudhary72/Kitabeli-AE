package com.kitabeli.ae.ui.addcheckStock

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.DialogOtpBinding
import dagger.hilt.android.AndroidEntryPoint
import `in`.aabhasjindal.otptextview.OTPListener

@AndroidEntryPoint
class OtpDialog : DialogFragment(R.layout.dialog_otp) {

    private var _binding: DialogOtpBinding? = null
    private val binding get() = _binding!!

    private var otpListener: ((String) -> Unit)? = null
    private var onCancel: (() -> Unit)? = null

    fun setOtpListener(listener: (String) -> Unit): OtpDialog {
        otpListener = listener
        return this
    }

    fun setCancelListener(listener: () -> Unit): OtpDialog {
        onCancel = listener
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        isCancelable = false
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogOtpBinding.bind(view)

        arguments?.getString(ID)?.let { id ->
            binding.title.text = "ID $id"
        }

        binding.icClose.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }

        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.btnSudahTerima.isEnabled = binding.otpView.otp?.length == 4
            }

            override fun onOTPComplete(otp: String) {

            }
        }

        binding.btnSudahTerima.setOnClickListener {
            binding.otpView.otp?.let { otp ->
                otpListener?.invoke(otp)
                dismiss()
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        otpListener = null
    }

    companion object {
        private const val ID = "id"
        fun getInstance(id: String): OtpDialog {
            return OtpDialog().apply {
                arguments = bundleOf(
                    ID to id
                )
            }
        }
    }
}