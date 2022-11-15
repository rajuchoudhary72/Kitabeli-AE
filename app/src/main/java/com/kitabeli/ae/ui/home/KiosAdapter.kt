package com.kitabeli.ae.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.databinding.ItemKiosBinding
import javax.inject.Inject

class KiosAdapter @Inject constructor() :
    ListAdapter<Int, KiosAdapter.KiosViewHolder>(DIFF_CALL_BACK) {


    class KiosViewHolder(binding: ItemKiosBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {

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
        holder.bind()
    }

    companion object {
        val DIFF_CALL_BACK = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }

        }
    }
}