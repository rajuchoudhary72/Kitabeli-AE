package com.kitabeli.ae.ui.addcheckStock

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.KioskOrderItemDetailsDTO
import com.kitabeli.ae.databinding.DialogAddCheckStockItemDetailBinding
import com.kitabeli.ae.utils.ext.toStandardRupiah
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddCheckStockItemDetailDialog : DialogFragment(R.layout.dialog_add_check_stock_item_detail) {
    private var _binding: DialogAddCheckStockItemDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var orderAdapter: AddCheckStockOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddCheckStockItemDetailBinding.inflate(layoutInflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orderList = arguments?.get(ORDER_LIST) as List<KioskOrderItemDetailsDTO?>
        val onPlatformAmount = arguments?.getLong(ON_PLATFORM_AMOUNT) ?: 0
        val offPlatformQty = arguments?.getInt(OFF_PLATFORM_QTY).toString()
        val offPlatformAmount = arguments?.getLong(OFF_PLATFORM_AMOUNT) ?: 0

        binding.apply {
            tvItemName.text = arguments?.getString(ITEM_NAME)
            includeOnPlatformHeader.also {
                it.tvHeaderAmount.text = onPlatformAmount.toStandardRupiah()
                it.root.setOnClickListener { _ ->
                    if (onPlatformAmount > 0) {
                        val isVisible = recyclerView.isVisible
                        recyclerView.visibility = if (isVisible) GONE else VISIBLE
                        includeOffPlatformItem.root.visibility = GONE
                        setTextViewDrawable(it.tvHeaderAmount, recyclerView.isVisible)
                        setTextViewDrawable(
                            includeOffPlatformHeader.tvHeaderAmount,
                            includeOffPlatformItem.root.isVisible
                        )
                    }
                }
            }
            includeOffPlatformHeader.also {
                it.tvHeaderText.text = getString(R.string.off_platform_text)
                it.tvHeaderAmount.text = offPlatformAmount.toStandardRupiah()
                it.root.setOnClickListener { _ ->
                    val isVisible = includeOffPlatformItem.root.isVisible
                    if (offPlatformAmount > 0) {
                        includeOffPlatformItem.root.visibility = if (isVisible) GONE else VISIBLE
                        recyclerView.visibility = GONE
                        setTextViewDrawable(
                            it.tvHeaderAmount,
                            includeOffPlatformItem.root.isVisible
                        )
                        setTextViewDrawable(
                            includeOnPlatformHeader.tvHeaderAmount,
                            recyclerView.isVisible
                        )
                    }
                }
            }
            includeOffPlatformItem.also {
                it.tvOrderStock.text = "$offPlatformQty pcs"
                it.tvOrderAmount.text = offPlatformAmount.toStandardRupiah()
            }
            recyclerView.adapter = orderAdapter.apply { submitList(orderList) }
            btnOk.setOnClickListener { dialog?.dismiss() }
        }
    }

    private fun setTextViewDrawable(view: TextView, isVisible: Boolean) {
        val arrowUp = ContextCompat.getDrawable(view.context, R.drawable.ic_arrow_up)
        val arrowDown = ContextCompat.getDrawable(view.context, R.drawable.ic_arrow_down)
        view.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            if (isVisible) arrowUp else arrowDown,
            null
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val ITEM_NAME = "item_name"
        const val ORDER_LIST = "order_list"
        const val OFF_PLATFORM_QTY = "off_platform_qty"
        const val OFF_PLATFORM_AMOUNT = "off_platform_amount"
        const val ON_PLATFORM_AMOUNT = "on_platform_amount"

        fun getInstance(
            itemName: String,
            orderList: List<KioskOrderItemDetailsDTO?>,
            offPlatformQty: Int,
            offPlatformAmount: Long,
            onPlatformAmount: Long
        ): AddCheckStockItemDetailDialog {
            return AddCheckStockItemDetailDialog().apply {
                arguments = bundleOf(
                    ITEM_NAME to itemName,
                    ORDER_LIST to orderList,
                    OFF_PLATFORM_QTY to offPlatformQty,
                    OFF_PLATFORM_AMOUNT to offPlatformAmount,
                    ON_PLATFORM_AMOUNT to onPlatformAmount,
                )
            }
        }
    }
}