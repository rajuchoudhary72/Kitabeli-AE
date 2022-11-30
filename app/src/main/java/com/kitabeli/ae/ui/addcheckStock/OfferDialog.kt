package com.kitabeli.ae.ui.addcheckStock

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.DialogOfferBinding
import com.kitabeli.ae.ui.common.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OfferDialog : BaseDialogFragment<OfferViewModel>(R.layout.dialog_offer) {

    private val otpViewModel: OfferViewModel by viewModels()

    private var _binding: DialogOfferBinding? = null
    private val binding get() = _binding!!

    private var otpListener: (() -> Unit)? = null
    private var onCancel: (() -> Unit)? = null

    fun setOtpSuccessListener(listener: () -> Unit): OfferDialog {
        otpListener = listener
        return this
    }

    fun setCancelListener(listener: () -> Unit): OfferDialog {
        onCancel = listener
        return this
    }

    override fun getViewModel(): OfferViewModel {
        return otpViewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        isCancelable = false
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogOfferBinding.bind(view)

        binding.icClose.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }

        binding.title.text = "On Platform Sale = Rp ${arguments?.getInt(onPlatformSalesAmountKey)}"
        binding.title2.text =
            "Off Platform Sale = Rp ${arguments?.getInt(offPlatformSalesAmountKey)}"


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
        private const val onPlatformSalesAmountKey = "onPlatformSalesAmount"
        private const val offPlatformSalesAmountKey = "offPlatformSalesAmount"
        fun getInstance(onPlatformSalesAmount: Int, offPlatformSalesAmount: Int): OfferDialog {
            return OfferDialog().apply {
                arguments = bundleOf(
                    onPlatformSalesAmountKey to onPlatformSalesAmount,
                    offPlatformSalesAmountKey to offPlatformSalesAmount
                )
            }
        }
    }
}