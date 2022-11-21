package com.kitabeli.ae.ui.addcheckStock

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.R
import com.kitabeli.ae.databinding.FragmentAddCheckStockBinding
import com.kitabeli.ae.ui.signature.SignatureFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class AddCheckStockFragment : Fragment() {
    private var _binding: FragmentAddCheckStockBinding? = null

    private val viewModel: AddCheckStockViewModel by viewModels()
    private val binding get() = _binding!!

    @Inject
    lateinit var kiosAdapter: StockItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddCheckStockBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            addCheckStockViewModel = viewModel
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = kiosAdapter


        viewLifecycleOwner.lifecycleScope.launch {

            viewModel
                .uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest {

                }
        }

        val list: List<Int> = (0..7).map { it }

        kiosAdapter.submitList(list)

        binding.btn.setOnClickListener {
            ConfirmationDialog()
                .setCodeInputListener {
                    OtpDialog()
                        .setCodeInputListener {
                            val bitmap = getBitmapFromView(
                                binding.scrollView,
                                binding.scrollView.getChildAt(0).height,
                                binding.scrollView.getChildAt(0).width
                            )



                            if (bitmap != null) {
                                val file =
                                    File(context?.filesDir?.path + File.separator + "Screenshot.png")
                                file.createNewFile()

                                file.write(
                                    bitmap,
                                    Bitmap.CompressFormat.JPEG,
                                    100
                                )
                            }

                            //findNavController().navigate()

                        }
                        .show(childFragmentManager, "")
                }
                .show(childFragmentManager, "qwe")
        }

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.signMitra.setOnClickListener {
            collectSignature(true)
        }

        binding.signAe.setOnClickListener {
            collectSignature(false)
        }

        binding.layoutStockItemHeader.imgInfoCekStokTerakhir.setOnClickListener {
            findNavController().navigate(
                R.id.photoViewFragment
            )
        }
        binding.layoutStockItemHeader.imgInfoTopUp.setOnClickListener {
            findNavController().navigate(
                R.id.photoViewFragment
            )
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                launch {
                    viewModel.mirtaSignature.collectLatest {
                        binding.mirtaSign.setImageBitmap(it)
                    }
                }

                launch {
                    viewModel.aeSignature.collectLatest {
                        binding.aeSign.setImageBitmap(it)
                    }
                }

            }
        }
    }

    private fun collectSignature(isMitra: Boolean) {
        setFragmentResultListener(SignatureFragment.REQUEST_KEY_SIGNATURE) { _, bundle ->
            if (bundle.containsKey(SignatureFragment.SIGNATURE_BITMAP)) {
                bundle.getParcelable<Bitmap>(SignatureFragment.SIGNATURE_BITMAP)?.let { signature ->
                    if (isMitra)
                        viewModel.setMitraSignature(signature)
                    else
                        viewModel.setAeSignature(signature)

                    binding.scrollView.smoothScrollTo(0, binding.scrollView.getChildAt(0).height)

                }
            }

        }
        findNavController().navigate(R.id.signatureFragment)
    }

    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    private fun File.createFileAndDirs() = apply {
        parentFile?.mkdirs()
        createNewFile()
    }

    fun File.write(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 80
    ) = apply {
        createFileAndDirs()
        outputStream().use {
            bitmap.compress(format, quality, it)
            it.flush()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}