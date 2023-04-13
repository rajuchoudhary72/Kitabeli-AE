package com.kitabeli.ae.ui.resign_and_return.return_request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.RefillRequestDto
import com.kitabeli.ae.data.remote.dto.ReturnProductDto
import com.kitabeli.ae.databinding.FragmentReturnProductBinding
import com.kitabeli.ae.ui.MainActivity
import com.kitabeli.ae.ui.addcheckStock.ConfirmationDialog
import com.kitabeli.ae.ui.addcheckStock.OtpDialog
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.ui.resign_and_return.ResignAndReturnViewModel
import com.kitabeli.ae.ui.resign_and_return.return_request.ReturnRequestProductAdapter.Companion.EXPIRED_ITEM
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ReturnRequestFragment : BaseFragment<ResignAndReturnViewModel>() {

    private var _binding: FragmentReturnProductBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: ResignAndReturnViewModel by viewModels()

    private val adapter by lazy {
        ReturnRequestProductAdapter(
            onDeleteButtonClick = { product ->
                mViewModel.deleteReturnRequestProduct(
                    refillRequestId = refillRequestId,
                    itemId = product.itemId,
                    onSuccess = {
                        mViewModel.getReturnRequestItemList(refillRequestId)
                    }
                )
            },
            onEditButtonClick = { product ->
                showAddItemDialog(product)
            },
            onAddButtonClick = {
                showAddItemDialog()
            }
        )
    }

    private var kioskCode = ""
    private var refillRequestId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kioskCode = arguments?.getString("KIOSK_CODE").orEmpty()
        mViewModel.createRefillRequest(kioskCode)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReturnProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        initView()
        observeViewModel()
    }

    private fun setToolbar() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        (activity as MainActivity).supportActionBar?.title = getString(R.string.item_return_title)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun getViewModel(): ResignAndReturnViewModel {
        return mViewModel
    }

    private fun initView() {
        with(binding) {
            recyclerView.adapter = adapter
            btnAccept.isEnabled = false
            btnAccept.setOnClickListener {
                askForConfirmation()
            }
        }
    }

    private fun observeViewModel() {
        mViewModel.returnItemList.observe(viewLifecycleOwner) { result: List<ReturnProductDto>? ->
            if (result?.isEmpty() == true) {
                adapter.submitList(listOf(ReturnProductDto()))
            } else {
                val list = result?.toMutableList() ?: mutableListOf()
                list.add(ReturnProductDto())
                adapter.submitList(list)
                binding.btnAccept.isEnabled = list.size > 1
            }
        }
        mViewModel.refillRequest.observe(viewLifecycleOwner) { result: RefillRequestDto? ->
            result?.id?.let {
                refillRequestId = it
                mViewModel.getReturnRequestItemList(refillRequestId)
            }
        }
    }

    private fun showAddItemDialog(product: ReturnProductDto? = null) {
        val requestFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
        val hasExpiredDate = product?.reason == EXPIRED_ITEM && product.details != null
        val selectedDate = when {
            hasExpiredDate -> requestFormat.parse(product?.details!!) ?: Date()
            else -> null
        }
        AddReturnProductDialog
            .getInstance(kioskCode, refillRequestId)
            .setSelectedDate(selectedDate)
            .setSelectedProduct(product)
            .setSelectedProductsId(
                mViewModel.returnItemList.value?.map {
                    it.itemId ?: 0L
                }.orEmpty()
            )
            .setOnProductAddedListener {
                mViewModel.getReturnRequestItemList(refillRequestId)
            }
            .show(childFragmentManager, AddReturnProductDialog::class.java.simpleName)
    }

    private fun askForConfirmation() {
        ConfirmationDialog().also {
            it.setContent(
                title = getString(R.string.does_inputted_stock_correct),
                message = getString(R.string.does_inputted_stock_correct_desc),
                confirmButtonText = getString(R.string.betul),
                cancelButtonText = getString(R.string.tidak)
            ).setConfirmListener {
                mViewModel.createStockReturnRequest(refillRequestId) { stockTrfId ->
                    showOtpDialog(stockTrfId)
                }
            }.show(childFragmentManager, ConfirmationDialog::class.java.simpleName)
        }
    }

    private fun showOtpDialog(stockTrfId: String?) {
        OtpDialog.getInstance(otpType = OtpDialog.OtpType.RETURN_REQUEST, stockTrfId = stockTrfId)
            .setOnOtpSuccessListener {
                showMessage(getString(R.string.return_request_success_msg))
                findNavController().popBackStack(R.id.fragment_post_login, false)
            }
            .show(childFragmentManager, OtpDialog::class.java.simpleName)
    }
}