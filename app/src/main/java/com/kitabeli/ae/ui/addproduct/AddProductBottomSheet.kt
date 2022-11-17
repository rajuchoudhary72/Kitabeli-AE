package com.kitabeli.ae.ui.addproduct

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.BottomSheetAddProductBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddProductBottomSheet : DialogFragment(R.layout.bottom_sheet_add_product) {
    private var _binding: BottomSheetAddProductBinding? = null

    private val binding get() = _binding!!


    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri: Uri = data?.data!!
                    val file = File(FileUriUtils.getRealPath(requireContext(), fileUri)!!)
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

    private fun showMessage(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        super.onViewCreated(view, savedInstanceState)
        _binding = BottomSheetAddProductBinding.bind(view)

        binding.icClose.setOnClickListener { dismiss() }

        binding.productImage.setOnClickListener {
            pickProductImage()
        }

        val items = (0..200).map { "Item $it" }
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.products.setAdapter(adapter)

    }

    private fun pickProductImage() {
        ImagePicker.with(this)
            .cameraOnly()
            .crop()
            .compress(1024)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}