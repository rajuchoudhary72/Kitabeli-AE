package com.kitabeli.ae.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.kitabeli.ae.databinding.FragmentKiosBinding
import com.kitabeli.ae.databinding.FragmentMaintanenceBinding
import com.kitabeli.ae.ui.common.BaseFragment

class MaintenanceFragment : Fragment() {

    private var _binding: FragmentMaintanenceBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMaintanenceBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })
    }

}