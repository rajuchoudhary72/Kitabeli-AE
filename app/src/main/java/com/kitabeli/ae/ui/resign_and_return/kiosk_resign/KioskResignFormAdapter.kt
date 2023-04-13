package com.kitabeli.ae.ui.resign_and_return.kiosk_resign

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kitabeli.ae.data.remote.dto.ResignOption
import com.kitabeli.ae.data.remote.dto.ResignQuestion
import com.kitabeli.ae.databinding.ItemResignFormBinding
import com.kitabeli.ae.utils.showGone

class KioskResignFormAdapter(
    private val onItemClick: (item: Map<String, List<ResignOption>>, size: Int) -> Unit,
) : ListAdapter<ResignQuestion, KioskResignFormAdapter.ViewHolder>(DIFF_CALL_BACK) {

    var responseMap = mutableMapOf<String, List<ResignOption>>()

    class ViewHolder(val binding: ItemResignFormBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: ResignQuestion,
            onResponseClick: (question: String?, option: ResignOption) -> Unit
        ) {
            with(binding) {
                tvQuestionName.text = data.question
                val responseAdapter = KioskResignFormChildAdapter(
                    onItemClick = { selectedOption, _ ->
                        onResponseClick.invoke(
                            data.questionCode, ResignOption(
                                label = selectedOption,
                                showTextBox = false
                            )
                        )
                    },
                    onNoteChangeListener = { selectedLabel, value ->
                        if (value.length > MIN_NOTES_LENGTH) {
                            onResponseClick.invoke(
                                data.questionCode, ResignOption(
                                    label = selectedLabel,
                                    showTextBox = true,
                                    value = value
                                )
                            )
                        } else {
                            onResponseClick.invoke(
                                data.questionCode, ResignOption(
                                    label = selectedLabel,
                                    showTextBox = true
                                )
                            )
                        }
                    }
                )

                if (data.options.size > 2) {
                    rvAnswer.showGone(true)
                    llAnswer.showGone(false)

                    responseAdapter.submitList(data.options)
                    rvAnswer.adapter = responseAdapter
                } else {
                    rvAnswer.showGone(false)
                    llAnswer.showGone(true)
                    rbYes.text = data.options[0].label
                    rbNo.text = data.options[1].label

                    rbYes.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            rbNo.isChecked = false
                            onResponseClick.invoke(
                                data.questionCode,
                                ResignOption(
                                    label = data.options[0].label,
                                    showTextBox = false
                                )
                            )
                        }
                    }

                    rbNo.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            rbYes.isChecked = false
                            onResponseClick.invoke(
                                data.questionCode,
                                ResignOption(
                                    label = data.options[1].label,
                                    showTextBox = false
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemResignFormBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            data = getItem(position),
            onResponseClick = { code, response ->
                responseMap[code.orEmpty()] = listOf(response)
                if (response.showTextBox == true && response.value == null) {
                    responseMap.remove(code)
                }
                onItemClick.invoke(responseMap, itemCount)
            }
        )
    }

    companion object {
        const val MIN_NOTES_LENGTH = 9
        val DIFF_CALL_BACK = object : DiffUtil.ItemCallback<ResignQuestion>() {
            override fun areItemsTheSame(
                oldItem: ResignQuestion,
                newItem: ResignQuestion
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ResignQuestion,
                newItem: ResignQuestion
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}