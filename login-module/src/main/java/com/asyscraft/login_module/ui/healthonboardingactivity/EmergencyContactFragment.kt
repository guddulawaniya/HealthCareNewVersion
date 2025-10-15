package com.asyscraft.login_module.ui.healthonboardingactivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.careavatar.core_service.repository.adapters.ContactsAdapter
import com.asyscraft.login_module.databinding.FragmentEmergencycontactBinding
import com.careavatar.core_service.repository.viewModels.ContactViewModel
import com.careavatar.core_ui.R
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_service.repository.viewModels.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EmergencyContactFragment : BaseFragment() {
    private lateinit var binding: FragmentEmergencycontactBinding
    private lateinit var viewModel: ContactViewModel
    private lateinit var contactAdapter: ContactsAdapter
    private val viewModeldata: OnboardingViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmergencycontactBinding.inflate(inflater, container, false)
        viewModel = ContactViewModel(application = requireActivity().application)

        val activity = requireActivity() as? InformationOnboardingActivity
        activity?.updateStep(10)

        binding.btninclude.buttonNext.text = getString(R.string.get_started)

        binding.btninclude.buttonNext.setOnClickListener {

            val selectedContacts = contactAdapter.snapshot().items.filter { it.isSelected }

            if (selectedContacts.isNotEmpty()) {
                val emergencyContactsdata = selectedContacts.joinToString(", ") { it.number }
                viewModeldata.updateEmergencyContacts(emergencyContactsdata)

                startActivity(Intent(requireContext(), Information_loading_Activity::class.java))


            } else {
                showToast("Select at least one contact")
            }
        }

        setupRecyclerView()

        if (hasReadContactsPermission()) {
            loadContacts()
        } else {
            binding.linearLayout.visibility = View.GONE
            binding.permissionbutton.visibility = View.VISIBLE
        }

        binding.permissionbutton.setOnClickListener {
            checkContactsPermission()
        }

        binding.customSearchbar.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                loadContacts(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        return binding.root
    }




    private fun hasReadContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun loadContacts(query: String = "") {
        binding.linearLayout.visibility = View.VISIBLE
        binding.permissionbutton.visibility = View.GONE

        lifecycleScope.launch {
            viewModel.getContacts(query, emptyList()).collectLatest { pagingData ->
                contactAdapter.submitData(pagingData)
            }
        }
    }

    private fun checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadContacts()
        } else {
            requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
        }
    }


    private fun setupRecyclerView() {
        contactAdapter = ContactsAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.emergencyContactRecylerview.apply {
            adapter = contactAdapter
            this.layoutManager = layoutManager
        }
    }


    private val requestContactsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadContacts()
            } else {
                showToast("Permission Denied!")
            }
        }


}