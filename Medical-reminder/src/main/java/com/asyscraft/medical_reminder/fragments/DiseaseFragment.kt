package com.asyscraft.medical_reminder.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.medical_reminder.R
import com.asyscraft.medical_reminder.adapters.DiseaseAdapter
import com.asyscraft.medical_reminder.databinding.FragmentDiseaseBinding
import com.asyscraft.medical_reminder.utils.SaveDataViewModel
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_model.medicalReminder.Disease
import com.careavatar.core_network.base.BaseFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiseaseFragment : BaseFragment() {

    private lateinit var binding: FragmentDiseaseBinding
    private val dataList = mutableListOf<Disease>()
    private var selectedDisease: Disease? = null

    private val viewModel: MedicalViewModel by viewModels()
    private val saveDataViewModel: SaveDataViewModel by viewModels()

    private val adapter: DiseaseAdapter by lazy {
        DiseaseAdapter(dataList) { disease ->
            onDiseaseSelected(disease)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiseaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNext.isEnabled = false
        binding.btnNext.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

        setupRecyclerView()
        setupSearchBar()
        setupNextButton()
        fetchDiseases()
        observeDisease()
    }

    /** ✅ RecyclerView setup */
    private fun setupRecyclerView() = with(binding.diseaseRecyclerview) {
        adapter = this@DiseaseFragment.adapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSearchBar() {
        binding.includeSearchBar.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.chipGroup.visibility = View.GONE
                binding.diseaseRecyclerview.visibility = View.VISIBLE
            }
        }

        binding.includeSearchBar.etSearch.addTextChangedListener { text ->
            val query = text?.toString()?.trim()?.lowercase() ?: ""
            println("Search query: '$query'")
            println("Original dataList size: ${dataList.size}")

            val filtered = if (query.isEmpty()) {
                dataList

            } else {
                dataList.filter { it.diseaseName.contains(query, ignoreCase = true) }
            }
            adapter.updateList(filtered.toMutableList())

            println("Filtered list size: ${filtered.size}")
            adapter.updateSearchQuery(query)
        }
    }


    /** ✅ Next button logic */
    private fun setupNextButton() {
        binding.btnNext.setOnClickListener {
            selectedDisease?.let {
                saveDataViewModel.updateDiseaseId(it._id)
                findNavController().navigate(R.id.action_diseaseFragment_to_uploadPrescriptionFragment)

            } ?: showToast("Please select a disease")
        }
    }

    /** ✅ Fetch data from API */
    private fun fetchDiseases() {
        launchIfInternetAvailable {
            viewModel.hitGetDisease()
        }
    }

    /** ✅ Observe response */
    private fun observeDisease() {
        collectApiResultOnStarted(viewModel.getDiseaseResponse) { response ->
            if (response.success) {
                dataList.clear()
                dataList.addAll(response.diseases)
                adapter.notifyDataSetChanged()

                setupTopDiseaseChips(response.diseases.take(5))
            }
        }
    }

    /** ✅ Top 5 chips */
    private fun setupTopDiseaseChips(topDiseases: List<Disease>) {
        val chipGroup = binding.chipGroup
        chipGroup.removeAllViews()

        val primaryColor = requireContext().getColor(com.careavatar.core_ui.R.color.primaryColor)
        val whiteColor = Color.WHITE
        val blackColor = Color.BLACK

        topDiseases.forEach { disease ->
            val chip = Chip(requireContext()).apply {
                text = disease.diseaseName
                isCheckable = true
                isClickable = true

                chipBackgroundColor = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked)
                    ),
                    intArrayOf(primaryColor, whiteColor)
                )

                setTextColor(
                    ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_checked),
                            intArrayOf(-android.R.attr.state_checked)
                        ),
                        intArrayOf(whiteColor, blackColor)
                    )
                )

                chipStrokeWidth = 1f
                chipStrokeColor = ColorStateList.valueOf("#33000000".toColorInt())
                chipCornerRadius = 20f
                textSize = 14f
                setPadding(16, 8, 16, 8)

                setOnClickListener {
                    chipGroup.clearCheck()
                    binding.btnNext.isEnabled = true
                    isChecked = true
                    onDiseaseSelected(disease)
                }
            }

            chipGroup.addView(chip)
        }
    }

    /** ✅ Update selected disease */
    private fun onDiseaseSelected(disease: Disease) {
        selectedDisease = disease
        binding.btnNext.isEnabled = true
        binding.btnNext.backgroundTintList = ColorStateList.valueOf("#4CAF50".toColorInt())
    }
}
