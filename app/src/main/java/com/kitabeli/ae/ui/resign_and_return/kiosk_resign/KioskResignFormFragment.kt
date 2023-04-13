package com.kitabeli.ae.ui.resign_and_return.kiosk_resign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.FragmentResignFormBinding
import com.kitabeli.ae.ui.MainActivity
import com.kitabeli.ae.ui.addcheckStock.ConfirmationDialog
import com.kitabeli.ae.ui.addcheckStock.OtpDialog
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.ui.resign_and_return.ResignAndReturnViewModel
import com.kitabeli.ae.ui.resign_and_return.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KioskResignFormFragment : BaseFragment<ResignAndReturnViewModel>() {

    private var _binding: FragmentResignFormBinding? = null
    private val binding get() = _binding!!

    private val resignAndReturnViewModel: ResignAndReturnViewModel by viewModels()
    private var kioskCode = ""
    private var formId: Int = 0

    val adapter by lazy {
        KioskResignFormAdapter(
            onItemClick = { data, size ->
                binding.btnAccept.isEnabled = data.size >= size
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resignAndReturnViewModel.getKioskResignForm()
        kioskCode = arguments?.getString("KIOSK_CODE").orEmpty()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResignFormBinding.inflate(inflater, container, false)
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
        (activity as MainActivity).supportActionBar?.title =
            getString(R.string.pengunduran_diri_kios)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun getViewModel(): ResignAndReturnViewModel {
        return resignAndReturnViewModel
    }

    private fun initView() {
        with(binding) {
            recyclerView.adapter = adapter
            btnAccept.also {
                it.isEnabled = false
                it.setOnClickListener {
                    showSubmitFormConfirmationDialog()
                }
            }
        }
    }

    private fun observeViewModel() {
        resignAndReturnViewModel.resignForm.observe(viewLifecycleOwner) { result ->
            when (result) {
                is UiState.Loading -> {}
                is UiState.Error -> {}
                is UiState.Success -> {
                    formId = result.resignForm?.id ?: 0
                    adapter.submitList(result.resignForm?.questionDTOS)
                }
            }
        }

        resignAndReturnViewModel.isFormSubmitted.observe(viewLifecycleOwner) { success: Boolean? ->
            if (success == true) {
                showOtpVerificationDialog()
            }
        }

        resignAndReturnViewModel.isOtpResent.observe(viewLifecycleOwner) { success: Boolean? ->
            if (success == true) {
                showToast(getString(R.string.success_resend_otp))
                showOtpVerificationDialog()
            }
        }
    }

    private fun showSubmitFormConfirmationDialog() {
        ConfirmationDialog().also {
            it.setContent(
                title = getString(R.string.confirm_resign_title),
                message = getString(R.string.confirm_resign_desc),
                confirmButtonText = getString(R.string.betul),
                cancelButtonText = getString(R.string.tidak)
            ).setConfirmListener {
                resignAndReturnViewModel.submitKioskResignForm(
                    formId = formId,
                    kioskCode = kioskCode,
                    responses = adapter.responseMap
                )
            }.show(childFragmentManager, ConfirmationDialog::class.java.simpleName)
        }
    }

    private fun showOtpVerificationDialog() {
        OtpDialog.getInstance(
            otpType = OtpDialog.OtpType.KIOSK_RESIGN,
            kioskCode = kioskCode,
            resignFormId = formId
        ).setOnOtpSuccessListener {
            showMessage(getString(R.string.resign_request_success_msg))
            findNavController().popBackStack(R.id.fragment_post_login, false)
        }.setOnResendOTPListener {
            resignAndReturnViewModel.resendKioskResignOtp(
                formId = formId,
                kioskCode = kioskCode
            )
        }.setKioskResignResponse(adapter.responseMap).show(
            childFragmentManager,
            OtpDialog::class.java.simpleName
        )
    }
}