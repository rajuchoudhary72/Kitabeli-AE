package com.kitabeli.ae.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.databinding.FragmentLoginBinding
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.utils.getVersionInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel>() {
    private var _binding: FragmentLoginBinding? = null

    private val loginViewModel: LoginViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            loginViewModel = this@LoginFragment.loginViewModel
        }
        return binding.root
    }

    override fun getViewModel(): LoginViewModel {
        return loginViewModel
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvVersionInfo.text = "v-${activity?.getVersionInfo()}"


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getUserSession()
            }
        }
    }

    private suspend fun getUserSession() {
        loginViewModel
            .isUserSessionAvailable
            .collectLatest { isAvailable ->
                if (isAvailable) {
                    validateKioskOwner()
                }
            }
    }

    private suspend fun validateKioskOwner() {
        loginViewModel
            .kioskCode
            .collectLatest { kioskCode ->
                if (kioskCode.isNotEmpty()) {
                    validateStockOpname()
                } else {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    )
                }
            }
    }

    private fun validateStockOpname() {
        loginViewModel.fetchKioskData { stockOpnameId, stockOpStatus ->
            when {
                stockOpnameId != null -> {
                    when (stockOpStatus) {
                        "CANCELLED",
                        "CANCELLED_BY_ADMIN",
                        "CANCELLED_BY_KIOSK_OWNER",
                        "EXPIRED" -> {
                            findNavController().navigate(
                                LoginFragmentDirections.actionToStockOpOnBoardingFragment()
                            )
                        }
                        "REPORT_GENERATED" -> {
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToAddCheckStockFragment(
                                    stockOpnameId
                                )
                            )
                        }
                        else -> {
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToKioskFragment(
                                    stockOpnameId
                                )
                            )
                        }
                    }
                }
                else -> {
                    findNavController().navigate(
                        LoginFragmentDirections.actionToStockOpOnBoardingFragment()
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}