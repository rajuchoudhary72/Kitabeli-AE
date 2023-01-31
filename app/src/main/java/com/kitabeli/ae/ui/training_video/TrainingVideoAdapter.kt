package com.kitabeli.ae.ui.training_video

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.TrainingVideoDto
import com.kitabeli.ae.databinding.ItemTrainingVideoBinding

class TrainingVideoAdapter(val onVideoClick: (videoId: String) -> Unit) :
    RecyclerView.Adapter<TrainingVideoAdapter.TVVH>() {

    private var list: List<TrainingVideoDto.Payload.Item> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listData: List<TrainingVideoDto.Payload.Item>) {
        list = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVVH {
        return TVVH(
            ItemTrainingVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TVVH, position: Int) {
        holder.setData(list[position])
    }

    override fun getItemCount() = list.size


    inner class TVVH(val view: ItemTrainingVideoBinding) : RecyclerView.ViewHolder(view.root) {

        init {
            val radius = view.root.context.resources.getDimension(R.dimen.corner_radius)
            val shapeAppearanceModel = view.ivVideoImage.shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(radius)
                .build()
            view.ivVideoImage.shapeAppearanceModel = shapeAppearanceModel
        }

        fun setData(item: TrainingVideoDto.Payload.Item) {
            with(view) {
                Glide.with(view.root.context).load(item.thumbnail).into(ivVideoImage)
                tvVideoTitle.text = item.title
                tvVideoDesc.text = item.description.orEmpty()

                ivVideoImage.setOnClickListener {
                    onVideoClick(item.videoDTO?.urls?.mp4?.substringAfterLast("/").orEmpty())
                }
            }
        }

    }
}