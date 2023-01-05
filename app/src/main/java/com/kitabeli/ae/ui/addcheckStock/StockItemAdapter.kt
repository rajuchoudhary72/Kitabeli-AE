package com.kitabeli.ae.ui.addcheckStock

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.StockOPNameReportItemDTO
import com.kitabeli.ae.databinding.ItemStockProductBinding
import javax.inject.Inject

class StockItemAdapter @Inject constructor() :
    ListAdapter<StockOPNameReportItemDTO, StockItemAdapter.ProductViewHolder>(DIFF_CALL_BACK) {

    var onItemClickListener: ((StockOPNameReportItemDTO) -> Unit)? = null

    class ProductViewHolder(val binding: ItemStockProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: StockOPNameReportItemDTO,
            onItemClickListener: ((StockOPNameReportItemDTO) -> Unit)?
        ) {
            binding.namaProduk.text = if (item.itemName.isNullOrBlank()) {
                "NA"
            } else {
                item.itemName
            }
            binding.cekStokTerakhir.text = item.lastStockOpCount.toString()
            binding.topUp.text = item.stnItemCount.toString()
            binding.cekStokSaatIni.text = item.stockOpCount.toString()

            val qtyToBePaid = item.quantitiesToBePaid ?: 0
            binding.perluDibayar.apply {
                text = qtyToBePaid.toString()
                if (qtyToBePaid > 0) {
                    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    setTextColor(ContextCompat.getColor(context, R.color.orange50))
                    setOnClickListener {
                        onItemClickListener?.invoke(item)
                    }
                } else {
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemStockProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickListener)
    }

    companion object {
        val DIFF_CALL_BACK = object : DiffUtil.ItemCallback<StockOPNameReportItemDTO>() {
            override fun areItemsTheSame(
                oldItem: StockOPNameReportItemDTO,
                newItem: StockOPNameReportItemDTO
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: StockOPNameReportItemDTO,
                newItem: StockOPNameReportItemDTO
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}