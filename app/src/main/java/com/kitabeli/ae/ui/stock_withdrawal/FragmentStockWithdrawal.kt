package com.kitabeli.ae.ui.stock_withdrawal

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.kitabeli.ae.databinding.FragmentKiosStockWithdrawalBinding
import com.kitabeli.ae.ui.addcheckStock.OtpDialog
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.utils.showGone
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FragmentStockWithdrawal : BaseFragment<StockWithdrawalViewModel>() {

    private lateinit var binding: FragmentKiosStockWithdrawalBinding
    private val stockWithdrawalViewModel: StockWithdrawalViewModel by viewModels()

    private val args: FragmentStockWithdrawalArgs by navArgs()

    private val stockWithdrawalAdapter: StockWithdrawalAdapter by lazy {
        StockWithdrawalAdapter(updateBottomText = { checked, left ->
            updateBottomView(checked, left)
        }, onFullImageClick = {
            showFullScreenImage(it)
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentKiosStockWithdrawalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getViewModel(): StockWithdrawalViewModel {
        return stockWithdrawalViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        callAPI()

    }

    private fun callAPI() {
        stockWithdrawalViewModel.getStockWithdrawalItems(args.stockTransferId) {
            stockWithdrawalAdapter.setData(it?.itemDetailDTOS.orEmpty())
        }
    }

    private fun initData() = with(binding) {

        rvStockWithdrawal.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stockWithdrawalAdapter
        }

        btn.setOnClickListener {
            BottomSheetImagePicker.getInstance(onButtonClick = { fileUri ->
                OtpDialog
                    .getInstance(otpType = OtpDialog.OtpType.STOCK_WITHDRAWAL)
                    .setOnOtpSuccessListener { otp ->
                        stockWithdrawalViewModel.submitOTP(
                            args.stockTransferId,
                            otp.orEmpty(),
                            File(FileUriUtils.getRealPath(requireContext(), fileUri)!!)
                        ) {
                            if (it?.status?.httpCode == "200") {
                                binding.clPartialPaymentInfo.showGone(false)
                                stockWithdrawalAdapter.fromOTPSuccess(true, fileUri)
                                clSwBottomView.showGone(false)
                            }
                        }

                    }
                    .show(childFragmentManager, "OTP")
            }).show(childFragmentManager, "image_picker")
        }

    }

    private fun updateBottomView(checked: String?, left: String?) = with(binding) {
        if (checked == null) {
            clSwBottomView.showGone(false)
        } else {
            clSwBottomView.showGone(true)
        }

        btn.isEnabled = left == "(Semua stok ditarik)"
        tvSwRemaining.text = left
        tvSwTotalChecked.text = checked

    }

    private fun showFullScreenImage(uri: Uri) = with(binding) {

        ivFullProof.setImageURI(uri)
        clFullProof.showGone(true)

        ivCancel.setOnClickListener {
            clFullProof.showGone(false)
        }
    }
}