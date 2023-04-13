package com.kitabeli.ae.ui.resign_and_return

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.FragmentResignAndReturnBinding
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.utils.Constants.RESIGN_HEADER_IMG_URL
import com.kitabeli.ae.utils.Constants.RESIGN_IMG_URL
import com.kitabeli.ae.utils.Constants.RETURN_IMG_URL
import com.kitabeli.ae.utils.ext.hideSoftInput
import com.kitabeli.ae.utils.ext.loadImageFromURL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResignAndReturnFragment : BaseFragment<ResignAndReturnViewModel>() {

    private var _binding: FragmentResignAndReturnBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: ResignAndReturnViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResignAndReturnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getViewModel(): ResignAndReturnViewModel {
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.hideSoftInput()
        initView()
    }


    private fun initView() = with(binding) {
        ivHeader.loadImageFromURL(RESIGN_HEADER_IMG_URL)
        ivResign.loadImageFromURL(RESIGN_IMG_URL)
        ivReturn.loadImageFromURL(RETURN_IMG_URL)

        cvResign.setOnClickListener {
            findNavController().navigate(
                R.id.fragment_resign_form,
                bundleOf("KIOSK_CODE" to arguments?.getString("KIOSK_CODE"))
            )
        }

        cvReturn.setOnClickListener {
            findNavController().navigate(
                R.id.fragment_return_request,
                bundleOf("KIOSK_CODE" to arguments?.getString("KIOSK_CODE"))
            )
        }

        btnAccept.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
    }
}