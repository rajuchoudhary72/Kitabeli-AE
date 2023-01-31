package com.kitabeli.ae.ui.cashCollection

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.data.remote.dto.CancelReasonDto
import com.kitabeli.ae.databinding.ItemCancelCashCollectionBinding

class CashCollectionSheetAdapter(
    private val onItemClick: (item: String, pos: Int) -> Unit,
    private var checkedPosition: Int = -1,
    private val onNoteChangeListener: (String) -> Unit,
) : ListAdapter<CancelReasonDto, CashCollectionSheetAdapter.ViewHolder>(Util) {

    var note = ""

    inner class ViewHolder(private val binding: ItemCancelCashCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CancelReasonDto) {
            val lastItemPosition = itemCount - 1
            val isLastItem = adapterPosition == lastItemPosition
            val isChecked = checkedPosition == adapterPosition
            val isLastItemChecked = checkedPosition == lastItemPosition
            with(binding) {
                tvReasonName.text = data.label
                rbSelected.isChecked = isChecked
                etNote.also {
                    it.visibility = if (isLastItem && isLastItemChecked) VISIBLE else GONE
                    it.setText(note)
                    it.addTextChangedListener { value ->
                        if (value != null) {
                            note = value.toString()
                            onNoteChangeListener.invoke(value.toString())
                        }
                    }
                    it.setOnEditorActionListener { _, actionId, _ ->
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            hideKeyboard(this)
                        }
                        true
                    }
                }

                root.setOnClickListener {
                    rbSelected.isChecked = true
                    onItemClick.invoke(data.name.orEmpty(), adapterPosition)
                    if (checkedPosition != adapterPosition) {
                        hideKeyboard(this)
                        notifyItemChanged(checkedPosition)
                        notifyItemChanged(lastItemPosition)
                        checkedPosition = adapterPosition
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemCancelCashCollectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    object Util : DiffUtil.ItemCallback<CancelReasonDto>() {
        override fun areItemsTheSame(oldItem: CancelReasonDto, newItem: CancelReasonDto): Boolean =
            oldItem.label == newItem.label

        override fun areContentsTheSame(
            oldItem: CancelReasonDto,
            newItem: CancelReasonDto
        ): Boolean =
            oldItem == newItem
    }

    private fun hideKeyboard(binding: ItemCancelCashCollectionBinding) {
        val imm = binding.root.context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
