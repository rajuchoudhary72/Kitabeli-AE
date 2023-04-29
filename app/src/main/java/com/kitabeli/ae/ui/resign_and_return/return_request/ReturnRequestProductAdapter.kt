package com.kitabeli.ae.ui.resign_and_return.return_request

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.data.remote.dto.ReturnProductDto
import com.kitabeli.ae.databinding.ItemReturnProductBinding
import com.kitabeli.ae.utils.showGone
import java.text.SimpleDateFormat
import java.util.*

class ReturnRequestProductAdapter(
    private val onDeleteButtonClick: (item: ReturnProductDto) -> Unit,
    private val onEditButtonClick: (item: ReturnProductDto) -> Unit,
    private val onAddButtonClick: () -> Unit,
) : ListAdapter<ReturnProductDto, ReturnRequestProductAdapter.ViewHolder>(Util) {

    inner class ViewHolder(private val binding: ItemReturnProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: ReturnProductDto) = with(binding) {
            val showPlaceholder = data == ReturnProductDto()
            val requestFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            cvMain.showGone(!showPlaceholder)
            llAddItem.showGone(showPlaceholder)
            tvProductName.text = data.itemName
            tvProductQty.text = "x ${data.requestQuantity}"
            tvReason.text = data.reasonLabel
            if (data.reason == EXPIRED_ITEM && data.details != null) {
                val date = requestFormat.parse(data.details) ?: Date()
                tvExpiredDate.text = dateFormatter.format(date)
                tvExpiredDate.showGone(true)
                tvExpiredLabel.showGone(true)
                divider2.showGone(true)
            } else {
                tvExpiredDate.showGone(false)
                tvExpiredLabel.showGone(false)
                divider2.showGone(false)
            }
            btnEdit.setOnClickListener {
                onEditButtonClick.invoke(data)
            }
            btnDelete.setOnClickListener {
                onDeleteButtonClick.invoke(data)
            }
            llAddItem.setOnClickListener {
                onAddButtonClick.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemReturnProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    object Util : DiffUtil.ItemCallback<ReturnProductDto>() {
        override fun areItemsTheSame(
            oldItem: ReturnProductDto,
            newItem: ReturnProductDto
        ): Boolean =
            oldItem.itemId == newItem.itemId

        override fun areContentsTheSame(
            oldItem: ReturnProductDto, newItem: ReturnProductDto
        ): Boolean = oldItem == newItem
    }

    companion object {
        const val EXPIRED_ITEM = "GOODS_WILL_EXPIRE"
    }

}
