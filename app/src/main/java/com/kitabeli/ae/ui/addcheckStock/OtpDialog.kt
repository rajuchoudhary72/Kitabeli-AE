package com.kitabeli.ae.ui.addcheckStock

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.DialogOtpBinding
import com.kitabeli.ae.ui.common.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import `in`.aabhasjindal.otptextview.OTPListener

@AndroidEntryPoint
class OtpDialog : BaseDialogFragment<OtpViewModel>(R.layout.dialog_otp) {

    private val otpViewModel: OtpViewModel by viewModels()

    private var _binding: DialogOtpBinding? = null
    private val binding get() = _binding!!

    private var otpListener: (() -> Unit)? = null
    private var onCancel: (() -> Unit)? = null

    fun setOtpSuccessListener(listener: () -> Unit): OtpDialog {
        otpListener = listener
        return this
    }

    fun setCancelListener(listener: () -> Unit): OtpDialog {
        onCancel = listener
        return this
    }

    override fun getViewModel(): OtpViewModel {
        return otpViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        isCancelable = false
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogOtpBinding.bind(view)

        arguments?.getInt(ID)?.let { id ->
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
                otpViewModel.verifyOtp(otp) {
                    otpListener?.invoke()
                    dismiss()
                }
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
        const val ID = "id"
        fun getInstance(id: Int): OtpDialog {
            return OtpDialog().apply {
                arguments = bundleOf(
                    ID to id
                )
            }
        }
    }
}