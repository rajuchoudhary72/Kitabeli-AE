package com.kitabeli.ae.ui.addcheckStock

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.data.remote.dto.KioskOrderItemDetailsDTO
import com.kitabeli.ae.databinding.ItemAddCheckStockOrderContainerBinding
import com.kitabeli.ae.utils.ext.toFormattedDate
import com.kitabeli.ae.utils.ext.toStandardRupiah
import javax.inject.Inject

class AddCheckStockOrderAdapter @Inject constructor() :
    ListAdapter<KioskOrderItemDetailsDTO, AddCheckStockOrderAdapter.ViewHolder>(DIFF_CALL_BACK) {

    class ViewHolder(val binding: ItemAddCheckStockOrderContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: KioskOrderItemDetailsDTO) {
            with(binding) {
                tvOrderId.text = item.orderId.toString()
                tvOrderDate.text = item.orderPlacedAt.toFormattedDate()
                includeOrderDetail.apply {
                    tvOrderStock.text = "${item.quantity} pcs"
                    tvOrderAmount.text = item.amount?.toStandardRupiah()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAddCheckStockOrderContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALL_BACK = object : DiffUtil.ItemCallback<KioskOrderItemDetailsDTO>() {
            override fun areItemsTheSame(
                oldItem: KioskOrderItemDetailsDTO,
                newItem: KioskOrderItemDetailsDTO
            ): Boolean {
                return oldItem.orderId == newItem.orderId
            }

            override fun areContentsTheSame(
                oldItem: KioskOrderItemDetailsDTO,
                newItem: KioskOrderItemDetailsDTO
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}