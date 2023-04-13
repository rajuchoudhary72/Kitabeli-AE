package com.kitabeli.ae.ui.post_login

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.FragmentPostLoginBinding
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.ui.home.KiosCodeInputDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostLoginFragment : BaseFragment<PostLoginViewModel>() {

    private var _binding: FragmentPostLoginBinding? = null
    private val binding get() = _binding!!

    private val postLoginViewModel: PostLoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPostLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getViewModel(): PostLoginViewModel {
        return postLoginViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeViewModel()
    }


    private fun initView() {
        with(binding) {
            ivStokKios.setOnClickListener {
                findNavController().navigate(PostLoginFragmentDirections.actionToHomeFragment())
            }

            ivTrainingVideo.setOnClickListener {
                findNavController().navigate(PostLoginFragmentDirections.actionToTrainingVideo())
            }

            clKioskShutdown.setOnClickListener {
                showKioskCodeInputDialog()
            }
        }
    }

    private fun observeViewModel() {
        postLoginViewModel.kioskDetail.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                findNavController().navigate(
                    R.id.fragment_shutdown_and_return,
                    bundleOf("KIOSK_CODE" to result.kioskCode)
                )
            }
            postLoginViewModel.resetKioskDetail()
        }
    }

    private fun showKioskCodeInputDialog() {
        KiosCodeInputDialog().also {
            it.setCodeInputListener { kioskCode ->
                postLoginViewModel.getKioskDetail(kioskCode)
            }.permissionListener { s: String ->
                if (s.isNotEmpty()) {
                    startScanning()
                }
            }.show(childFragmentManager, "")
        }
    }

    private fun startScanning() {
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

    private val activityResultLauncher =
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

    private fun openCameraWithScanner() {
        setFragmentResultListener(KIOSK_CODE_REQUEST_KEY) { _, bundle ->
            if (bundle.containsKey(KIOSK_CODE_ARG)) {
                val kioskCode = bundle.getString(KIOSK_CODE_ARG)
                postLoginViewModel.getKioskDetail(kioskCode)
            }
        }
        findNavController().navigate(
            R.id.scanQRFragment,
            bundleOf("INIT_STOCK_OP" to false)
        )
    }

    companion object {
        const val KIOSK_CODE_REQUEST_KEY = "KIOSK_CODE_REQUEST_KEY"
        const val KIOSK_CODE_ARG = "KIOSK_CODE"
    }
}