package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.alzimer_module.adapter.AdapterPatientList
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.Patientlist
import com.asyscraft.alzimer_module.databinding.ActivityPatientListBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PatientList : BaseActivity(), AdapterPatientList.OnPatientClickListener {

    private lateinit var binding: ActivityPatientListBinding
    private lateinit var adapterPatientList: AdapterPatientList
    private var datalistpatient: ArrayList<Patientlist> = ArrayList()

    private val viewModel: YourViewModel by viewModels()

    var origin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        origin = intent.getStringExtra("type").toString()

        binding.addpatient.setOnClickListener {
            val intent = Intent(this, ParticipantInformation::class.java)
            intent.putExtra("type",origin)
            startActivity(intent)
        }

        viewModel.GetPatientList()

        observeResponsepatientlist()
        setupRecyclerViewpatientlist()
    }

    private fun setupRecyclerViewpatientlist() {

        adapterPatientList = AdapterPatientList(this, datalistpatient, this)

        binding.patientlist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.patientlist.adapter = adapterPatientList
    }

    override fun onPatientClick(position: Int, patient: Patientlist) {
        Toast.makeText(this, "Clicked: ${patient.fullName}", Toast.LENGTH_SHORT).show()

        val patientid = SavedPrefManager.saveStringPreferences(this, SavedPrefManager.PATIENT_ID, patient._id)
        SavedPrefManager.saveStringPreferences(this, SavedPrefManager.USERID, patient.user)

        Log.d("patientidsave", patientid)
        Log.d("patientidapi response", patient._id)

        if(patient.isAssessmentComplete){
            val intent = Intent(this, HomePage::class.java)
            intent.putExtra("isassesment", true)
            intent.putExtra("type",origin)
            intent.putExtra("patientimage", patient.image)
            startActivity(intent)
        }else{
            val intent = Intent(this, BrainHealthCheck::class.java)
            intent.putExtra("type",origin)
            startActivity(intent)
        }

    }



    private fun observeResponsepatientlist() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getpatientlist.observe(this@PatientList) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@PatientList)
                            Log.d("PatientList", "⏳ Loading patient list...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val patients = response.data?.patients
                            if (!patients.isNullOrEmpty()) {
                                datalistpatient.clear()
                                datalistpatient.addAll(patients)
                                adapterPatientList.notifyDataSetChanged()
                            } else {
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("PatientList", "❌ Error: ${response.message}")
                        }

                        else -> {
                            Progresss.stop()
                        }
                    }
                }

            }
        }
    }

}
