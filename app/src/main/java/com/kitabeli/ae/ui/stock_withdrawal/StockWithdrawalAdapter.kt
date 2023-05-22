package com.kitabeli.ae.ui.stock_withdrawal

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.StockWithdrawalDto
import com.kitabeli.ae.databinding.ItemStockWithdrawalBinding
import com.kitabeli.ae.utils.showGone
import java.io.File
import javax.inject.Inject

class StockWithdrawalAdapter @Inject constructor(
    val updateBottomText: (String?, String?) -> Unit,
    val onFullImageClick: (Uri) -> Unit
) :
    RecyclerView.Adapter<StockWithdrawalAdapter.StockWithdrawalVH>() {

    private var list: List<StockWithdrawalDto.ItemDetailDTOS> = emptyList()
    private var fromOTPSuccess = false
    private lateinit var fileUri: Uri

    fun setData(listData: List<StockWithdrawalDto.ItemDetailDTOS>) {
        list = listData
        notifyDataSetChanged()
    }

    fun fromOTPSuccess(value: Boolean, fileValue: Uri) {
        fromOTPSuccess = value
        fileUri = fileValue
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockWithdrawalVH {
        return StockWithdrawalVH(
            ItemStockWithdrawalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: StockWithdrawalVH, position: Int) {
        holder.setData(list[position])
    }


    inner class StockWithdrawalVH(val view: ItemStockWithdrawalBinding) :
        RecyclerView.ViewHolder(view.root) {

        @SuppressLint("SetTextI18n")
        fun setData(data: StockWithdrawalDto.ItemDetailDTOS) = with(view) {
            if (absoluteAdapterPosition == 0) {
                tvSwHeader.showGone(true)
                if (fromOTPSuccess) {
                    tvSwHeader.text = "Detail Stok yang Ditarik"
                    clOtpSuccess.showGone(true)
                }
                if (::fileUri.isInitialized) {
                    tvImg.text = File(FileUriUtils.getRealPath(view.context, fileUri)!!).name
                    ivProof.setImageURI(fileUri)
                    tvFullScreen.setOnClickListener {
                        onFullImageClick.invoke(fileUri)
                    }
                    clProof.showGone(true)
                } else {
                    clProof.showGone(false)
                }

            } else {
                tvSwHeader.showGone(false)
                clOtpSuccess.showGone(false)
            }

            Glide.with(ivProduct.context)
                .load(data.image)
                .placeholder(R.drawable.placeholder_add_photo)
                .error(R.drawable.placeholder_add_photo)
                .into(ivProduct)

            if (fromOTPSuccess) {
                view5.showGone(false)
                tvCheckboxLabel.showGone(false)
                appCompatCheckBox.showGone(false)
            } else {
                view5.showGone(true)
                tvCheckboxLabel.showGone(true)
                appCompatCheckBox.showGone(true)
            }

            appCompatCheckBox.isChecked = data.isSelected

            tvSwProdName.text = data.itemName
            tvSwQuanitity.text = "x ${data.quantity}"

            appCompatCheckBox.setOnClickListener {
                data.isSelected = !data.isSelected
                notifyItemChanged(absoluteAdapterPosition)

                when (val totalChecked = list.filter { it.isSelected }.size) {
                    0 -> {
                        updateBottomText(null, null)
                    }
                    list.size -> {
                        updateBottomText("${list.size} jenis stok ditarik", "(Semua stok ditarik)")
                    }
                    else -> {
                        updateBottomText(
                            "$totalChecked jenis stok ditarik",
                            "(Sisa ${list.size - totalChecked} jenis lagi)"
                        )
                    }
                }
            }

        }

    }
}