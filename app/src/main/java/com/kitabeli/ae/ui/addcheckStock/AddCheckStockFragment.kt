package com.kitabeli.ae.ui.addcheckStock

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.PaymentDetailDto
import com.kitabeli.ae.databinding.FragmentAddCheckStockBinding
import com.kitabeli.ae.ui.cashCollection.CancelCashCollectionBottomSheet
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.ui.login.midtrans.Midtrans
import com.kitabeli.ae.ui.signature.SignatureFragment
import com.kitabeli.ae.utils.ext.openWhatsAppSupport
import com.kitabeli.ae.utils.ext.toFormattedDate
import com.kitabeli.ae.utils.showGone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AddCheckStockFragment : BaseFragment<AddCheckStockViewModel>() {

    private var _binding: FragmentAddCheckStockBinding? = null
    private val mViewModel: AddCheckStockViewModel by viewModels()
    private val binding get() = _binding!!
    private var totalPrice: String? = null


    private val args: AddCheckStockFragmentArgs by navArgs()

    @Inject
    lateinit var stockItemAdapter: StockItemAdapter

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.e("Permission", "${it.key} = ${it.value}")
                if (it.key == "android.permission.ACCESS_FINE_LOCATION"
                ) {
                    getLatLong()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askLocationPermission()
    }

    private fun askLocationPermission() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

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

    override fun onSaveInstanceState(outState: Bundle) {}

    override fun getViewModel(): AddCheckStockViewModel {
        return mViewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getLatLong()
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel
                    .isKioskOwner
                    .collectLatest { isKioskOwner ->
                        val agreementText = when {
                            isKioskOwner -> getString(R.string.ko_agreement_text)
                            else -> getString(R.string.ae_agreement_text)
                        }
                        mViewModel.isKioskOwnerUser.value = isKioskOwner
                        mViewModel.agreementText.value = agreementText
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel
                .uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { uiState ->
                    if (uiState is UiState.Success) {
                        if (args.isFromPartialPayment) {
                            binding.recyclerView.showGone(false)
                            binding.clPpPaymentView.showGone(true)
                            binding.layoutStockItemHeader.root.showGone(false)
                            binding.title.showGone(false)
                            binding.message.showGone(false)
                            binding.clPartialPaymentInfo.showGone(true)
                            binding.ppTotalAmt.text = "Rp. ${uiState.report?.totalAmountToBePaid}"
                            binding.ppPaidAmt.text = "Rp. ${uiState.report?.totalPartialPaidAmount}"
                            binding.price.text = "Rp. ${uiState.report?.totalPartialPendingAmount}"
                            totalPrice = uiState.report?.totalPartialPendingAmount

                        } else {
                            binding.recyclerView.showGone(true)
                            stockItemAdapter.submitList(uiState.report?.stockOPNameReportItemDTOs)
                            totalPrice = uiState.report?.totalAmountToBePaid
                            binding.price.text = "Rp $totalPrice"
                        }



                        if (uiState.report?.status == "OTP_GENERATED" && uiState.report.isKioskShutdown == false) {
                            collectOTP()
                        }

                        if (uiState.report?.status == "PAYMENT_COMPLETED" || uiState.report?.status == "CANCELLED") {
                            navigateToHome()
                        }

                        if (uiState.report?.status == "ONLINE_PAYMENT_INITIATED") {
                            mViewModel.showResumePaymentBtn.value = true
                        }

                        if (uiState.report == null && uiState.shouldShowBankAlert == true) {
                            BankAccountAlertDialog()
                                .setOnButtonClickListener {
                                    findNavController().popBackStack()
                                }.show(
                                    childFragmentManager,
                                    BankAccountAlertDialog::class.java.simpleName
                                )
                        }
                    }
                }
        }

        mViewModel.showPaymentDialog.observe(viewLifecycleOwner) { result ->
            if (result) {
                showPaymentAlertDialog()
                mViewModel.showPaymentDialog.value = false
            }
        }

        binding.btnCancel.setOnClickListener {
            CancelCashCollectionBottomSheet.getInstance(
                onButtonClick = { reason, note ->
                    showConfirmCancelDialog(reason, note)
                }
            ).show(childFragmentManager, CancelCashCollectionBottomSheet::class.java.simpleName)
        }

        binding.btn.setOnClickListener {
            if (binding.clPpPaymentView.visibility == View.VISIBLE && args.isFromPartialPayment.not()) {
                askForConfirmation()
            } else {
                checkForKiosShutdown()
            }

        }

        binding.btnPay.setOnClickListener {
            createReportFile()
            mViewModel.submitReport(openOTP = {
                goToMidtransPaymentScreen(it)
            })
        }

        binding.btnResumePayment.setOnClickListener {
            mViewModel.getPaymentDetails { paymentDetail ->
                val report = (mViewModel.uiState.value as UiState.Success).report
                if (paymentDetail != null && report != null) {
                    val date = paymentDetail.paymentExpireTime.toFormattedDate("dd MMM yyyy")
                    if (paymentDetail.bankName != null && paymentDetail.virtualAccountNumber != null) {
                        findNavController().navigate(
                            R.id.fragment_payment_detail,
                            bundleOf(
                                "STOCKOP_ID" to paymentDetail.orderId,
                                "STOCKOP_DATE" to date,
                                "AMOUNT" to paymentDetail.orderAmount,
                                "INCENTIVE" to report.incentiveAmount,
                                "BANK_NAME" to paymentDetail.bankName,
                                "VA_NUMBER" to paymentDetail.virtualAccountNumber,
                                "EXPIRED_AT" to paymentDetail.paymentExpireTime,
                                "BILL_CODE" to paymentDetail.billerCode,
                            )
                        )
                    } else {
                        goToMidtransPaymentScreen(paymentDetail.token)
                    }
                } else {
                    showToast("Detail Pembayaran tidak ditemukan")
                }
            }
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.signMitra.setOnClickListener {
            collectSignature(true)
        }

        binding.signAe.setOnClickListener {
            collectSignature(false)
        }

        binding.checkboxEditName.setOnCheckedChangeListener { _, b ->
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
                    mViewModel.enteredMitraName.value = text.toString()
                } else {
                    mViewModel.statusMitraName.value = false
                    mViewModel.enteredMitraName.value = ""
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
                    report.offPlatformSalesAmount ?: "0.0",
                    report.incentiveAmount ?: "0.0"
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

        binding.btnHelp.setOnClickListener {
            activity?.openWhatsAppSupport()
        }
    }


    private fun getLatLong() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //askLocationPermission()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                Log.e("lat", location.latitude.toString())
                Log.e("long", location.longitude.toString())
                mViewModel.latitude.value = location.latitude
                mViewModel.longitude.value = location.longitude


            }
        }
    }

    private fun showConfirmCancelDialog(reason: String, note: String) {
        ConfirmationWithImageDialog().also {
            it.setContent(
                title = "Yakin Ingin Membatalkan Cek Stok?",
                subTitle = "Semua data yang sudah diunggah tidak akan tersimpan.",
                cancelButtonText = "Kembali",
                confirmButtonText = "Iya, Batalkan",
                showCancelButton = true
            )
            it.setOnButtonClickListener {
                mViewModel.cancelReport(cancelReason = reason, note = note) {
                    navigateToHome()
                }
            }.show(
                childFragmentManager,
                ConfirmationWithImageDialog::class.java.simpleName
            )
        }
    }

    private fun askForConfirmation() {
        ConfirmationDialog()
            .setContent(
                "Apakah kamu sudah menerima uang hasil penjualan?",
                message = "Wajib setor uang penjualan bila proses input selesai. Atau mengulang input data bila belum.",
                confirmButtonText = "Sudah, Minta OTP",
                cancelButtonText = "Belum"
            )
            .setConfirmListener {
                createReportFile()
                mViewModel.partialAmountConfirmedByAE.value = true
                mViewModel.submitReport(openOTP = {
                    collectOTP()
                })
            }.show(childFragmentManager, ConfirmationDialog::class.java.simpleName)
    }

    private fun navigateToHome() {
        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel
                .isKioskOwner
                .collectLatest { isKioskOwner ->
                    if (isKioskOwner) {
                        findNavController().navigate(
                            AddCheckStockFragmentDirections.actionToStockOpOnBoardingFragment()
                        )
                    } else {
                        findNavController().navigate(
                            AddCheckStockFragmentDirections.actionAddCheckStockFragmentToHomeFragment()
                        )
                    }
                }
        }
    }

    private fun createReportFile() {
        val bitmap = getBitmapFromView(
            binding.scrollView,
            binding.scrollView.getChildAt(0).height,
            binding.scrollView.getChildAt(0).width
        )
        mViewModel.setReportFile(createFile(bitmap!!, "${System.currentTimeMillis()}.jpg"))
    }

    private fun collectOTP() {
        val report = (mViewModel.uiState.value as UiState.Success).report!!
        OtpDialog
            .getInstance(
                otpType = OtpDialog.OtpType.STOCK_OPNAME,
                stockOpReportId = report.id
            )
            .setOnOtpSuccessListener {
                if (it == null) {
                    navigateToHome()
                } else {
                    navigateToStockWithdrawal(it)
                }
            }
            .setOnCloseListener {
                navigateToHome()
            }
            .show(childFragmentManager, "OTP")
    }

    private fun navigateToStockWithdrawal(s: String) {
        findNavController().navigate(
            AddCheckStockFragmentDirections.actionToStockWithdrawal(stockTransferId = s)
        )
    }

    private fun goToMidtransPaymentScreen(snapToken: String?) {
        Midtrans.checkout(
            requireActivity(),
            snapToken,
            onResult = { result ->
                Log.d("MIDDTRANSSSSS", "{${result.status}}")
                Log.d("MIDDTRANSSSSS RESPONSE", "{${result.response?.transactionId}}")
                Log.d("MIDDTRANSSSSS RESPONSE", "{${result.response?.paymentType}}")
                Log.d("MIDDTRANSSSSS RESPONSE", "{${result.response?.grossAmount}}")
                Log.d("MIDDTRANSSSSS RESPONSE", "{${result.response?.bcaVaNumber}}")
                mViewModel.showPaymentDialog.value = true
            }
        )
    }

    private fun showPaymentAlertDialog() {
        ConfirmationWithImageDialog().also {
            it.setOnButtonClickListener {
                goToMitraApp()
            }.show(
                childFragmentManager,
                ConfirmationWithImageDialog::class.java.simpleName
            )
        }
    }

    private fun goToMitraApp() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mViewModel
                    .kioskCode
                    .collectLatest { kioskCode ->
                        try {
                            activity?.finish()
                            val intent = Intent("id.kitabeli.mitra.invoiceList")
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra("KIOSK_CODE", kioskCode)
                            startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=id.kitabeli.mitra")
                                    )
                                )
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=id.kitabeli.mitra")
                                    )
                                )
                            }
                        }

                    }
            }
        }
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


    /*Kios ShutDown*/
    private fun checkForKiosShutdown() {
        createReportFile()
        mViewModel.submitReport(openOTP = {
            askForConfirmation()
        }, openKiosShutDownDialog = {
            openKiosShutDownDialog()
        }, openPartialPaymentDialog = {
            openPartialPaymentDialog(it)
        })
    }

    private fun openPartialPaymentDialog(paymentDetailDto: PaymentDetailDto?) {
        ConfirmationDialog().setContent(
            title = "Kios Akan Melakukan Pembayaran Sebagian",
            message = "Pastikan uang yang diterima sesuai dengan nominal yang disebutkan",
            confirmButtonText = "Oke",
            cancelButtonText = "Kembali",
            partialPaymentAmount = paymentDetailDto?.orderAmount
        ).setConfirmListener {
            if (args.isFromPartialPayment) {
                askForConfirmation()
            } else {
                showPartialPaymentView(paymentDetailDto)
            }

        }.show(childFragmentManager, "KiosShutDown")
    }

    @SuppressLint("SetTextI18n")
    private fun showPartialPaymentView(paymentDetailDto: PaymentDetailDto?) = with(binding) {
        clPartialPaymentInfo.showGone(true)
        clPpPaymentView.showGone(true)
        binding.price.text = "Rp.${paymentDetailDto?.orderAmount}"
        binding.ppTotalAmt.text = "Rp. $totalPrice"
        binding.scrollView.smoothScrollTo(0, 0)
        binding.tvPpPaidAmtLabel.showGone(false)
        binding.ppPaidAmt.showGone(false)
        binding.v2.showGone(false)

    }

    private fun openKiosShutDownDialog() {
        ConfirmationDialog().setContent(
            title = "Tunggu Konfirmasi Pemilik Kios",
            message = "Pemilik kios akan memilih terlebih dahulu cara pembayarannya",
            confirmButtonText = "Refresh",
            null
        ).setConfirmListener {
            checkForKiosShutdown()
        }.show(childFragmentManager, "KiosShutDown")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}