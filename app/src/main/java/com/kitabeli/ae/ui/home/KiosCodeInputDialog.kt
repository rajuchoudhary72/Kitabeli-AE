package com.kitabeli.ae.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.DialogKiosCodeInputBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KiosCodeInputDialog : DialogFragment(R.layout.dialog_kios_code_input) {

    private var _binding: DialogKiosCodeInputBinding? = null
    private val binding get() = _binding!!

    private var codeInputListener: ((String) -> Unit)? = null

    fun setCodeInputListener(listener: (String) -> Unit): KiosCodeInputDialog {
        codeInputListener = listener
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        super.onViewCreated(view, savedInstanceState)
        _binding = DialogKiosCodeInputBinding.bind(view)

        binding.kiosCode.doAfterTextChanged {
            binding.btnOk.isEnabled = it.toString().isNullOrEmpty().not()
        }

        binding.icClose.setOnClickListener {
            dismiss()
        }

        binding.kiosCode.doAfterTextChanged {
            binding.btnOk.isEnabled = it?.isNotEmpty() ?: false
        }

        binding.btnOk.setOnClickListener {
            val kiosCode = binding.kiosCode.text.toString()
            codeInputListener?.invoke(kiosCode)
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