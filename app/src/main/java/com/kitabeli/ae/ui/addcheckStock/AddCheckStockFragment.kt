package com.kitabeli.ae.ui.addcheckStock

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.FragmentAddCheckStockBinding
import com.kitabeli.ae.ui.cashCollection.CancelCashCollectionBottomSheet
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.ui.signature.SignatureFragment
import com.kitabeli.ae.utils.showGone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AddCheckStockFragment : BaseFragment<AddCheckStockViewModel>() {
    private var _binding: FragmentAddCheckStockBinding? = null

    private val mViewModel: AddCheckStockViewModel by viewModels()
    private val binding get() = _binding!!

    @Inject
    lateinit var stockItemAdapter: StockItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddCheckStockBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            addCheckStockViewModel = mViewModel
        }

        return binding.root
    }

    override fun getViewModel(): AddCheckStockViewModel {
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = stockItemAdapter.apply {
            onItemClickListener = { stockItem ->
                AddCheckStockItemDetailDialog
                    .getInstance(
                        itemName = stockItem.itemName.orEmpty(),
                        orderList = stockItem.kioskOrderItemDetailsDTOList.orEmpty(),
                        offPlatformQty = stockItem.offPlatformSaleQuantity ?: 0,
                        offPlatformAmount = stockItem.offPlatformSaleAmount ?: 0L,
                        onPlatformAmount = stockItem.onPlatformSaleAmount ?: 0L
                    ).show(
                        childFragmentManager,
                        AddCheckStockItemDetailDialog::class.java.simpleName
                    )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel
                .uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiState ->
                    if (uiState is UiState.Success) {
                        stockItemAdapter.submitList(uiState.report?.stockOPNameReportItemDTOs)
                        binding.price.text = "Rp ${uiState.report?.totalAmountToBePaid}"

                        if (uiState.report?.status == "OTP_GENERATED") {
                            collectOpt()
                        }

                        if (uiState.report?.status == "PAYMENT_COMPLETED" || uiState.report?.status == "CANCELLED") {
                            navigateToHome()
                        }
                    }
                }
        }

        binding.btnCancel.setOnClickListener {
            CancelCashCollectionBottomSheet.getInstance(
                onButtonClick = { reason, note ->
                    mViewModel.cancelReport(cancelReason = reason, note = note) {
                        navigateToHome()
                    }
                }
            ).show(childFragmentManager, CancelCashCollectionBottomSheet::class.java.simpleName)
        }

        binding.btn.setOnClickListener {
            askForConfirmation()
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.signMitra.setOnClickListener {
            collectSignature(true)
        }

        binding.signAe.setOnClickListener {
            collectSignature(false)
        }

        binding.checkboxEditName.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                mViewModel.statusMitraName.value = binding.edtMitraName.text.toString().isNotEmpty()
                binding.mitra.showGone(false)
                binding.edtMitraName.showGone(true)
                binding.txtKiosNameSubtitle.text =
                    context?.getText(R.string.pemilik_kios_selected_subtitle)
            } else {
                mViewModel.statusMitraName.value = true
                binding.mitra.showGone(true)
                binding.edtMitraName.showGone(false)
                binding.txtKiosNameSubtitle.text = context?.getText(R.string.pemilik_kios_subtitle)
            }
        }

        binding.edtMitraName.doOnTextChanged { text, start, before, count ->
            if (binding.checkboxEditName.isChecked) {
                if (text.toString().isNotEmpty()) {
                    mViewModel.statusMitraName.value = true
                    mViewModel.enterdMitraname.value = text.toString()
                } else {
                    mViewModel.statusMitraName.value = false
                    mViewModel.enterdMitraname.value = ""
                }
            } else {
                mViewModel.statusMitraName.value = true
            }

        }

        /* binding.edtMitraName.setOnClickListener {
             binding.scrollView.smoothScrollBy(0,150)
         }*/

        binding.layoutStockItemHeader.imgInfoCekStokTerakhir.setOnClickListener {
            findNavController().navigate(
                R.id.photoViewFragment
            )
        }
        binding.imgOffer.setOnClickListener {
            val report = (mViewModel.uiState.value as UiState.Success).report!!

            OfferDialog
                .getInstance(
                    report.onPlatformSalesAmount ?: "0.0",
                    report.offPlatformSalesAmount ?: "0.0"
                )
                .show(childFragmentManager, "")
        }

        binding.layoutStockItemHeader.imgInfoTopUp.setOnClickListener {
            findNavController().navigate(
                R.id.photoViewFragment
            )
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                launch {
                    mViewModel.mirtaSignature.collectLatest {
                        binding.mirtaSign.setImageURI(it?.toUri())
                    }
                }

                launch {
                    mViewModel.aeSignature.collectLatest {
                        binding.aeSign.setImageURI(it?.toUri())
                    }
                }

            }
        }
    }

    private fun askForConfirmation() {
        ConfirmationDialog()
            .setSubmitReportListener {
                createReportFile()
                mViewModel.submitReport {
                    collectOpt()
                }
            }
            .show(childFragmentManager, "CONFIRM")
    }

    private fun navigateToHome() {
        findNavController().navigate(AddCheckStockFragmentDirections.actionAddCheckStockFragmentToHomeFragment())
    }

    private fun createReportFile() {
        val bitmap = getBitmapFromView(
            binding.scrollView,
            binding.scrollView.getChildAt(0).height,
            binding.scrollView.getChildAt(0).width
        )
        mViewModel.setReportFile(createFile(bitmap!!, "${System.currentTimeMillis()}.jpg"))
    }

    private fun collectOpt() {
        val report = (mViewModel.uiState.value as UiState.Success).report!!
        OtpDialog
            .getInstance(report.id)
            .setOtpSuccessListener {
                navigateToHome()
            }
            .setCancelListener {
                navigateToHome()
            }
            .show(childFragmentManager, "OTP")
    }


    private fun collectSignature(isMitra: Boolean) {
        setFragmentResultListener(SignatureFragment.REQUEST_KEY_SIGNATURE) { _, bundle ->
            if (bundle.containsKey(SignatureFragment.SIGNATURE_BITMAP)) {
                bundle.getParcelable<Bitmap>(SignatureFragment.SIGNATURE_BITMAP)
                    ?.let { signature: Bitmap ->
                        if (isMitra) {
                            mViewModel.setMitraSignature(
                                createFile(
                                    signature,
                                    "${System.currentTimeMillis()}.jpg"
                                )
                            )
                        } else {
                            mViewModel.setAeSignature(
                                createFile(
                                    signature,
                                    "${System.currentTimeMillis()}.jpg"
                                )
                            )
                        }
                        binding.scrollView.smoothScrollTo(
                            0,
                            binding.scrollView.getChildAt(0).height
                        )
                    }
            }
        }
        findNavController().navigate(R.id.signatureFragment)
    }

    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }


    private fun createFile(bitmap: Bitmap, fileName: String): File {
        val file = File(context?.filesDir?.path + File.separator + fileName)

        if (file.exists())
            file.delete()

        file.createNewFile()

        file.write(
            bitmap,
            Bitmap.CompressFormat.JPEG,
            100
        )

        return file
    }

    private fun File.write(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 80
    ) = apply {
        createFileAndDirs()
        outputStream().use {
            bitmap.compress(format, quality, it)
            it.flush()
        }
    }

    private fun File.createFileAndDirs() = apply {
        parentFile?.mkdirs()
        createNewFile()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}