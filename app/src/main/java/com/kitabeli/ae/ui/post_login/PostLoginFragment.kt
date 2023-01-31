package com.kitabeli.ae.ui.post_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.databinding.FragmentPostLoginBinding
import com.kitabeli.ae.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostLoginFragment : BaseFragment<PostLoginViewModel>() {

    private var _binding: FragmentPostLoginBinding? = null
    private val binding get() = _binding!!

    private val postLoginViewModel: PostLoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPostLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getViewModel(): PostLoginViewModel {
        return postLoginViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {
        with(binding) {
            ivStokKios.setOnClickListener {
                findNavController().navigate(PostLoginFragmentDirections.actionToHomeFragment())
            }

            ivTrainingVideo.setOnClickListener {
                findNavController().navigate(PostLoginFragmentDirections.actionToTrainingVideo())
            }
        }
    }
}