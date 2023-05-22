package com.kitabeli.ae.ui.scanqr

import android.os.Bundle
import android.util.Size
import android.view.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.kitabeli.ae.R
import com.kitabeli.ae.data.remote.dto.KiosDto
import com.kitabeli.ae.databinding.FragmentScanqrBinding
import com.kitabeli.ae.ui.common.BaseFragment
import com.kitabeli.ae.ui.home.HomeViewModel
import com.kitabeli.ae.ui.post_login.PostLoginFragment
import com.kitabeli.ae.ui.post_login.PostLoginFragment.Companion.KIOSK_CODE_ARG
import com.kitabeli.ae.ui.post_login.PostLoginFragment.Companion.KIOSK_CODE_REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint
import maulik.barcodescanner.analyzer.MLKitBarcodeAnalyzer
import maulik.barcodescanner.analyzer.ScanningResultListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val ARG_SCANNING_SDK = "scanning_SDK"

@AndroidEntryPoint
class ScanQrFragment : BaseFragment<HomeViewModel>() {
    private var _binding: FragmentScanqrBinding? = null

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService
    private val homeViewModel: HomeViewModel by viewModels()
    private val binding get() = _binding!!

    private var shouldInitializeStockOp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldInitializeStockOp = arguments?.getBoolean("INIT_STOCK_OP") ?: true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentScanqrBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        initScanner()

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun getViewModel(): HomeViewModel {
        return homeViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun initScanner() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {

        if (requireActivity().isDestroyed || requireActivity().isFinishing) {
            //This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            //java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return
        }

        cameraProvider?.unbindAll()

        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(binding.cameraPreview.width, binding.cameraPreview.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val orientationEventListener = object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation: Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
            }
        }
        orientationEventListener.enable()

        //switch the analyzers here, i.e. MLKitBarcodeAnalyzer, ZXingBarcodeAnalyzer
        class ScanningListener : ScanningResultListener {
            override fun onScanned(result: String) {
                requireActivity().runOnUiThread {
                    imageAnalysis.clearAnalyzer()
                    cameraProvider?.unbindAll()
                    if (result.contains("kioskId")) {
                        val qrCodeValue = result.split("kioskId")[1].replace("=", "")
                        if (shouldInitializeStockOp) {
                            homeViewModel.initializeStock(qrCodeValue) { kios: KiosDto ->
                                findNavController().popBackStack(R.id.fragment_post_login, false)
                                findNavController().navigate(
                                    R.id.homeFragment,
                                    bundleOf("qrcodevalue" to qrCodeValue)
                                )
                            }
                        } else {
                            setFragmentResult(
                                KIOSK_CODE_REQUEST_KEY,
                                bundleOf(KIOSK_CODE_ARG to qrCodeValue)
                            )
                            findNavController().popBackStack()
                        }
                    } else {
                        showMessage(getString(R.string.error_unknown))
                    }
                }
            }
        }

        val analyzer: ImageAnalysis.Analyzer = MLKitBarcodeAnalyzer(ScanningListener())
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)
        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        val camera =
            cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

        /* if (camera?.cameraInfo?.hasFlashUnit() == true) {
             binding.ivFlashControl.visibility = View.VISIBLE

             binding.ivFlashControl.setOnClickListener {
                 camera.cameraControl.enableTorch(!flashEnabled)
             }

             camera.cameraInfo.torchState.observe(this) {
                 it?.let { torchState ->
                     if (torchState == TorchState.ON) {
                         flashEnabled = true
                         binding.ivFlashControl.setImageResource(R.drawable.ic_round_flash_on)
                     } else {
                         flashEnabled = false
                         binding.ivFlashControl.setImageResource(R.drawable.ic_round_flash_off)
                     }
                 }
             }
         }*/


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}