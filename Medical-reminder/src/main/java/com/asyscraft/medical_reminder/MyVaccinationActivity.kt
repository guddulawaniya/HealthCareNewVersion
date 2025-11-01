package com.asyscraft.medical_reminder

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.medical_reminder.adapters.VaccinationAdapter
import com.asyscraft.medical_reminder.databinding.ActivityMyVaccinationBinding
import com.asyscraft.medical_reminder.viewModels.MedicalViewModel
import com.careavatar.core_model.medicalReminder.VaccinationDetailsResponse
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyVaccinationActivity : BaseActivity() {
    private lateinit var binding: ActivityMyVaccinationBinding
    private val viewModel: MedicalViewModel by viewModels()
    private lateinit var adapter: VaccinationAdapter
    private var dataList = mutableListOf<VaccinationDetailsResponse.Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyVaccinationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addVaccination.setOnClickListener {
            startActivity(Intent(this, AddVaaccinationActivity::class.java))
        }
        binding.backButton.setOnClickListener { finish() }


        setUpRecyclerview()
        observer()
    }

    override fun onResume() {
        super.onResume()
        hitGetVaccinationList()
    }
    private fun setUpRecyclerview(){
        adapter = VaccinationAdapter(dataList)
        binding.rvVaccination.adapter = adapter
        binding.rvVaccination.layoutManager = LinearLayoutManager(this)

    }
    private fun observer(){
        collectApiResultOnStarted(viewModel.getVaccinationDetailsResponse){
            if (it.success){
                dataList.clear()
                dataList.addAll(it.data)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun hitGetVaccinationList(){
        lifecycleScope.launch {
            val userId = userPref.userId.first()
            launchIfInternetAvailable {
                viewModel.hitGetVaccinationList(userId.toString())
            }
        }


    }
}