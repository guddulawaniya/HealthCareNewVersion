package com.asyscraft.medical_reminder.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asyscraft.medical_reminder.R
import com.asyscraft.medical_reminder.databinding.FragmentScanMedicineBinding
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ScanMedicineFragment : BaseFragment() {

    private lateinit var binding: FragmentScanMedicineBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanMedicineBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}