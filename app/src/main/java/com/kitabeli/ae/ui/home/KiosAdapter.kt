package com.kitabeli.ae.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.KiosItem
import com.kitabeli.ae.databinding.ItemKiosBinding
import javax.inject.Inject

class KiosAdapter @Inject constructor() :
    ListAdapter<KiosItem, KiosAdapter.KiosViewHolder>(DIFF_CALL_BACK) {

    var onClickItem: ((KiosItem) -> Unit)? = null

    class KiosViewHolder(val binding: ItemKiosBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: KiosItem, onClickItem: ((KiosItem) -> Unit)?) {
            binding.textName.text = item.kiosCode
            binding.textData.text = item.createdAt

            binding.root.setOnClickListener {
                onClickItem?.invoke(item)
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
                }

                "QA_REJECTED" -> {
                    binding.statusImage.setImageResource(R.drawable.ic_cancel)
                    binding.card.setStrokeColor(
                        ContextCompat.getColorStateList(
                            binding.card.context,
                            R.color.red
                        )
                    )
                }

                else -> {
                    binding.statusImage.setImageResource(R.drawable.ic_watch)
                    binding.card.setStrokeColor(
                        ContextCompat.getColorStateList(
                            binding.card.context,
                            R.color.yellow
                        )
                    )
                }
            }
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
        holder.bind(getItem(position), onClickItem)
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