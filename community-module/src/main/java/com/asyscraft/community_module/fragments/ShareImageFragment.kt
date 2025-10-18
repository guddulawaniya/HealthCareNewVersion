package com.asyscraft.community_module.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.asyscraft.community_module.R
import com.asyscraft.community_module.databinding.FragmentShareImageBinding
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareImageFragment : BaseFragment() {
    private lateinit var binding : FragmentShareImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShareImageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}