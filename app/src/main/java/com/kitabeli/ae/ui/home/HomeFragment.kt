package com.kitabeli.ae.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.databinding.FragmentHomeBinding
import com.kitabeli.ae.ui.common.BaseFragment
import com.rubensousa.decorator.LinearMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel>() {
    private var _binding: FragmentHomeBinding? = null

    private val homeViewModel: HomeViewModel by viewModels()
    private val binding get() = _binding!!

    @Inject
    lateinit var kiosAdapter: KiosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = homeViewModel
        }
        return binding.root
    }

    override fun getViewModel(): HomeViewModel {
        return homeViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewKios.adapter = kiosAdapter
        binding.recyclerViewKios.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(
                    R.dimen.screen_margin
                )
            )
        )
        kiosAdapter.onClickItem = { kios ->
            navigateToKios(kios.stockOpnameId!!)
        }

        homeViewModel.kiosData.observe(viewLifecycleOwner) { data ->
            data?.items?.let {
                kiosAdapter.submitList(it)
            }
        }

        binding.btn.setOnClickListener {
            KiosCodeInputDialog()
                .setCodeInputListener {
                    homeViewModel.initializeStock(it) { kios: KiosDto ->
                        refreshKiosData()
                    }
                }
                .show(childFragmentManager, "")
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshKiosData()
        }
    }

    private fun refreshKiosData() {
        homeViewModel.fetchKiosData { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout?")
            .setMessage("Are you sure, you want to logout.")
            .setPositiveButton("Logout") { _, _ ->
                homeViewModel.logout {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                }
            }
            .setNegativeButton("Cancel") { _, _ ->

            }
            .show()
    }

    private fun navigateToKios(stockOpnameId: Int) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToKiosFragment(
                stockOpnameId
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}