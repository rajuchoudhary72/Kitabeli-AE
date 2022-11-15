package com.kitabeli.ae.ui.addproduct

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.BottomSheetAddProductBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductBottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet_add_product) {
    private var _binding: BottomSheetAddProductBinding? = null

    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = BottomSheetAddProductBinding.bind(view)

        binding.icClose.setOnClickListener { dismiss() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}