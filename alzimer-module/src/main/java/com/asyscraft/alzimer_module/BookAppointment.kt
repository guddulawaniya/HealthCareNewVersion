package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.adapter.AdapterBookappointmentAdapterGame
import com.careavatar.core_model.alzimer.Doctor
import com.asyscraft.alzimer_module.databinding.ActivityBookAppointmentBinding
import com.asyscraft.alzimer_module.utils.GridSpacing
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookAppointment : BaseActivity() {
    private lateinit var binding: ActivityBookAppointmentBinding

    private lateinit var adapter: AdapterBookappointmentAdapterGame
    private var datalist: ArrayList<Doctor> = ArrayList()

    private val viewModel: YourViewModel by viewModels()

    private  var filterQuery: String?= null
    private  var query: String? = null

    private lateinit var patientid: String



    private val filterLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            filterQuery = data?.getStringExtra("FILTER_APPLIED")

            if (!filterQuery.isNullOrEmpty()) {
                // Call your ViewModel function with the filtered query
                val patientid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PATIENT_ID).toString()
                viewModel.GetallDoctors(specialist = filterQuery, patientId = patientid)

                Log.d("patientid upper ", patientid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PATIENT_ID).toString()

        Log.d("patineeeet", patientid)

        binding.back.setOnClickListener{
            finish()
        }



        binding.filter.setOnClickListener{
            val intent = Intent(this, Filter::class.java)
            filterLauncher.launch(intent)
        }

        setupRecyclerView()


        binding.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Optional: debounce logic here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                query = s.toString().trim()
                if (query!!.isNotEmpty()) {
                    viewModel.GetallDoctors(name = query, patientId = patientid) // Call your API here
                }
            }
        })


        viewModel.GetallDoctors(patientId = patientid)
        observeResponsegetallDoctors()

    }

    override fun onResume() {
        super.onResume()

        if(filterQuery.isNullOrEmpty() && query.isNullOrEmpty()){
            viewModel.GetallDoctors(patientId = patientid)
        }
    }




    private fun setupRecyclerView() {

        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.recyclerview.layoutManager = gridLayoutManager


        val spacingInPixels = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
        binding.recyclerview.addItemDecoration(GridSpacing(2, spacingInPixels, true))
        adapter = AdapterBookappointmentAdapterGame(this, datalist) { doctor ->
            Toast.makeText(this, "Clicked on: ${doctor.name}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DoctorDetails::class.java)
            intent.putExtra("DOCTORS_ID", doctor._id)
            startActivity(intent)
        }
        binding.recyclerview.adapter = adapter

    }



    private fun observeResponsegetallDoctors() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetallDoctor.observe(this@BookAppointment) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@BookAppointment)
                            Log.d("DoctorsList", "🔄 Loading doctors...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    datalist.clear()
                                    datalist.addAll(data.data)
                                    adapter.notifyDataSetChanged()

                                    // Optional: show/hide views based on data presence
//                                    if (datalist.isEmpty()) {
//                                        binding.recyclerview.visibility = View.GONE
//                                        binding.img.visibility = View.VISIBLE
//                                    } else {
//                                        binding.recyclerview.visibility = View.VISIBLE
//                                        binding.img.visibility = View.GONE
//                                    }

                                    Log.d("DoctorsList", "✅ Loaded ${data.data.size} doctors.")
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Failed to load doctors", this@BookAppointment)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("DoctorsList", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@BookAppointment)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@BookAppointment)
                        }
                    }
                }
            }
        }
    }

}