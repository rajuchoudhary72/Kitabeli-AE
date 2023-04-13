package com.kitabeli.ae.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.databinding.FragmentStockOpOnBoardingBinding
import com.kitabeli.ae.ui.MainActivity
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.utils.ext.openWhatsAppSupport
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StockOpOnBoardingFragment : BaseFragment<StockOpOnBoardingViewModel>() {

    private lateinit var binding: FragmentStockOpOnBoardingBinding

    private val onBoardingViewModel: StockOpOnBoardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStockOpOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getViewModel(): StockOpOnBoardingViewModel {
        return onBoardingViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })
    }


    private fun initView() {
        with(binding) {
            btnHelp.setOnClickListener {
                activity?.openWhatsAppSupport()
            }
            clPlayVideo.setOnClickListener {
                VideoPlayerDialog().show(childFragmentManager, VideoPlayerDialog::class.simpleName)
            }
            btnCancel.setOnClickListener {
                activity?.finish()
            }
            btnAccept.setOnClickListener {
                initializeStock()
            }
            (activity as MainActivity).apply {
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    private fun initializeStock() {
        viewLifecycleOwner.lifecycleScope.launch {
            onBoardingViewModel
                .kioskCode
                .collectLatest { kioskCode ->
                    if (kioskCode.isNotEmpty()) {
                        validateStockOpnameId(kioskCode)
                    } else {
                        showToast("Kode kios tidak ditemukan")
                    }
                }
        }
    }

    private fun validateStockOpnameId(kioskCode: String) {
        onBoardingViewModel.initializeStock(kioskCode) { stockOpnameId ->
            when {
                stockOpnameId != null -> {
                    findNavController().navigate(
                        StockOpOnBoardingFragmentDirections.actionToKioskFragment(
                            stockOpnameId
                        )
                    )
                }
            }
        }
    }
}