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

    private var codeInputListener: ((String) -> Unit)? = null

    fun setCodeInputListener(listener: (String) -> Unit): ConfirmationDialog {
        codeInputListener = listener
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogConfirmationBinding.bind(view)

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