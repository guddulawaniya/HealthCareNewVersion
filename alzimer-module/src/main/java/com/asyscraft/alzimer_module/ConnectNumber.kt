package com.asyscraft.alzimer_module

import com.asyscraft.alzimer_module.utils.DottedLineView
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityConnectNumberBinding
import com.asyscraft.alzimer_module.singltondata.AnswerCollector
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SoundEffectManager
import com.asyscraft.alzimer_module.utils.Timer
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConnectNumber : BaseActivity() {

    private lateinit var binding: ActivityConnectNumberBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private lateinit var dottedLineView: DottedLineView
    private lateinit var  type : String

    private val selectedValues = mutableListOf<String>()

    //    sound
    lateinit var soundPool: SoundPool
    var soundId: Int = 0
    var soundId2: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConnectNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type").toString()

        binding.cross.setOnClickListener{
            finish()
        }

        dottedLineView = findViewById(R.id.dottedLineView)


        //     sound

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()

        // Load the sound
        soundId = soundPool.load(this, R.raw.line_draw, 1)
        soundId2 = soundPool.load(this, R.raw.match_complete, 1)

        SoundEffectManager.loadState(this)
        updateSoundIcon()

        binding.music.setOnClickListener {
            SoundEffectManager.toggleSound(this)
            updateSoundIcon()
        }

        binding.next.isEnabled = false

        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 9 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }


        //Timer
        Timer = Timer(context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {

                    if(selectedValues == selectedValues.sorted() || selectedValues == selectedValues.sortedDescending()){
                        val answer = AnswerRequest(
                            question = "68149bf9dd60aa3d342403fa",
                            isCorrect = false,
                            points = 1,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)


                        val intent = Intent(this, MoveClock::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    }else{
                        val answer = AnswerRequest(
                            question = "68149bf9dd60aa3d342403fa",
                            isCorrect = false,
                            points = 0,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)


                        val intent = Intent(this, MoveClock::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    }


                }
            }) // 1 min)
        Timer.start()

        viewModel.loadQuestionData()

        observeDataQuestion()


        setupTouchListenersAndSelection()
    }


    // Function to get the center coordinates of a view
    private fun getViewCenter(view: View): Pair<Float, Float> {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val x = location[0] + view.width / 2f
        val y = location[1] + view.height / 2f
        return Pair(x, y)
    }



    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListenersAndSelection() {
        val textViews = listOf(
            binding.text1,
            binding.text2,
            binding.text3,
            binding.text4,
            binding.text5,
            binding.text6
        )

        textViews.forEach { textView ->
            textView.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val value = (view as TextView).text.toString()
                        if (!selectedValues.contains(value)) {
                            selectedValues.add(value)
                            Log.d("SelectedValues", "Current list: $selectedValues")

                            binding.next.isEnabled = true
                            binding.next.setBackgroundResource(R.drawable.save_button_background)

                            // Redraw all connecting lines
                            drawConnectingLines()

                            binding.next.setOnClickListener {

                                val ascending = selectedValues.sorted()
                                val descending = selectedValues.sortedDescending()

                                if(selectedValues == ascending || selectedValues == descending){

                                    val answer = AnswerRequest(
                                        question = "68149bf9dd60aa3d342403fa",
                                        isCorrect = true,
                                        points = 1,
                                        isSkipped = false
                                    )

                                    AnswerCollector.addAnswer(answer)

                                    if(SoundEffectManager.isSoundOn){
                                        soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                                    }

                                    val intent = Intent(this, MoveClock::class.java)
                                    intent.putExtra("type",type)
                                    startActivity(intent)
                                    Timer.cancel()
                                    finish()
                                }else{

                                    val answer = AnswerRequest(
                                        question = "68149bf9dd60aa3d342403fa",
                                        isCorrect = false,
                                        points = 0,
                                        isSkipped = false
                                    )

                                    AnswerCollector.addAnswer(answer)


                                    val intent = Intent(this, MoveClock::class.java)
                                    intent.putExtra("type",type)
                                    startActivity(intent)
                                    Timer.cancel()
                                    finish()

                                }
                            }

                        } else {
                            Log.d("SelectedValues", "Value '$value' already selected.")
                        }
                    }
                }
                true
            }
        }
    }


    private fun drawConnectingLines() {
        val viewMap = mapOf(
            binding.text1.text.toString() to binding.text1,
            binding.text2.text.toString() to binding.text2,
            binding.text3.text.toString() to binding.text3,
            binding.text4.text.toString() to binding.text4,
            binding.text5.text.toString() to binding.text5,
            binding.text6.text.toString() to binding.text6
        )

        dottedLineView.clearPoints()

        for (i in 0 until selectedValues.size - 1) {
            val startView = viewMap[selectedValues[i]]
            val endView = viewMap[selectedValues[i + 1]]

            if (startView != null && endView != null) {
                val start = getViewCenter(startView)
                val end = getViewCenter(endView)
                dottedLineView.addPoint(start, end)
                if(SoundEffectManager.isSoundOn){
                    soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                }
            }
        }

        dottedLineView.invalidate()
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
                viewModel.dataQuestion.observe(this@ConnectNumber) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question = questionResponse.data.find { it._id == "68149bf9dd60aa3d342403fa" }

                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        Log.d("Questionsss", "${q.question}")

                                        if (q.options != null) {
                                            val values = q.options.values.toList()
                                            Log.d("valueees", "$values")

                                            if (values.size >= 6) {
                                                binding.text1.text = values[0]
                                                binding.text2.text = values[1]
                                                binding.text3.text = values[2]
                                                binding.text4.text = values[3]
                                                binding.text5.text = values[4]
                                                binding.text6.text = values[5]
                                            } else {
                                                androidExtension.alertBox("Not enough options to display", this@ConnectNumber)
                                            }
                                        }
                                    }
                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Question fetch failed", this@ConnectNumber)
                                }
                            } ?: androidExtension.alertBox("Unexpected empty response", this@ConnectNumber)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@ConnectNumber)
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@ConnectNumber)
                            Log.d("API_RESPONSE", "🔄 Loading question data...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state: $response", this@ConnectNumber)
                        }
                    }
                }
            }
        }
    }



}