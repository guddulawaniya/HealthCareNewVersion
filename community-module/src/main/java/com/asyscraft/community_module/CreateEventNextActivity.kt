package com.asyscraft.community_module

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.asyscraft.community_module.databinding.ActivityCreateEventNextBinding
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.AddressPickerActivity
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable
import com.careavatar.core_utils.DateTimePickerUtil.pickDate
import com.careavatar.core_utils.DateTimePickerUtil.pickDateTime
import com.careavatar.core_utils.DateTimePickerUtil.pickTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEventNextActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateEventNextBinding
    private lateinit var addressPickerLauncher: ActivityResultLauncher<Intent>
    private var latitude = 0.0
    private var longitude = 0.0
    private var eventMode : Boolean = true
    private var selecteddate : String?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventNextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btninclude.buttonNext.text = "Next"

        switchTabLayout()

        binding.datePickTextView.setOnClickListener {
            pickDate(this,onDatePicked={
                selecteddate = it
                binding.datePickTextView.text = formatDateToReadable(it)

            })
        }

        binding.locationField.setOnClickListener {
            val intent = Intent(this, AddressPickerActivity::class.java)
            addressPickerLauncher.launch(intent)
        }

        binding.timePickTextView.setOnClickListener {

            pickTime(this,onTimePicked={
                binding.timePickTextView.text = it

            })

        }

        val durationOptions = listOf("15 min", "30 min", "45 min", "60 min")
        val durationAdapter = ArrayAdapter(
            this, // or requireContext() if in Fragment
            com.careavatar.core_ui.R.layout.dropdown_item, // default dropdown layout
            durationOptions
        )


        val eventTimeDuration = binding.eventTimeDuration
        eventTimeDuration.setAdapter(durationAdapter)

        eventTimeDuration.setOnClickListener {
            eventTimeDuration.requestFocus()
            eventTimeDuration.showDropDown()
        }

        eventTimeDuration.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                eventTimeDuration.showDropDown()
            }
        }

        eventTimeDuration.setOnItemClickListener { parent, view, position, id ->
            val selectedDuration = durationOptions[position]

        }

        binding.btninclude.buttonNext.setOnClickListener {

            if (validateEventInputs()) {

                val communityId = intent.getStringExtra("communityId")
                val title = intent.getStringExtra("title")
                val description = intent.getStringExtra("description")
                val mainImage = intent.getStringExtra("mainImage")
                val selectedImages = intent.getStringArrayListExtra("selectedImages")

                Log.d("latitude",latitude.toString())
                Log.d("longitude",longitude.toString())
                Log.d("address",binding.locationField.text.toString())

                Log.d("mainimage",mainImage.toString())

                startActivity(
                    Intent(this@CreateEventNextActivity, EventCreatePreviewActivity::class.java)
                        .putExtra("eventmode", eventMode)
                        .putExtra("eventdate", binding.datePickTextView.text.toString().trim())
                        .putExtra("selectedEventdate", selecteddate)
                        .putExtra("location", binding.locationField.text.toString().trim())
                        .putExtra("meetingLinkField", binding.meetingLinkField.text.toString().trim())
                        .putExtra("timePickTextView", binding.timePickTextView.text.toString().trim())
                        .putExtra("eventTimeDuration", binding.eventTimeDuration.text.toString().trim())
                        .putExtra("communityId", communityId)
                        .putExtra("title", title)
                        .putExtra("description", description)
                        .putExtra("latitude", latitude)
                        .putExtra("longitude", longitude)
                        .putExtra("mainImage", mainImage)
                        .putStringArrayListExtra("selectedImages", ArrayList(selectedImages))
                )
            }
        }

        getLocation()

    }

    private fun validateEventInputs(): Boolean {
        val eventDate = binding.datePickTextView.text.toString().trim()
        val location = binding.locationField.text.toString().trim()
        val meetingLink = binding.meetingLinkField.text.toString().trim()
        val time = binding.timePickTextView.text.toString().trim()
        val duration = binding.eventTimeDuration.text.toString().trim()

        return when {
            eventDate.isEmpty() -> {
                showInputError(binding.datePickTextView,"Please select an event date")
                false
            }
            !eventMode && location.isEmpty() -> {
                showInputError(binding.locationField,"Please enter a location")
                false
            }
            eventMode && meetingLink.isEmpty() -> {
                showInputError(binding.meetingLinkField,"Please enter the meeting link")
                false
            }
            time.isEmpty() -> {
                showInputError(binding.timePickTextView,"Please select a start time")

                false
            }
            duration.isEmpty() -> {
                showInputError(binding.eventTimeDuration,"Please enter the event duration")
                false
            }
            else -> true
        }
    }


    private fun getLocation(){
        addressPickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                val selectedaddress = data?.getStringExtra("selected_address")
                val selected_latitude = data?.getDoubleExtra("selected_latitude", 0.0)
                val selected_longitude = data?.getDoubleExtra("selected_longitude", 0.0)

                Log.d("latitude",selected_latitude.toString())
                Log.d("longitude",selected_longitude.toString())
                Log.d("address",selectedaddress.toString())



                latitude = selected_latitude ?: 0.0
                longitude = selected_longitude ?: 0.0

                binding.locationField.text = selectedaddress

            }
        }
    }

    private fun switchTabLayout(){

        binding.onlineText.setOnClickListener {
            eventMode = true
            binding.onlineText.setBackgroundResource(com.careavatar.core_ui.R.drawable.community_online_selected_bg)
            binding.onlineText.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.primaryColor))

            binding.offlineText.setBackgroundResource(com.careavatar.core_ui.R.drawable.edit_text_bg)
            binding.offlineText.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.textColor))

            binding.meetingLinkField.visibility = View.VISIBLE
            binding.meetingLinkFieldtextview.visibility = View.VISIBLE
            binding.locationField.visibility = View.GONE
            binding.locationFieldTextview.visibility = View.GONE
        }

        binding.offlineText.setOnClickListener {
            eventMode = false
            binding.offlineText.setBackgroundResource(com.careavatar.core_ui.R.drawable.community_online_selected_bg)
            binding.offlineText.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.primaryColor))

            binding.onlineText.setBackgroundResource(com.careavatar.core_ui.R.drawable.edit_text_bg)
            binding.onlineText.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.textColor))

            binding.meetingLinkField.visibility = View.GONE
            binding.meetingLinkFieldtextview.visibility = View.GONE
            binding.locationField.visibility = View.VISIBLE
            binding.locationFieldTextview.visibility = View.VISIBLE
        }

    }
}