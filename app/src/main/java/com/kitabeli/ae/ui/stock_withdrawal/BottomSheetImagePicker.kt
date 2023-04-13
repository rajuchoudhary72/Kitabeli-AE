package com.kitabeli.ae.ui.stock_withdrawal

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.BottomSheetSwProofBinding
import com.kitabeli.ae.ui.common.BaseActivity
import com.kitabeli.ae.utils.showGone
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class BottomSheetImagePicker : BottomSheetDialogFragment(R.layout.bottom_sheet_sw_proof) {



    lateinit var binding: BottomSheetSwProofBinding
    private lateinit var file: File
    private lateinit var fileUri: Uri
    private var onButtonClick: ((file: Uri) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSwProofBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
        init()
    }


    private fun initDialog() {
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun init() {

        with(binding) {
            ivClose.setOnClickListener {
                dismiss()
            }

            btnConfirm.setOnClickListener {
                if (btnConfirm.text == "Ambil Foto") {
                    pickProductImage()
                } else {
                    dismiss()
                    onButtonClick?.invoke(fileUri)
                }
            }


            clPreImageSelect.setOnClickListener {
                pickProductImage()
            }
        }
    }

    private fun pickProductImage() {
        ImagePicker.with(this)
            .cameraOnly()
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)

            }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                     fileUri = data?.data!!
                    file = File(FileUriUtils.getRealPath(requireContext(), fileUri)!!)
                    with(binding) {
                        clPreImageSelect.showGone(false)
                        ivSelected.setImageURI(fileUri)
                        tvPhotoName.text = file.name
                        clPostImageSelect.showGone(true)
                        tvPhotoRetake.setOnClickListener {
                            pickProductImage()
                        }
                        btnConfirm.text = "Minta OTP"

                    }
                }

                ImagePicker.RESULT_ERROR -> {
                    showMessage(ImagePicker.getError(data))
                }

                else -> {
                    showMessage(getString(R.string.cancelled_task))
                }
            }
        }

    private fun showMessage(message: String) {
        getBaseActivity().showMessage(message)
    }

    private fun getBaseActivity(): BaseActivity {
        if (requireActivity() is BaseActivity) {
            return requireActivity() as BaseActivity
        } else {
            throw IllegalArgumentException("Your parent activity is not base activity, Please extend Base Activity.")
        }
    }

    companion object {
        fun getInstance(onButtonClick: (file: Uri) -> Unit) = BottomSheetImagePicker().apply {
            this.onButtonClick = onButtonClick
        }
    }
}