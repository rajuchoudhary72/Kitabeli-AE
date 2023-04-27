package com.kitabeli.ae.ui.resign_and_return.return_request

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.RefillRequestDto
import com.kitabeli.ae.data.remote.dto.ReturnProductDto
import com.kitabeli.ae.data.remote.dto.ReturnReasonDto
import com.kitabeli.ae.data.remote.dto.SkuDTO
import com.kitabeli.ae.databinding.DialogAddReturnProductBinding
import com.kitabeli.ae.ui.common.BaseDialogFragment
import com.kitabeli.ae.ui.resign_and_return.ResignAndReturnViewModel
import com.kitabeli.ae.utils.showGone
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddReturnProductDialog : BaseDialogFragment<ResignAndReturnViewModel>(R.layout.dialog_add_return_product) {
    private var _binding: DialogAddReturnProductBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: ResignAndReturnViewModel by viewModels()

    private var kioskCode = ""
    private var refillRequestId = 0L
    private var currentQty = 0

    private val datePicker by lazy {
        MaterialDatePicker.Builder.datePicker()
            .setNegativeButtonText(getString(R.string.batalkan))
            .setPositiveButtonText(getString(R.string.pilih))
            .setSelection(selectedDate?.time ?: Date().time)
            .setCalendarConstraints(getCalendarConstraints()).build()
    }
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    private fun getCalendarConstraints(): CalendarConstraints {
        val date3MonthsAhead = Calendar.getInstance().also { it.add(Calendar.MONTH, 3) }
        return CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.before(date3MonthsAhead.timeInMillis))
            .build()
    }

    private var selectedDate: Date? = null
    fun setSelectedDate(date: Date?): AddReturnProductDialog {
        val fixedDate = Calendar.getInstance().apply {
            time = date ?: Date()
            add(Calendar.HOUR, 7)
        }
        selectedDate = Date().apply { time = fixedDate.timeInMillis }
        return this
    }

    private var selectedProduct: ReturnProductDto? = null
    fun setSelectedProduct(product: ReturnProductDto?): AddReturnProductDialog {
        selectedProduct = product
        return this
    }

    private var selectedProductIds: List<Long> = emptyList()
    fun setSelectedProductsId(list: List<Long>): AddReturnProductDialog {
        selectedProductIds = list
        return this
    }

    private var onProductAdded: (() -> Unit)? = null
    fun setOnProductAddedListener(onAdded: (() -> Unit)): AddReturnProductDialog {
        onProductAdded = onAdded
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kioskCode = arguments?.getString(KIOSK_CODE).orEmpty()
        refillRequestId = arguments?.getLong(REFILL_REQ_ID) ?: 0L
        mViewModel.getSkuList(kioskCode)
        mViewModel.getReturnReasonList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddReturnProductBinding.inflate(layoutInflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeViewModel()
    }

    private fun initView() = with(binding) {
        textProductName.isEnabled = selectedProduct == null
        textProductName.isClickable = selectedProduct == null

        tvExpiredLabel.showGone(false)
        textExpiredDate.showGone(false)

        bindSelectedProduct()

        icClose.setOnClickListener {
            dismiss()
        }
        etProducts.setDropDownBackgroundDrawable(
            ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.bg_dialog
            )
        )
        etReasons.setDropDownBackgroundDrawable(
            ContextCompat.getDrawable(
                binding.root.context,
                R.drawable.bg_dialog
            )
        )
        etReasons.addTextChangedListener {
            val isExpiredReason = it?.toString()?.lowercase()?.contains(EXPIRED_LABEL) == true
            tvExpiredLabel.showGone(isExpiredReason)
            textExpiredDate.showGone(isExpiredReason)
        }
        etExpiredDate.setOnClickListener {
            datePicker.show(childFragmentManager, MaterialDatePicker::class.java.simpleName)
        }
        etExpiredDate.setDropDownBackgroundDrawable(null)
        datePicker.addOnPositiveButtonClickListener {
            mViewModel.selectedExpiryDate = Date(it)
            etExpiredDate.setText(dateFormatter.format(it))
        }

        etQty.addTextChangedListener {
            val value = it.toString()
            currentQty = when {
                value.isNotEmpty() -> value.toInt()
                else -> 0
            }
        }
        btnMinus.setOnClickListener {
            if (currentQty > 0) {
                currentQty--
                etQty.setText(currentQty.toString())
            }
        }
        btnPlus.setOnClickListener {
            currentQty++
            etQty.setText(currentQty.toString())
        }
        btnAdd.setOnClickListener {
            onButtonAddClick()
        }
    }

    private fun bindSelectedProduct() = with(binding) {
        selectedProduct?.let {
            val requestFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            etProducts.also { view ->
                view.setText(it.itemName)
                view.isClickable = false
                view.isFocusable = false
                view.isCursorVisible = false
            }
            textProductName.isClickable = false
            textProductName.isFocusable = false

            etReasons.setText(it.reasonLabel)
            it.requestQuantity?.let { qty ->
                currentQty = qty
                etQty.setText(qty.toString())
            }
            if (it.reasonLabel?.lowercase()?.contains(EXPIRED_LABEL) == true) {
                it.details?.let { expiredDate ->
                    val date = requestFormat.parse(expiredDate) ?: Date()
                    mViewModel.selectedExpiryDate = date
                    etExpiredDate.setText(dateFormatter.format(date))
                }
                tvExpiredLabel.showGone(true)
                textExpiredDate.showGone(true)
            }
        }
    }

    private fun onButtonAddClick() = with(binding) {
        val productList = mViewModel.productList.value
        val reasonList = mViewModel.reasonList.value
        val isExpiredReason = etReasons.text.toString().lowercase().contains(EXPIRED_LABEL)
        val isProductSelected = etProducts.text.toString().isNotEmpty()
        val isReasonSelected = etReasons.text.toString().isNotEmpty()
        val isExpiredDateSelected = when {
            isExpiredReason -> etExpiredDate.text.toString().isNotEmpty()
            else -> true
        }
        val isQtyInputted = etQty.text.toString().isNotEmpty()
        val isFormValid =
            isProductSelected && isReasonSelected && isExpiredDateSelected && isQtyInputted

        validateForm(
            isProductSelected = isProductSelected,
            isReasonSelected = isReasonSelected,
            isExpiredDateSelected = isExpiredDateSelected,
            isExpiredReason = isExpiredReason,
            isQtyInputted = isQtyInputted
        )

        if (isFormValid) {
            mViewModel.selectedProduct = productList?.firstOrNull {
                it.name == etProducts.text.toString()
            }
            mViewModel.stockQty = etQty.text.toString().toInt()
            mViewModel.selectedReason = reasonList?.firstOrNull {
                it.label == etReasons.text.toString()
            }
            if (selectedProduct != null) {
                mViewModel.updateReturnRequestProduct(kioskCode) {
                    dismiss()
                    onProductAdded?.invoke()
                }
            } else {
                mViewModel.addReturnRequestProduct(kioskCode) {
                    dismiss()
                    onProductAdded?.invoke()
                }
            }
            Log.e("SELECTED PRODUCT", mViewModel.selectedProduct?.name.orEmpty())
            Log.e("SELECTED QTY", mViewModel.stockQty.toString())
            Log.e("SELECTED REASON", mViewModel.selectedReason?.name.toString())
            Log.e("SELECTED DATE", mViewModel.selectedExpiryDate.toString())
        }
    }

    private fun validateForm(
        isProductSelected: Boolean,
        isReasonSelected: Boolean,
        isExpiredDateSelected: Boolean,
        isExpiredReason: Boolean,
        isQtyInputted: Boolean
    ) = with(binding) {
        when {
            isProductSelected -> textProductName.error = null
            else -> textProductName.error = getString(R.string.produk_harus_diisi)
        }

        when {
            isReasonSelected -> textReasonName.error = null
            else -> textReasonName.error = getString(R.string.alasan_harus_diisi)
        }

        when {
            isExpiredDateSelected && isExpiredReason -> textExpiredDate.error = null
            else -> textExpiredDate.error = getString(R.string.tanggal_harus_diisi)
        }

        when {
            isQtyInputted -> textProductQuantity.error = null
            else -> textProductQuantity.error = getString(R.string.jumlah_harus_diisi)
        }
    }

    private fun observeViewModel() = with(binding) {
        mViewModel.productList.observe(viewLifecycleOwner) { result: List<SkuDTO>? ->
            val unselectedProducts = result?.filter {
                it.skuId !in selectedProductIds.map { id -> id.toInt() }
            }
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_auto_complete_text,
                R.id.tv_item_name,
                unselectedProducts?.map { sku -> sku.name }.orEmpty()
            )
            if (selectedProduct == null) {
                etProducts.setAdapter(adapter)
            }
        }
        mViewModel.reasonList.observe(viewLifecycleOwner) { result: List<ReturnReasonDto>? ->
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_auto_complete_text,
                R.id.tv_item_name,
                result?.map { it.label }.orEmpty()
            )
            etReasons.setAdapter(adapter)
        }
        mViewModel.refillRequest.observe(viewLifecycleOwner) { result: RefillRequestDto? ->
            if (result != null) {
                mViewModel.addReturnRequestProduct(kioskCode) {
                    dismiss()
                    onProductAdded?.invoke()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KIOSK_CODE = "KIOSK_CODE"
        const val REFILL_REQ_ID = "REFILL_REQUEST_ID"
        const val EXPIRED_LABEL = "kedaluwarsa"

        fun getInstance(kioskCode: String, refillRequestId: Long): AddReturnProductDialog {
            return AddReturnProductDialog().apply {
                arguments = bundleOf(
                    KIOSK_CODE to kioskCode,
                    REFILL_REQ_ID to refillRequestId
                )
            }
        }
    }

    override fun getViewModel(): ResignAndReturnViewModel {
        return mViewModel;
    }
}