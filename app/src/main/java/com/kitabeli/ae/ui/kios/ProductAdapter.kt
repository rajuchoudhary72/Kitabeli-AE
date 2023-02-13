package com.kitabeli.ae.ui.kios

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.StockOpNameItemDTOS
import com.kitabeli.ae.databinding.ItemProductBinding
import javax.inject.Inject

class ProductAdapter @Inject constructor() :
    ListAdapter<StockOpNameItemDTOS, ProductAdapter.ProductViewHolder>(DIFF_CALL_BACK) {

    var status: () -> String = { "INITIALIZED" }

    var onClickEdit: ((StockOpNameItemDTOS) -> Unit)? = null

    class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /*Belum diajukan      == INITIALIZED
          Menunggu verifikasi == QA_ASSIGNMENT_PENDING
          Terverifikasi   ===QA_VERIFIED
          Ditolak ===QA_REJECTED*/
        fun bind(
            stockItem: StockOpNameItemDTOS,
            onClickEdit: ((StockOpNameItemDTOS) -> Unit)?,
            status: () -> String
        ) {
            binding.item = stockItem
            binding.btnEdit.setOnClickListener {
                onClickEdit?.invoke(stockItem)
            }
            when (stockItem.status) {
                "INITIALIZED" -> {
                    binding.textProductStatus.setTextColor(
                        ContextCompat.getColor(
                            binding.textProductStatus.context,
                            R.color.grey
                        )
                    )
                    binding.textProductStatus.text = "Belum diajukan"
                }

                "QA_ASSIGNED" -> {
                    binding.textProductStatus.setTextColor(
                        ContextCompat.getColor(
                            binding.textProductStatus.context,
                            R.color.blue
                        )
                    )
                    binding.textProductStatus.text = "Menunggu verifikasi"
                }

                "QA_VERIFIED" -> {
                    binding.textProductStatus.setTextColor(
                        ContextCompat.getColor(
                            binding.textProductStatus.context,
                            R.color.green
                        )
                    )
                    binding.textProductStatus.text = "Terverifikasi"
                }

                else -> {
                    binding.textProductStatus.setTextColor(
                        ContextCompat.getColor(
                            binding.textProductStatus.context,
                            R.color.red
                        )
                    )
                    binding.textProductStatus.text = "Ditolak"
                }
            }

            binding.btnEdit.isVisible = status() == "QA_ASSIGNED" || status() == "INITIALIZED"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onClickEdit, status)
    }

    companion object {
        val DIFF_CALL_BACK = object : DiffUtil.ItemCallback<StockOpNameItemDTOS>() {
            override fun areItemsTheSame(
                oldItem: StockOpNameItemDTOS,
                newItem: StockOpNameItemDTOS
            ): Boolean {
                return oldItem.skuId == newItem.skuId
            }

            override fun areContentsTheSame(
                oldItem: StockOpNameItemDTOS,
                newItem: StockOpNameItemDTOS
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}