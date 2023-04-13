package com.kitabeli.ae.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.KiosItem
import com.kitabeli.ae.data.remote.dto.KiosStatusListDTO
import com.kitabeli.ae.databinding.ItemKiosBinding
import com.kitabeli.ae.utils.showGone
import javax.inject.Inject

class KiosAdapter @Inject constructor() :
    ListAdapter<KiosItem, KiosAdapter.KiosViewHolder>(DIFF_CALL_BACK) {

    var onClickItem: ((KiosItem) -> Unit)? = null
    var onShowDetailClick: ((String) -> Unit)? = null

    class KiosViewHolder(val binding: ItemKiosBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            item: KiosItem,
            onClickItem: ((KiosItem) -> Unit)?,
            onShowDetailClick: ((String) -> Unit)?
        ) {
            binding.textName.text = item.kiosCode
            binding.textData.text = item.createdAt

            binding.root.setOnClickListener {
                onClickItem?.invoke(item)
            }

            binding.tvPpInfo.showGone(false)
            binding.tvPpInfoTwo.showGone(false)
            binding.clPartialPayment.showGone(false)


            item.kiosStatusListDTO?.mapIndexed { index, kiosStatusListDTO ->
                when (index) {
                    0 -> {
                        updateChipsUI(binding.tvPpInfo, kiosStatusListDTO)
                    }
                    1 -> {
                        updateChipsUI(binding.tvPpInfoTwo, kiosStatusListDTO)
                    }

                }
            }

            when (item.status) {
                "COMPLETED" -> {
                    binding.statusImage.setImageResource(R.drawable.ic_done)
                    binding.card.setStrokeColor(
                        ContextCompat.getColorStateList(
                            binding.card.context,
                            R.color.green
                        )
                    )

                    binding.card.isClickable = item.isReturnStockTransferCompleted == false

                    if (item.isReturnStockTransferCompleted == false) {
                        binding.card.setOnClickListener {
                            onShowDetailClick?.invoke(item.stockTransferId.orEmpty())
                        }
                    }


                }

                "QA_REJECTED", "CANCELLED", "EXPIRED" -> {
                    binding.statusImage.setImageResource(R.drawable.ic_cancel)
                    binding.card.setStrokeColor(
                        ContextCompat.getColorStateList(
                            binding.card.context,
                            R.color.red
                        )
                    )
                    binding.card.isClickable = false
                }
                "PARTIAL_PAYMENT_PENDING" -> {
                    binding.statusImage.setImageResource(R.drawable.ic_partial_payment)
                    binding.card.setStrokeColor(
                        ContextCompat.getColorStateList(
                            binding.card.context,
                            R.color.grey_40
                        )
                    )
                    binding.card.isClickable = true
                    binding.clPartialPayment.showGone(true)
                    binding.tvPpInfo.showGone(true)
                    binding.price.text = "Rp.${item.pendingAmount}"


                    if (item.stockTransferId != null) {
                        binding.clShowPendingAmount.showGone(false)
                        binding.tvShowDetail.showGone(true)
                        binding.tvShowDetail.setOnClickListener {
                            onShowDetailClick?.invoke(item.stockTransferId)
                        }
                    } else {
                        binding.clShowPendingAmount.showGone(true)
                        binding.tvShowDetail.showGone(false)
                    }


                }

                else -> {
                    binding.statusImage.setImageResource(R.drawable.ic_watch)
                    binding.card.setStrokeColor(
                        ContextCompat.getColorStateList(
                            binding.card.context,
                            R.color.yellow
                        )
                    )
                    binding.card.isClickable = true
                }
            }
        }

        private fun updateChipsUI(textView: TextView, kiosStatusListDTO: KiosStatusListDTO) {
            when (kiosStatusListDTO.messageType) {
                "INFO" -> {
                    textView.backgroundTintList =
                        ContextCompat.getColorStateList(binding.root.context, R.color.home_blue_bg)
                    textView.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.home_blue_text
                        )
                    )
                }
                "SUCCESS" -> {
                    textView.backgroundTintList =
                        ContextCompat.getColorStateList(binding.root.context, R.color.home_green_bg)
                    textView.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.home_green_text
                        )
                    )
                }
                "ERROR" -> {
                    textView.backgroundTintList =
                        ContextCompat.getColorStateList(binding.root.context, R.color.home_red_bg)
                    textView.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.home_red_text
                        )
                    )
                }

            }
            textView.text = kiosStatusListDTO.messageInfo
            textView.showGone(true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KiosViewHolder {
        return KiosViewHolder(
            ItemKiosBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: KiosViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            onClickItem,
            onShowDetailClick
        )
    }

    companion object {
        val DIFF_CALL_BACK = object : DiffUtil.ItemCallback<KiosItem>() {
            override fun areItemsTheSame(oldItem: KiosItem, newItem: KiosItem): Boolean {
                return oldItem.stockOpnameId == newItem.stockOpnameId
            }

            override fun areContentsTheSame(oldItem: KiosItem, newItem: KiosItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}