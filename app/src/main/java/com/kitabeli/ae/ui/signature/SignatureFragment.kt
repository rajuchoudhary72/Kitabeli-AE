package com.kitabeli.ae.ui.signature

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.kitabeli.ae.databinding.FragmentSignatureBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignatureFragment : Fragment() {
    private var _binding: FragmentSignatureBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        _binding = FragmentSignatureBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.icClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnClear.setOnClickListener {
            binding.signaturePad.clear()
        }

        binding.btnSave.setOnClickListener {
            if (binding.signaturePad.isEmpty) {
                Toast.makeText(requireContext(), "Add signature", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setFragmentResult(
                REQUEST_KEY_SIGNATURE, bundleOf(
                    SIGNATURE_BITMAP to binding.signaturePad.getSignatureBitmap()
                )
            )
            findNavController().popBackStack()
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    companion object {
        const val REQUEST_KEY_SIGNATURE = "request_key_signature"
        const val SIGNATURE_BITMAP = "signature_bitmap"
    }
}