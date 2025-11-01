package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityDateSelectorBinding
import com.asyscraft.alzimer_module.singltondata.AnswerCollector
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SoundEffectManager
import com.asyscraft.alzimer_module.utils.Timer
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class DateSelector : BaseActivity() {

    private lateinit var binding: ActivityDateSelectorBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private var selectedScore = 0
    private var isDateEvaluated = false
    private lateinit var  type : String

    //    sound
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0
//    var isSoundOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDateSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type").toString()


        binding.cross.setOnClickListener{
            finish()
        }
        binding.next.isEnabled = false

        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 5 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }

        //     sound
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        // Load the sound
        soundId2 = soundPool.load(this, R.raw.match_complete, 1)

//        binding.music.setOnClickListener{
//            isSoundOn = !isSoundOn
//
//            if (isSoundOn) {
//                binding.music.setImageResource(R.drawable.imageon) // Replace with your "sound on" drawable
//            } else {
//                binding.music.setImageResource(R.drawable.imageoff) // Replace with your "sound off" drawable
//            }
//        }



        SoundEffectManager.loadState(this)
        updateSoundIcon()

        binding.music.setOnClickListener {
            SoundEffectManager.toggleSound(this)
            updateSoundIcon()
        }


        binding.next.setOnClickListener{
//            val intent = Intent(this, Place::class.java)
//            startActivity(intent)
//            val isCorrect = selectedScore == 1

//            val answersList = listOf(
//                Answer("680631fac1980def07ed8fcc", isCorrect, selectedScore, "2025-04-24T12:00:00.000Z")
//            )
//
//            viewModel.submitAnswers(answersList)

            if(selectedScore ==2 ){

                val answer = AnswerRequest(
                    question = "6814a426dd60aa3d34240423",
                    isCorrect = true,
                    points = 2,
                    isSkipped = false
                )

                AnswerCollector.addAnswer(answer)

                if(SoundEffectManager.isSoundOn){
                    soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                }

                val intent = Intent(this, Place::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
                Timer.cancel()
                finish()
            }else if(selectedScore ==1 ){

            val answer = AnswerRequest(
                question = "6814a426dd60aa3d34240423",
                isCorrect = true,
                points = 1,
                isSkipped = false
            )

            AnswerCollector.addAnswer(answer)

            if(SoundEffectManager.isSoundOn){
                soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
            }

            val intent = Intent(this, Place::class.java)
            intent.putExtra("type",type)
            startActivity(intent)
            Timer.cancel()
            finish()
        } else{

                val answer = AnswerRequest(
                    question = "6814a426dd60aa3d34240423",
                    isCorrect = false,
                    points = 0,
                    isSkipped = false
                )

                AnswerCollector.addAnswer(answer)


                val intent = Intent(this, Place::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
                Timer.cancel()
                finish()
            }

        }

        //Timer
        Timer = Timer(context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {
//                    val intent = Intent(this, Place::class.java)
//                    startActivity(intent)
//                    finish() // Optional: remove this activity from back stack


                    val answer = AnswerRequest(
                        question = "6814a426dd60aa3d34240423",
                        isCorrect = false,
                        points = selectedScore,
                        isSkipped = true
                    )

                    AnswerCollector.addAnswer(answer)


                    val intent = Intent(this, Place::class.java)
                    intent.putExtra("type",type)
                    startActivity(intent)
                    Timer.cancel()
                    finish()

                }
            }) // 1 min)
        Timer.start()

        val date = mutableListOf("Date") + (1..31).map { it.toString() }
        val months = mutableListOf("Month") + listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val years = mutableListOf("Year") + (1950..2100).map { it.toString() }
        val days = mutableListOf("Days") + listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

        fun <T> setupSpinner(spinner: Spinner, data: List<T>) {
            val adapter = ArrayAdapter(this, R.layout.spinner_layouts, R.id.spinner_item_text, data)
            adapter.setDropDownViewResource(R.layout.spinner_layouts)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    updateSelectedDate()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        setupSpinner(binding.spinnerDate, date)
        setupSpinner(binding.spinnerMonth, months)
        setupSpinner(binding.spinnerYear, years)
        setupSpinner(binding.spinnerDays, days)



        viewModel.loadQuestionData()
//        viewModel.dataQuestion.observe(this) { response ->
//
//            if (response.success) {
//                Log.d("API_RESPONSE", "✅ Success: ${response.msg}")
//
//                val question = response.data.find { it._id == "6814a426dd60aa3d34240423"}
//
//                question?.let { q ->
//                    // Get the list of images
//
//                    binding.instruction.text = q.instruction.toString()
//                    binding.question.text = q.question.toString()
//                    val imageList = q.image.toList()
//                    Log.d("Questionsss","${q.question}")
//                    Log.d("imagesss circle","${q.image}")
//                    val baseUrl = "http://172.104.206.4:5000/uploads/"
//
//
//                    // Ensure you have 4 images to display
//                    if (imageList!= null) {
//                        // Duplicate the list to get 3 images
//
//                        // Load shuffled images into ImageViews
//                        Picasso.get()
//                            .load(baseUrl+imageList[0].second)
//                            .into(binding.image)
//
//
//                    } else {
//                        Log.e("API_RESPONSE", "Not enough images")
//                    }
//                }
//
//
//            } else {
//                Log.e("API_RESPONSE", "❌ Failed: ${response.msg}")
//            }
//        }


//        viewModel.submitAnswer.observe(this) { response ->
//            if (response.success) {
//
//                val intent = Intent(this, Place::class.java)
//                startActivity(intent)
//                finish()
//                Log.d("DateSelector", response.msg)
//                // Navigate user to next screen or show success
//            } else {
//                Log.e("DateSelector", response.msg)
//                // Show error to user
//            }
//        }


        observeDataQuestion()
    }
    // Function to update the displayed selected date
    private fun updateSelectedDate() {
        val selectedDay = binding.spinnerDate.selectedItem.toString()
        val selectedMonth = binding.spinnerMonth.selectedItem.toString()
        val selectedYear = binding.spinnerYear.selectedItem.toString()
        val selectedDayName = binding.spinnerDays.selectedItem.toString()

        if (selectedDay == "Date" || selectedMonth == "Month" || selectedYear == "Year" || selectedDayName == "Days") {
            // Don't do anything if not fully selected
            binding.next.isEnabled = false
            isDateEvaluated = false
            return
        }

        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH).toString()
        val currentMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
        val currentYear = calendar.get(Calendar.YEAR).toString()
        val currentDayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)


        if (selectedDay == currentDay &&
            selectedMonth == currentMonth &&
            selectedYear == currentYear &&
            selectedDayName == currentDayName) {
            selectedScore = 2
        }else if(selectedDay == currentDay &&
            selectedMonth == currentMonth &&
            selectedYear == currentYear){
            selectedScore = 1
        } else {
            selectedScore = 0
            Log.d("DateCheck", "❌ Incorrect! Dates do not match.")
            // Toast.makeText(this, "Wrong Date. Try Again!", Toast.LENGTH_SHORT).show()
        }
        isDateEvaluated = true
        binding.next.isEnabled = true
        binding.next.setBackgroundResource(R.drawable.save_button_background)
    }


    private fun updateSoundIcon() {
        if (SoundEffectManager.isSoundOn) {
            binding.music.setImageResource(R.drawable.imageon)
        } else {
            binding.music.setImageResource(R.drawable.imageoff)
        }
    }



    private fun observeDataQuestion() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataQuestion.observe(this@DateSelector) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question = questionResponse.data.find { it._id == "6814a426dd60aa3d34240423" }

                                    question?.let { q ->
                                        binding.instruction.text = q.instruction.toString()
                                        binding.question.text = q.question.toString()

                                        Log.d("Questionsss", q.question.toString())
                                        Log.d("imagesss circle", q.image.toString())

                                        val imageList = q.image.toList()
                                        val baseUrl = "http://172.104.206.4:5000/uploads/"

                                        if (imageList.isNotEmpty()) {
                                            // Load the first image into the ImageView
                                            Picasso.get()
                                                .load(baseUrl + imageList[0].second)
                                                .into(binding.image)
                                        } else {
                                            androidExtension.alertBox("No images found", this@DateSelector)
                                            Log.e("API_RESPONSE", "❌ No images available in question")
                                        }
                                    } ?: androidExtension.alertBox("Question not found", this@DateSelector)
                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Question fetch failed", this@DateSelector)
                                }
                            } ?: androidExtension.alertBox("Unexpected empty response", this@DateSelector)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@DateSelector)
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@DateSelector)
                            Log.d("API_RESPONSE", "🔄 Loading question data...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state: $response", this@DateSelector)
                        }
                    }
                }
            }
        }
    }

}