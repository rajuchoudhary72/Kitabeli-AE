package com.kitabeli.ae.ui.cashCollection

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.BottomSheetCancelCashCollectionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelCashCollectionBottomSheet :
    BottomSheetDialogFragment(R.layout.bottom_sheet_cancel_cash_collection) {

    lateinit var binding: BottomSheetCancelCashCollectionBinding
    private val viewModel: CancelCashCollectionViewModel by viewModels()

    var onButtonClickListener: ((String, String) -> Unit)? = null
    var selectedReason: String = ""
    var selectedPosition: Int = -1
    var note: String = ""

    private val adapter by lazy {
        CashCollectionSheetAdapter(
            onItemClick = { item, pos ->
                selectedReason = item
                selectedPosition = pos
                if (selectedReason != "OTHERS") {
                    binding.btnConfirm.isEnabled = true
                }
            },
            checkedPosition = selectedPosition,
            onNoteChangeListener = {
                note = it
                if (selectedReason == "OTHERS") {
                    binding.btnConfirm.isEnabled = note.length > 9
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCancelCashCollectionBinding.inflate(inflater, container, false)
        viewModel.getCancelReasonList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
        bind()
        observeViewModel()
    }

    private fun initDialog() {
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun bind() = with(binding) {
        ivClose.setOnClickListener { requireDialog().dismiss() }
        recyclerView.adapter = adapter
        val context = root.context
        val greyColor = ContextCompat.getColor(context, R.color.grey_40)
        val primaryColor = ContextCompat.getColor(context, R.color.orange50)
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled)
            ), intArrayOf(
                greyColor,
                primaryColor
            )
        )
        if (selectedReason.isEmpty()) {
            btnConfirm.isEnabled = false
        }
        btnConfirm.strokeColor = colorStateList
        btnConfirm.setTextColor(colorStateList)
        btnConfirm.setOnClickListener {
            if (selectedReason != "OTHERS") {
                note = ""
            }
            onButtonClickListener?.invoke(selectedReason, note)
        }
    }

    private fun observeViewModel() {
        viewModel.cancelReason.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UiState.Loading -> {}
                is UiState.Error -> {}
                is UiState.Success -> {
                    adapter.submitList(result.cancelReason)
                }
            }
        }
    }

    companion object {
        fun getInstance(
            onButtonClick: (String, String) -> Unit
        ) = CancelCashCollectionBottomSheet().apply {
            onButtonClickListener = onButtonClick
        }
    }
}