package com.kitabeli.ae.ui.kios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.StockOpNameItemDTOS
import com.kitabeli.ae.databinding.FragmentKiosBinding
import com.kitabeli.ae.ui.MainActivity
import com.kitabeli.ae.ui.addcheckStock.ConfirmationDialog
import com.kitabeli.ae.ui.addproduct.AddProductBottomSheet
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.ui.home.HomeFragmentDirections
import com.kitabeli.ae.utils.ext.openWhatsAppSupport
import com.kitabeli.ae.utils.ext.showLogoutDialog
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

    private var selectedProductIds: List<Int> = emptyList()

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                kiosViewModel.isKioskOwner.collectLatest { isKioskOwner ->
                    kiosViewModel.isKioskOwnerUser.value = isKioskOwner
                    if (isKioskOwner) {
                        (activity as MainActivity).apply {
                            setSupportActionBar(binding.toolbar)
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        }
                        binding.btn.setOnClickListener {
                            askForConfirmation()
                        }
                    } else {
                        binding.btn.setOnClickListener {
                            kiosViewModel.markEligibleForQa()
                        }
                    }
                }
            }
        }

        binding.recyclerViewKios.adapter = productAdapter
        binding.recyclerViewKios.addItemDecoration(
            LinearMarginDecoration.create(
                margin = resources.getDimensionPixelOffset(
                    R.dimen.screen_margin
                )
            )
        )

        productAdapter.onClickEdit = { item ->
            addProduct(item)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            kiosViewModel
                .kiosDetail
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { kiosDetails ->
                    val itemDTOS = kiosDetails?.stockOpNameItemDTOS
                    val filteredProductList = itemDTOS?.filter { it.status != REJECTED_ITEM_STATUS }
                    productAdapter.status = { kiosDetails?.status ?: "" }
                    productAdapter.submitList(itemDTOS)
                    productAdapter.submitList(itemDTOS) {
                        productAdapter.notifyDataSetChanged()
                    }
                    selectedProductIds = filteredProductList?.map { it.skuId ?: 0 }.orEmpty()
                    binding.kiosCode.text = kiosDetails?.kiosCode
                    /*   binding.floatingActionButton.isVisible =
                           kiosDetails?.isStatusCompleted()?.not() ?: true
                       binding.btn.isEnabled = kiosDetails?.isStatusCompleted() ?: false*/
                }
        }

        binding.floatingActionButton.setOnClickListener {
            addProduct()

        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshKiosDetails()
        }
        binding.btnRefresh.setOnClickListener {
            refreshKiosDetails()
        }
        binding.btnTandaTanganDokumen.setOnClickListener {
            kiosViewModel.getKiosDetails()?.let { kios ->
                findNavController().navigate(
                    KiosFragmentDirections.actionKiosFragmentToAddCheckStockFragment(
                        kios.stockOnNameId!!
                    )
                )
            }
        }
        binding.btnLogout.setOnClickListener { logout() }
        binding.btnHelp.setOnClickListener {
            activity?.openWhatsAppSupport()
        }
    }

    private fun askForConfirmation() {
        ConfirmationDialog()
            .setContent(
                title = getString(R.string.check_stock_dialog_title_text),
                message = getString(R.string.check_stock_dialog_desc_text),
                confirmButtonText = getString(R.string.ya_yakin),
                cancelButtonText = getString(R.string.belum_cek_kembali)
            )
            .setConfirmListener {
                kiosViewModel.markEligibleForQa()
            }.show(childFragmentManager, ConfirmationDialog::class.java.simpleName)
    }

    private fun addProduct(item: StockOpNameItemDTOS? = null) {
        kiosViewModel.getKiosDetails()?.let { kios ->
            AddProductBottomSheet
                .getInstance(
                    kios.stockOnNameId!!,
                    kios.kiosCode!!,
                    item
                )
                .setProductAddListener(
                    selectedIds = selectedProductIds,
                    listener = { refreshKiosDetails() }
                )
                .show(childFragmentManager, "")
        }
    }

    private fun refreshKiosDetails() {
        kiosViewModel.fetchKissDetails { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }
    }

    private fun logout() {
        requireContext().showLogoutDialog {
            kiosViewModel.logout {
                findNavController().navigate(
                    KiosFragmentDirections.actionKiosFragmentToLoginFragment()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REJECTED_ITEM_STATUS = "QA_REJECTED"
    }
}