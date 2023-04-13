package com.kitabeli.ae.ui.resign_and_return.kiosk_resign

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.data.remote.dto.ResignOption
import com.kitabeli.ae.databinding.ItemResignFormChildBinding
import com.kitabeli.ae.utils.showGone


class KioskResignFormChildAdapter(
    private val onItemClick: (item: String, pos: Int) -> Unit,
    private var checkedPosition: Int = -1,
    private val onNoteChangeListener: (label: String?, value: String) -> Unit,
) : ListAdapter<ResignOption, KioskResignFormChildAdapter.ViewHolder>(Util) {

    private fun getTextWatcher(label: String?): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(value: Editable) {
                onNoteChangeListener.invoke(label, value.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        }
    }

    inner class ViewHolder(private val binding: ItemResignFormChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResignOption) {
            val lastItemPosition = itemCount - 1
            val isLastItem = absoluteAdapterPosition == lastItemPosition
            val isChecked = checkedPosition == absoluteAdapterPosition
            val isLastItemChecked = checkedPosition == lastItemPosition
            val isOtherOption = data.showTextBox == true
            val showNote = isLastItem && isLastItemChecked && isOtherOption
            with(binding) {
                tvReasonName.text = data.label
                rbSelected.isChecked = isChecked
                etNote.also {
                    it.showGone(showNote)
                    if (showNote) {
                        it.addTextChangedListener(getTextWatcher(data.label))
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
                    etNote.text = null
                    if (data.showTextBox == true) {
                        etNote.addTextChangedListener(getTextWatcher(data.label))
                        onNoteChangeListener.invoke(data.label, "")
                    } else {
                        etNote.removeTextChangedListener(getTextWatcher(data.label))
                        onItemClick.invoke(data.label.orEmpty(), absoluteAdapterPosition)
                    }
                    if (checkedPosition != absoluteAdapterPosition) {
                        hideKeyboard(this)
                        notifyItemChanged(checkedPosition)
                        notifyItemChanged(lastItemPosition)
                        checkedPosition = absoluteAdapterPosition
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemResignFormChildBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    object Util : DiffUtil.ItemCallback<ResignOption>() {
        override fun areItemsTheSame(oldItem: ResignOption, newItem: ResignOption): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ResignOption,
            newItem: ResignOption
        ): Boolean =
            oldItem.label == newItem.label
    }

    private fun hideKeyboard(binding: ItemResignFormChildBinding) {
        val imm = binding.root.context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
