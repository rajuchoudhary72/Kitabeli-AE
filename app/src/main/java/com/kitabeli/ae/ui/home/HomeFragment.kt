package com.kitabeli.ae.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.data.remote.dto.KiosItem
import com.kitabeli.ae.databinding.FragmentHomeBinding
import com.kitabeli.ae.ui.MainActivity
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


    private fun setToolbar() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        (activity as MainActivity).supportActionBar?.title =
            resources.getString(R.string.cek_stok_hari_ini)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        binding.recyclerViewKios.adapter = kiosAdapter
        binding.recyclerViewKios.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(
                    R.dimen.screen_margin
                )
            )
        )
        kiosAdapter.onClickItem = { kios ->
            if (kios.status == "REPORT_GENERATED") {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToAddCheckStockFragment(
                        kios.stockOpnameId
                    )
                )
            } else {
                navigateToKios(kios.stockOpnameId)
            }
        }
        homeViewModel.kiosData.observe(viewLifecycleOwner) { data ->
            data?.items?.let {
                kiosAdapter.submitList(it)
                //  checkIsReportGenerated(data.items)
            }
        }

        binding.btn.setOnClickListener {
            KiosCodeInputDialog()
                .setCodeInputListener {
                    homeViewModel.initializeStock(it) { kios: KiosDto ->
                        refreshKiosData()
                    }
                }.permissionListener { s: String ->
                    if (s.isNotEmpty()) {
                        startScanning()
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

        if (!arguments?.getString("qrcodevalue").isNullOrEmpty()) {
            refreshKiosData()
        }

        /*  KiosCodeInputDialog().permissionListener { s: String ->
              if (s.isNotEmpty()) {
                  startScanning()
              }
          }*/
    }

    private fun checkIsReportGenerated(items: List<KiosItem>) {
        items
            .firstOrNull { kios -> kios.status == "REPORT_GENERATED" }
            ?.let { kios ->
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToAddCheckStockFragment(
                        kios.stockOpnameId
                    )
                )
            }
    }

    private fun refreshKiosData() {
        homeViewModel.fetchKiosData { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_))
            .setMessage(getString(R.string.logout_msg))
            .setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog))
            .setPositiveButton(getString(R.string.logout_)) { _, _ ->
                homeViewModel.logout {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->

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

    override fun onResume() {
        super.onResume()
        refreshKiosData()
    }

    fun startScanning() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCameraWithScanner()
        } else {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                openCameraWithScanner()
            } else {
                Snackbar.make(
                    requireActivity().findViewById(R.id.nav_host_fragment_content_main),
                    getString(R.string.cancelled_task),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }

    fun openCameraWithScanner() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToScanqrFragment())
    }
}