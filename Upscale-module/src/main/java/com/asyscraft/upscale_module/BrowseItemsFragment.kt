package com.asyscraft.upscale_module

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.upscale_module.adapters.BrowersitemAdapter
import com.asyscraft.upscale_module.databinding.FragmentBrowseItemsBinding
import com.careavatar.core_model.BrowersitemModel
import com.careavatar.core_network.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BrowseItemsFragment : BaseFragment() {


    private lateinit var binding : FragmentBrowseItemsBinding
    private lateinit var adapter : BrowersitemAdapter
    private val datalist = mutableListOf<BrowersitemModel>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBrowseItemsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerview()
        setUpSearch()
    }

    private fun setUpSearch() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            adapter.filter.filter(text.toString())
        }
    }


    private fun setUpRecyclerview(){

        datalist.add(BrowersitemModel("Hostpital Bed","Fully electric, adjustable","For Donation",com.careavatar.core_ui.R.drawable.hostpital_bed_image))
        datalist.add(BrowersitemModel("Wheelchair","Fully electric, adjustable","For Sale: \$25",com.careavatar.core_ui.R.drawable.hostpital_bed_image))
        datalist.add(BrowersitemModel("Hostpital Bed","Fully electric, adjustable","For Donation",com.careavatar.core_ui.R.drawable.hostpital_bed_image))
        datalist.add(BrowersitemModel("Wheelchair","Fully electric, adjustable","For Sale: \$25",com.careavatar.core_ui.R.drawable.hostpital_bed_image))
        datalist.add(BrowersitemModel("Hostpital Bed","Fully electric, adjustable","For Donation",com.careavatar.core_ui.R.drawable.hostpital_bed_image))
        datalist.add(BrowersitemModel("Wheelchair","Fully electric, adjustable","For Sale: \$25",com.careavatar.core_ui.R.drawable.hostpital_bed_image))
        datalist.add(BrowersitemModel("Hostpital Bed","Fully electric, adjustable","For Donation",com.careavatar.core_ui.R.drawable.hostpital_bed_image))
        datalist.add(BrowersitemModel("Wheelchair","Fully electric, adjustable","For Sale: \$25",com.careavatar.core_ui.R.drawable.hostpital_bed_image))



        adapter = BrowersitemAdapter(requireContext(),datalist, onClickListener = {
            startActivity(Intent(requireContext(), UpscaledetailActivity::class.java))

        })
        binding.browerRecylerview.adapter =adapter
        binding.browerRecylerview.layoutManager = LinearLayoutManager(requireContext())
    }

}