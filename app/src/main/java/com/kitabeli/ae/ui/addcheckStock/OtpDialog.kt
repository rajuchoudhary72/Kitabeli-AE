package com.kitabeli.ae.ui.addcheckStock

import `in`.aabhasjindal.otptextview.OTPListener
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.ResignOption
import com.kitabeli.ae.databinding.DialogOtpBinding
import com.kitabeli.ae.ui.common.BaseDialogFragment
import com.kitabeli.ae.utils.showGone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpDialog : BaseDialogFragment<OtpViewModel>(R.layout.dialog_otp) {

    private val otpViewModel: OtpViewModel by viewModels()

    private var _binding: DialogOtpBinding? = null
    private val binding get() = _binding!!

    var otpDialogType: OtpType = OtpDialog.OtpType.STOCK_OPNAME

    private var onOtpSuccessListener: ((String?) -> Unit)? = null
    private var onCloseListener: (() -> Unit)? = null
    private var onResendOTPListener: (() -> Unit)? = null

    private var kioskResignResponse = mapOf<String, List<ResignOption>>()

    private var fromSW = false
    fun setOnOtpSuccessListener(listener: (String?) -> Unit): OtpDialog {
        onOtpSuccessListener = listener
        return this
    }

    fun setOnCloseListener(listener: () -> Unit): OtpDialog {
        onCloseListener = listener
        return this
    }

    fun setOnResendOTPListener(listener: () -> Unit): OtpDialog {
        onResendOTPListener = listener
        return this
    }

    fun setKioskResignResponse(data: Map<String, List<ResignOption>>): OtpDialog {
        kioskResignResponse = data
        return this
    }

    override fun getViewModel(): OtpViewModel {
        return otpViewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        isCancelable = false
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogOtpBinding.bind(view)

        val stockOpReportId = arguments?.getInt(STOCK_OP_REPORT_ID)
        val kioskCode = arguments?.getString(KIOSK_CODE)
        val resignFormId = arguments?.getInt(RESIGN_FORM_ID)
        val stockTransferId = arguments?.getString(STOCK_TRF_ID)

        when (otpDialogType) {
            OtpType.STOCK_OPNAME -> {

                binding.title.text = "ID $stockOpReportId"
                isCancelable = false
            }
            else -> {
                binding.title.text = getString(R.string.konfirmasi_ko)
                binding.message.text = getString(R.string.ask_kiosk_otp_desc)
                binding.icClose.showGone(true)
                isCancelable = true
            }
        }

        binding.icClose.setOnClickListener {
            dismiss()
            onCloseListener?.invoke()
        }

        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.btnSudahTerima.isEnabled = binding.otpView.otp?.length == OTP_LENGTH
            }

            override fun onOTPComplete(otp: String) {}
        }

        binding.btnResendOtp.setOnClickListener {
            dismiss()
            onResendOTPListener?.invoke()
        }

        binding.btnSudahTerima.setOnClickListener {
            binding.otpView.otp?.let { otp ->
                hideKeyboard()
                when (otpDialogType) {
                    OtpType.STOCK_OPNAME -> {
                        otpViewModel.verifyOtp(otp) {
                            onOtpSuccessListener?.invoke(it?.stockTransferId)
                            dismiss()
                        }
                    }
                    OtpType.KIOSK_RESIGN -> {
                        otpViewModel.verifyKioskResignOtp(
                            kioskCode = kioskCode,
                            otp = otp,
                            formId = resignFormId
                        ) {
                            onOtpSuccessListener?.invoke(otp)
                            dismiss()
                        }
                    }
                    OtpType.RETURN_REQUEST -> {
                        otpViewModel.verifyReturnRequestOtp(
                            stockTransferId = stockTransferId,
                            otp = otp,
                        ) {
                            onOtpSuccessListener?.invoke(otp)
                            dismiss()
                        }
                    }
                    OtpType.STOCK_WITHDRAWAL -> {
                        onOtpSuccessListener?.invoke(otp)
                        dismiss()
                    }
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
        onOtpSuccessListener = null
    }

    enum class OtpType {
        STOCK_OPNAME,
        KIOSK_RESIGN,
        STOCK_WITHDRAWAL,
        RETURN_REQUEST,
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    companion object {
        const val STOCK_OP_REPORT_ID = "STOCK_OP_REPORT_ID"
        const val KIOSK_CODE = "KIOSK_CODE"
        const val RESIGN_FORM_ID = "RESIGN_FORM_ID"
        const val STOCK_TRF_ID = "STOCK_TRF_ID"
        const val OTP_LENGTH = 4

        fun getInstance(
            otpType: OtpType = OtpType.STOCK_OPNAME,
            stockOpReportId: Int = 0,
            kioskCode: String? = null,
            resignFormId: Int? = null,
            stockTrfId: String? = null
        ): OtpDialog {
            return OtpDialog().apply {
                otpDialogType = otpType
                arguments = bundleOf(
                    STOCK_OP_REPORT_ID to stockOpReportId,
                    KIOSK_CODE to kioskCode,
                    RESIGN_FORM_ID to resignFormId,
                    STOCK_TRF_ID to stockTrfId,
                )
            }
        }
    }
}