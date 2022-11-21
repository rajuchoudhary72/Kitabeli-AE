package com.kitabeli.ae.ui.kios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.FragmentKiosBinding
import com.kitabeli.ae.ui.addproduct.AddProductBottomSheet
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KiosFragment : Fragment() {
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
        }
        return binding.root
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
                .kiosStocks
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    if (it is KiosUiState.Success) {
                        it.report?.stockOpNameItemDTOS?.let {
                            productAdapter.submitList(it)
                        }
                        binding.kiosCode.text = it.report?.kiosCode
                    }
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

                    }
                    .show(childFragmentManager, "")
            }

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}