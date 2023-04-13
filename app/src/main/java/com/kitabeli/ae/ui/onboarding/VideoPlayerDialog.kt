package com.kitabeli.ae.ui.onboarding

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.kitabeli.ae.databinding.DialogVideoPlayerBinding
import com.kitabeli.ae.utils.Constants.STOCK_OPNAME_TRAINING_VIDEO

class VideoPlayerDialog : DialogFragment() {

    private var player: ExoPlayer? = null
    private val playWhenReady: Boolean = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogVideoPlayerBinding.inflate(layoutInflater, null, false)
        val dialog = AlertDialog.Builder(requireContext()).setView(binding.root).create()
        val dialogFragment = this@VideoPlayerDialog
        dialog.setCancelable(true)

        with(binding) {
            ivClose.setOnClickListener {
                dialogFragment.dismiss()
            }
            btnConfirm.setOnClickListener {
                dialogFragment.dismiss()
            }
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        initializePlayer(binding)
        return dialog
    }

    private fun initializePlayer(binding: DialogVideoPlayerBinding) {
        player = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                binding.player.player = exoPlayer
                val mediaItem = MediaItem.fromUri(STOCK_OPNAME_TRAINING_VIDEO)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(0, 0L)
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer() {
        player?.run {
            playWhenReady = false
            release()
        }
        player = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        releasePlayer()
    }
}