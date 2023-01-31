package com.kitabeli.ae.ui.training_video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.FragmentTrainingVideoBinding
import com.kitabeli.ae.ui.MainActivity
import com.kitabeli.ae.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingVideoFragment : BaseFragment<TrainingVideoViewModel>() {

    private var _binding: FragmentTrainingVideoBinding? = null
    private val binding get() = _binding!!

    private val trainingVideoViewModel: TrainingVideoViewModel by viewModels()

    private val tvAdapter: TrainingVideoAdapter by lazy {
        TrainingVideoAdapter { videoId ->
            findNavController().navigate(
                R.id.fragment_video_player,
                bundleOf("videoId" to videoId)
            )
        }
    }


    override fun getViewModel(): TrainingVideoViewModel {
        return trainingVideoViewModel
    }

    private fun setToolbar() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        (activity as MainActivity).supportActionBar?.title = "Video Pelatihan"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrainingVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setToolbar()
        initView()
        callAPI()
    }

    private fun initView() {
        with(binding) {
            rvVideo.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = tvAdapter
            }
        }
    }

    private fun callAPI() {
        trainingVideoViewModel.getTrainingVideos {
            tvAdapter.setData(it?.items.orEmpty())
        }
    }
}