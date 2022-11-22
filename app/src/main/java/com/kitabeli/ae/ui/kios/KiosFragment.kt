package com.kitabeli.ae.ui.kios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.FragmentKiosBinding
import com.kitabeli.ae.ui.addproduct.AddProductBottomSheet
import com.kitabeli.ae.ui.common.BaseFragment
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KiosFragment : BaseFragment<KiosViewModel>() {
    private var _binding: FragmentKiosBinding? = null

    private val kiosViewModel: KiosViewModel by viewModels()

    private val binding get() = _binding!!

    @Inject
    lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentKiosBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = kiosViewModel
        }
        return binding.root
    }

    override fun getViewModel(): KiosViewModel {
        return kiosViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.recyclerViewKios.adapter = productAdapter
        binding.recyclerViewKios.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(
                    R.dimen.screen_margin
                )
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            kiosViewModel
                .kiosDetail
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { kiosDetails ->
                    productAdapter.submitList(kiosDetails?.stockOpNameItemDTOS)
                    binding.kiosCode.text = kiosDetails?.kiosCode
                    binding.floatingActionButton.isVisible =
                        kiosDetails?.isStatusCompleted()?.not() ?: true
                    binding.btn.isEnabled = kiosDetails?.isStatusCompleted() ?: false
                }
        }

        binding.floatingActionButton.setOnClickListener {
            kiosViewModel.getKiosDetails()?.let { kios ->
                AddProductBottomSheet
                    .getInstance(
                        kios.stockOnNameId!!,
                        kios.kiosCode!!
                    )
                    .setProductAddListener {
                        refreshKiosDetails()
                    }
                    .show(childFragmentManager, "")
            }

        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshKiosDetails()
        }

        binding.btn.setOnClickListener {
            kiosViewModel.getKiosDetails()?.let { kios ->
                findNavController().navigate(
                    KiosFragmentDirections.actionKiosFragmentToAddCheckStockFragment(
                        kios.stockOnNameId!!
                    )
                )
            }
        }

    }

    private fun refreshKiosDetails() {
        kiosViewModel.fetchKissDetails { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}