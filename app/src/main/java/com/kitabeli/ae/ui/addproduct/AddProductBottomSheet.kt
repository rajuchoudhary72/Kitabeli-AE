package com.kitabeli.ae.ui.addproduct

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.BottomSheetAddProductBinding
import com.kitabeli.ae.ui.common.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddProductBottomSheet :
    BaseDialogFragment<AddProductViewModel>(R.layout.bottom_sheet_add_product) {
    private var _binding: BottomSheetAddProductBinding? = null
    private val binding get() = _binding!!

    private val addProductViewModel: AddProductViewModel by viewModels()

    private var productAddListener: (() -> Unit)? = null

    fun setProductAddListener(listener: () -> Unit): AddProductBottomSheet {
        productAddListener = listener
        return this
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            addProductViewModel.showLoading(false)
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri: Uri = data?.data!!
                    val file = File(FileUriUtils.getRealPath(requireContext(), fileUri)!!)
                    addProductViewModel.uploadProductImage(file)
                    binding.productImage.setImageURI(fileUri)
                }

                ImagePicker.RESULT_ERROR -> {
                    showMessage(ImagePicker.getError(data))
                }

                else -> {
                    showMessage("Task Cancelled")
                }
            }
        }

    override fun getViewModel(): AddProductViewModel {
        return addProductViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        super.onViewCreated(view, savedInstanceState)
        _binding = BottomSheetAddProductBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            addProductViewModel = getViewModel()
        }

        binding.icClose.setOnClickListener { dismiss() }

        binding.productImage.setOnClickListener {
            pickProductImage()
        }

        val items = (0..200).map { "Item $it" }
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.products.setAdapter(adapter)


        binding.btnAdd.setOnClickListener {
            if (addProductViewModel.photoProof.value.isNullOrBlank())
                showMessage("Please capture product photo")
            addProductViewModel.addProduct {
                showMessage("Added successfully.")
                dismiss()
            }
        }

    }

    private fun pickProductImage() {
        ImagePicker.with(this)
            .cameraOnly()
            .crop()
            .compress(1024)
            .createIntent { intent ->
                addProductViewModel.showLoading(true)
                startForProfileImageResult.launch(intent)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {

        const val STOCK_OP_NAME_ID = "stockOpNameId"
        fun getInstance(stockOpNameId: Int): AddProductBottomSheet {
            return AddProductBottomSheet().apply {
                arguments = bundleOf(STOCK_OP_NAME_ID to stockOpNameId)
            }
        }
    }
}