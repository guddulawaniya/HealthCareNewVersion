package com.careavatar.dashboardmodule.dashboardfragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.service_module.ui.BookCaretakerActivity
import com.asyscraft.service_module.ui.MedicalServicesActivity
import com.asyscraft.service_module.ui.careTaker_DetailsActivity
import com.careavatar.core_model.ActiveServiceModel
import com.careavatar.core_ui.R
import com.careavatar.dashboardmodule.adapters.ActiveServicesAdapter
import com.careavatar.dashboardmodule.databinding.FragmentServicesBinding
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServicesFragment : BaseFragment() {
    private lateinit var binding: FragmentServicesBinding
    private lateinit var adapter: ActiveServicesAdapter
    private val dataList = mutableListOf<ActiveServiceModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServicesBinding.inflate(inflater, container, false)

        binding.btnBookNow.setOnClickListener {
            startActivity(Intent(requireContext(), BookCaretakerActivity::class.java))
        }

        binding.medicalOrderNow.setOnClickListener {
            startActivity(Intent(requireContext(), MedicalServicesActivity::class.java))
        }

        binding.viewDetails.setOnClickListener {
            startActivity(Intent(requireContext(), careTaker_DetailsActivity::class.java))
        }


        setRecyclerView()
        return binding.root
    }

    private fun setRecyclerView() {

        dataList.add(
            ActiveServiceModel(
                "Sophia Bennet",
                "Caretaker",
                R.drawable.service_userprofile_image_sample, "In Progress"
            )
        )
        dataList.add(
            ActiveServiceModel(
                "Dan",
                "Caretaker",
                R.drawable.sample_image, "Scheduled"
            )
        )

        adapter = ActiveServicesAdapter(requireContext(), dataList)
        binding.activeRecylerview.adapter = adapter
        binding.activeRecylerview.layoutManager = LinearLayoutManager(requireContext())
    }

}