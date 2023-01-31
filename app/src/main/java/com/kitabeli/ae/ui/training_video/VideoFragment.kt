package com.kitabeli.ae.ui.training_video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.viewModels
import com.kitabeli.ae.databinding.FragmentVideoPlayerBinding
import com.kitabeli.ae.ui.common.BaseFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment : BaseFragment<TrainingVideoViewModel>() {

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private val trainingVideoViewModel: TrainingVideoViewModel by viewModels()

    private var videoId: String? = null

    override fun getViewModel(): TrainingVideoViewModel {
        return trainingVideoViewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoId = it.getString("videoId")
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {

        with(binding) {
            lifecycle.addObserver(youtubePlayerView)

            youtubePlayerView.toggleFullScreen()
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
                    videoId?.let { youTubePlayer.loadVideo(it, 0f) }
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.release()
        _binding = null
    }
}