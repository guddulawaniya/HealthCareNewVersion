package com.asyscraft.alzimer_module

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.adapter.AdapterDate
import com.asyscraft.alzimer_module.adapter.Adaptertime
import com.careavatar.core_model.alzimer.date_response
import com.careavatar.core_model.alzimer.time_response
import com.asyscraft.alzimer_module.databinding.ActivityDoctorDetailsBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class DoctorDetails : BaseActivity() {

    private lateinit var binding: ActivityDoctorDetailsBinding
    private lateinit var adapter: AdapterDate
    private lateinit var adaptertime: Adaptertime

    private var selectedDateFormatted: String? = null
    private var selectedTimeFormatted: String? = null

    private lateinit var DoctorsId:String

    private val viewModel: YourViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        DoctorsId = intent.getStringExtra("DOCTORS_ID").toString()


        if (DoctorsId != null) {
            viewModel.DoctorsDetails(DoctorsId)
            observeResponseDoctorsdetails()

        }

//        binding.descriptionTextView.setTextWithReadMore(
//            "Dr. Arjun Mehta is a licensed psychologist with over 12 years of experience in cognitive therapy, dementia care, and behavioral counseling. He has worked extensively with elderly patients and their caregivers, helping them manage emotional and cognitive challenges effectively through a holistic and personalized approach."
//        )

        binding.toggleButton.setOnClickListener{
            if(binding.descriptionTextView.isExpanded){
                binding.descriptionTextView.collapse()
                binding.toggleButton.text = "See More"
            }else{
                binding.descriptionTextView.expand()
                binding.toggleButton.text = "See Less"
            }
        }

        binding.back.setOnClickListener{
            finish()
        }

        binding.bookBtn.setOnClickListener{

            if (selectedDateFormatted != null && selectedTimeFormatted != null) {

                val requestDate = selectedDateFormatted!!
                val requestTime = selectedTimeFormatted!!
                val patientid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.PATIENT_ID)


                if (patientid != null && DoctorsId != null) {
                    viewModel.Bookappointment(
                        Patientid = patientid,
                        doctorsId = listOf(DoctorsId),
                        appointmentDate = requestDate,
                        appointmentTime = requestTime

//                        Patientid = "6824f1d81105141ac056e5b8",
//                        doctorsId = listOf("682ada307f255395f5a85741"),
//                        appointmentDate = requestDate,
//                        appointmentTime = requestTime


                    )
                }

            }
        }


        setupRecyclerView()
        setupRecyclerViewtime()
        observeResponsebookappointment()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecyclerView() {
//        val datalist = listOf(
//            date_response( 20, "Tue"),
//            date_response( 21, "wed"),
//            date_response( 22, "Thru"),
//            date_response( 23, "Fri"),
//            date_response( 24, "Sat"),
//            date_response( 25, "Sun"),
//        )


        val today = LocalDate.now()
        val datalist = (0..10).map { offset ->
            val date = today.plusDays(offset.toLong())
            date_response(
                Date = date.dayOfMonth,
                Day = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            )
        }


//        val selectedPosition = datalist.indexOfFirst {
//            it.Date == today.dayOfMonth
//        }



//        adapter = AdapterDate(this, datalist) { date ->
//            val selectedDate = today.withDayOfMonth(date.Date) // Reconstruct full LocalDate
////            selectedDateFormatted = selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
//            selectedDateFormatted = selectedDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
//            Toast.makeText(this, "Selected Date: $selectedDateFormatted", Toast.LENGTH_SHORT).show()
//        }



        // Get index of today's date in the list (typically index 0)
        val selectedPosition = 0

        // Set selectedDateFormatted from selectedPosition
        val selectedDateObj = datalist[selectedPosition]
        val selectedDate = today.plusDays(selectedPosition.toLong())
        selectedDateFormatted = selectedDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
        // Optional toast:




        // Setup adapter with selectedPosition
        adapter = AdapterDate(this, datalist, selectedPosition) { date ->
            val selectedDate = today.withDayOfMonth(date.Date)
            selectedDateFormatted = selectedDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
            Toast.makeText(this, "Selected Date: $selectedDateFormatted", Toast.LENGTH_SHORT).show()
        }


        binding.recyclerviewdate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewdate.adapter = adapter
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecyclerViewtime() {
//        val datalist = listOf(
//            time_response( "10:00 AM"),
//            time_response( "11:00 AM"),
//            time_response( "12:00 AM"),
//            time_response( "01:00 AM"),
//            time_response( "02:00 AM")
//        )


        val formatter = DateTimeFormatter.ofPattern("hh:00 a", Locale.ENGLISH)
        val now = LocalTime.now()
        val nextHour = now.plusHours(1).withMinute(0).withSecond(0).withNano(0)

        val datalist = (0..23).map { offset ->
            val time = nextHour.plusHours(offset.toLong())
            time_response(time.format(formatter))
        }

        adaptertime = Adaptertime(this, datalist) { time ->
            val timeInput = time.Time
            val formatterInput = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
            val formatterOutput = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

            val parsedTime = LocalTime.parse(timeInput, formatterInput)
            selectedTimeFormatted = parsedTime.format(formatterOutput).lowercase()

            Toast.makeText(this, "Selected Time: $selectedTimeFormatted", Toast.LENGTH_SHORT).show()
        }

        binding.time.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.time.adapter = adaptertime
    }




//    private fun observeResponseDoctorsdetails() {
//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.Doctorsdetails.observe(this@DoctorDetails) { response ->
//
//                    if(response.success){
//                        binding.dctrName.text = response.doctorObj.name
//                        binding.dctrSpecialization.text = response.doctorObj.specialist
////                        binding.dctrImage.setImageResource(response.doctorObj.image)
//
//                        val imageurl = response.doctorObj.image
//                        Picasso.get()
//                            .load(imageurl)
//                            .placeholder(com.example.healthcarealzimer.R.drawable.doctor_img) // Optional: shows while loading
//                            .error(com.example.healthcarealzimer.R.drawable.doctor_img)       // optional
//                            .into(binding.dctrImage)
//
//                        binding.number.text = response.doctorObj.totalPatients.toString()
//                        binding.years.text = "${response.doctorObj.experience} yr"
//                        binding.ratingNo.text = response.doctorObj.rating.toString()
//                        binding.descriptionTextView.text = response.doctorObj.about.toString()
//
//                    }else{
//
//                    }
//
//                }
//            }
//        }
//    }







    private fun observeResponseDoctorsdetails() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.Doctorsdetails.observe(this@DoctorDetails) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@DoctorDetails)
                            Log.d("DoctorDetails", "🔄 Fetching doctor details...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    val doctor = data.doctorObj
                                    binding.dctrName.text = doctor.name
                                    binding.dctrSpecialization.text = doctor.specialist

                                    Picasso.get()
                                        .load(doctor.image)
                                        .placeholder(R.drawable.doctor_img)
                                        .error(R.drawable.doctor_img)
                                        .into(binding.dctrImage)

                                    binding.number.text = doctor.totalPatients.toString()
                                    binding.years.text = "${doctor.experience} yr"
                                    binding.ratingNo.text = doctor.rating.toString()
                                    binding.descriptionTextView.text = doctor.about ?: ""
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Failed to load doctor details", this@DoctorDetails)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("DoctorDetails", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@DoctorDetails)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected state", this@DoctorDetails)
                        }
                    }
                }
            }
        }
    }







//    private fun observeResponsebookappointment() {
//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.bookappointment.observe(this@DoctorDetails) { response ->
//
//                    if(response.success){
//
//                        finish()
//
//                    }else{
//
//                    }
//
//                }
//            }
//        }
//    }



    private fun observeResponsebookappointment() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookappointment.observe(this@DoctorDetails) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@DoctorDetails)
                            Log.d("BookAppointment", "🔄 Booking appointment...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    Log.d("BookAppointment", "✅ Appointment booked successfully")
                                    androidExtension.alertBox("Appointment booked successfully", this@DoctorDetails)
                                    finish()
                                } else {
                                    androidExtension.alertBox(data.msg ?: "Booking failed", this@DoctorDetails)
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("BookAppointment", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@DoctorDetails)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected state", this@DoctorDetails)
                        }
                    }
                }
            }
        }
    }

}