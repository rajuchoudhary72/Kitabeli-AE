package com.kitabeli.ae.ui.addcheckStock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.DialogBankAcountAlertBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankAccountAlertDialog : DialogFragment(R.layout.dialog_bank_acount_alert) {

    private var _binding: DialogBankAcountAlertBinding? = null
    private val binding get() = _binding!!

    private var onButtonClick: (() -> Unit)? = null

    fun setOnButtonClickListener(listener: () -> Unit): BankAccountAlertDialog {
        onButtonClick = listener
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        isCancelable = false
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogBankAcountAlertBinding.bind(view)

        binding.btnOk.setOnClickListener {
            onButtonClick?.invoke()
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