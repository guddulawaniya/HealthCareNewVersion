package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityFilterBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Filter : BaseActivity() {

    private val viewModel: YourViewModel by viewModels()
    var queryParam: String? = null

    private lateinit var patientid: String

    private lateinit var binding: ActivityFilterBinding
    var clickCountWhole = 0
    var clickCountWhole2 = 0
    var clickCountWhole3 = 0
    var clickCountWhole4 = 0
    var clickCountWhole5 = 0
    var clickCountWhole6 = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.applyChange.visibility = View.GONE

        patientid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PATIENT_ID).toString()

        // Set click listeners for each view
        binding.whole.setOnClickListener {
            clickCountWhole++
            updateViewState(binding.whole, binding.image, binding.name, clickCountWhole, R.drawable.psychiatrist_fill, R.drawable.psychiatrist)
            checkApplyChangeVisibility()
        }

        binding.whole2.setOnClickListener {
            clickCountWhole2++
            updateViewState(binding.whole2, binding.image2, binding.name2, clickCountWhole2, R.drawable.geriatrician_fill, R.drawable.geriatrician)
            checkApplyChangeVisibility()
        }

        binding.whole3.setOnClickListener {
            clickCountWhole3++
            updateViewState(binding.whole3, binding.image3, binding.name3, clickCountWhole3, R.drawable.dietician_fill, R.drawable.dietician)
            checkApplyChangeVisibility()
        }

        binding.whole4.setOnClickListener {
            clickCountWhole4++
            updateViewState(binding.whole4, binding.image4, binding.name4, clickCountWhole4, R.drawable.yoga_therapist_fill, R.drawable.yoga_therapist)
            checkApplyChangeVisibility()
        }

        binding.whole5.setOnClickListener {
            clickCountWhole5++
            updateViewState(binding.whole5, binding.image5, binding.name5, clickCountWhole5, R.drawable.neurologist_fill, R.drawable.neurologist)
            checkApplyChangeVisibility()
        }

        binding.whole6.setOnClickListener {
            clickCountWhole6++
            updateViewState(binding.whole6, binding.image6, binding.name6, clickCountWhole6, R.drawable.neuropsychologist_fill, R.drawable.neuropsychologist)
            checkApplyChangeVisibility()
        }


        binding.back.setOnClickListener{
            finish()
        }



        binding.applyChange.setOnClickListener{
            val selectedSpecialists = mutableListOf<String>()

            if (clickCountWhole % 2 != 0) {
                selectedSpecialists.add(binding.name.text.toString())
            }
            if (clickCountWhole2 % 2 != 0) {
                selectedSpecialists.add(binding.name2.text.toString())
            }
            if (clickCountWhole3 % 2 != 0) {
                selectedSpecialists.add(binding.name3.text.toString())
            }
            if (clickCountWhole4 % 2 != 0) {
                selectedSpecialists.add(binding.name4.text.toString())
            }
            if (clickCountWhole5 % 2 != 0) {
                selectedSpecialists.add(binding.name5.text.toString())
            }
            if (clickCountWhole6 % 2 != 0) {
                selectedSpecialists.add(binding.name6.text.toString())
            }

            // Join the selected specialists by comma
            queryParam = selectedSpecialists.joinToString(",")
            viewModel.GetallDoctors(specialist = queryParam, patientId = patientid)
        }


        observeResponsegetallDoctors()
    }


    // Function to update the view based on whether it's selected or not
    private fun updateViewState(view: View, imageView: ImageView, textView: TextView, clickCount: Int, selectedImage: Int, unselectedImage: Int) {
        if (clickCount % 2 != 0) {
            view.setBackgroundResource(R.drawable.background_purple_filter)
            imageView.setImageResource(selectedImage)
            textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            view.setBackgroundResource(R.drawable.background_white)
            imageView.setImageResource(unselectedImage)
            textView.setTextColor(ContextCompat.getColor(this, R.color.A524E2))
        }
    }


    private fun checkApplyChangeVisibility() {
        // If any of the click counts is odd (meaning the item is selected), show the applyChange button
        if (clickCountWhole % 2 != 0 || clickCountWhole2 % 2 != 0 || clickCountWhole3 % 2 != 0 ||
            clickCountWhole4 % 2 != 0 || clickCountWhole5 % 2 != 0 || clickCountWhole6 % 2 != 0) {
            binding.applyChange.visibility = View.VISIBLE


        } else {
            binding.applyChange.visibility = View.GONE
        }
    }




//    private fun observeResponsegetallDoctors() {
//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.GetallDoctor.observe(this@Filter) { response ->
//
//                    if(response.success){
////                        datalist.clear()
////                        datalist.addAll(response.data)
////                        adapter.notifyDataSetChanged()
//
//                        val resultIntent = Intent()
//                        resultIntent.putExtra("FILTER_APPLIED", queryParam) // or send any data
//                        setResult(RESULT_OK, resultIntent)
//                        finish()
//
//                        // Update visibility based on data
////                        if (datalist.isEmpty()) {
////                            binding.recyclerview.visibility = View.GONE
////                            binding.img.visibility = View.VISIBLE
////                        } else {
////                            binding.recyclerview.visibility = View.VISIBLE
////                            binding.img.visibility = View.GONE
////                        }
//
//                    }else{
//
//                    }
//
//                }
//            }
//        }
//    }


    private fun observeResponsegetallDoctors() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetallDoctor.observe(this@Filter) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Filter)
                            Log.d("DoctorsFilter", "🔄 Loading filtered doctor list...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    val resultIntent = Intent().apply {
                                        putExtra("FILTER_APPLIED", queryParam) // you can customize this
                                    }
                                    setResult(RESULT_OK, resultIntent)
                                    finish()

                                    Log.d("DoctorsFilter", "✅ Filter applied successfully.")
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Filter failed", this@Filter)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("DoctorsFilter", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Filter)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@Filter)
                        }
                    }
                }
            }
        }
    }



}