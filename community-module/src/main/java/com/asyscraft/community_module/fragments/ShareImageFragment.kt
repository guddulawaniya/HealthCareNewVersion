package com.asyscraft.community_module.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.R
import com.asyscraft.community_module.adpaters.GalleryImageAdapter
import com.asyscraft.community_module.databinding.FragmentShareImageBinding
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareImageFragment : BaseFragment() {
    private lateinit var binding : FragmentShareImageBinding
    private lateinit var imageAdapter: GalleryImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShareImageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpImageRecyclerView()
    }

    private fun setUpImageRecyclerView() {
        val imageDataList = mutableListOf<Int>()
        imageDataList.add(com.careavatar.core_ui.R.drawable.sample_image)
        imageDataList.add(com.careavatar.core_ui.R.drawable.group_sample)
        imageDataList.add(com.careavatar.core_ui.R.drawable.service_userprofile_image_sample)
        imageDataList.add(com.careavatar.core_ui.R.drawable.sample_image)

        imageAdapter = GalleryImageAdapter(imageDataList)
        binding.shareGalleryRecyclerview.adapter = imageAdapter
        binding.shareGalleryRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }
}