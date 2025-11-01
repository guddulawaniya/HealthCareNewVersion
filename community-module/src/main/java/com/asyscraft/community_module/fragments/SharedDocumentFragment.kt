package com.asyscraft.community_module.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.R
import com.asyscraft.community_module.adpaters.DocumentShareAdapter
import com.asyscraft.community_module.adpaters.GalleryImageAdapter
import com.asyscraft.community_module.databinding.FragmentSharedDocumentBinding
import com.careavatar.core_model.shareDocumentModel
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SharedDocumentFragment : BaseFragment() {
    private lateinit var binding: FragmentSharedDocumentBinding
    private lateinit var documentAdapter: DocumentShareAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedDocumentBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpImageRecyclerView()
    }

    private fun setUpImageRecyclerView() {
        val imageDataList = mutableListOf<shareDocumentModel>()
        imageDataList.add(shareDocumentModel("Project Proposal","PDF 1.3MB"))
        imageDataList.add(shareDocumentModel("Personal Project","PDF 1.3MB"))
        imageDataList.add(shareDocumentModel("Project Proposal","PDF 1.3MB"))
        imageDataList.add(shareDocumentModel("Project Proposal","PDF 1.3MB"))
        imageDataList.add(shareDocumentModel("Project Proposal","PDF 1.3MB"))

        documentAdapter = DocumentShareAdapter(imageDataList)
        binding.shareDocumentRecyclerview.adapter = documentAdapter
        binding.shareDocumentRecyclerview.layoutManager =
            LinearLayoutManager(requireContext())
    }
}