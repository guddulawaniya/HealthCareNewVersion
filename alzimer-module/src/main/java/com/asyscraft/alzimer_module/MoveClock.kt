package com.asyscraft.alzimer_module


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityMoveClockBinding
import com.asyscraft.alzimer_module.singltondata.AnswerCollector
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SoundEffectManager
import com.asyscraft.alzimer_module.utils.Timer
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.roundToInt

@AndroidEntryPoint
class MoveClock : BaseActivity() {

    private lateinit var binding: ActivityMoveClockBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private var correctTime: Map<String, String> = mapOf()
    private lateinit var type: String

    // sound
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMoveClockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getStringExtra("type").toString()
        binding.next.isEnabled = false

        binding.cross.setOnClickListener {
            finish()
        }

        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable =
                drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 10 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }

        // sound
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        // Load the sound
        soundId2 = soundPool.load(this, R.raw.match_complete, 1)

        SoundEffectManager.loadState(this)
        updateSoundIcon()

        binding.music.setOnClickListener {
            SoundEffectManager.toggleSound(this)
            updateSoundIcon()
        }


        //timer
        Timer = Timer(
            context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {

                    val correctTimeValue = correctTime.values.firstOrNull()
                    val currentTime = binding.currentTimeText.text.toString()

                    val isCorrect = (currentTime == correctTimeValue)


                    val answer = AnswerRequest(
                        question = "6814a04add60aa3d3424040e",
                        isCorrect = isCorrect,
                        points = if (isCorrect) 1 else 0,
                        isSkipped = if (isCorrect) false else true
                    )

                    Log.d("answer1", answer.toString())
                    AnswerCollector.addAnswer(answer)


                    val intent = Intent(this, moneySpend::class.java)
                    intent.putExtra("type", type)
                    startActivity(intent)
                    Timer.cancel()
                    finish() // Optional: remove this activity from back stack
                }
            }) // 1 min)
        Timer.start()




        viewModel.loadQuestionData()
        observeDataQuestion()

        setupClockHand(binding.minuteHand, isMinuteHand = true)
        setupClockHand(binding.hourHand, isMinuteHand = false)

    }


    private fun setupClockHand(hand: View, isMinuteHand: Boolean) {
        hand.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onGlobalLayout() {
                hand.viewTreeObserver.removeOnGlobalLayoutListener(this)
                hand.pivotX = (hand.width / 2).toFloat()
                hand.pivotY = hand.height.toFloat()

                hand.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        val centerX = binding.clockContainer.width / 2f
                        val centerY = binding.clockContainer.height / 2f

                        val dx = event.rawX - (binding.clockContainer.left + centerX)
                        val dy = event.rawY - (binding.clockContainer.top + centerY)

                        val angle =
                            Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
                        val finalAngle = if (angle < 0) angle + 360 else angle
                        v.rotation = finalAngle

                        updateTimeFromHands() // Update displayed time after move
                    }
                    true
                }
            }
        })
    }


    private fun updateTimeFromHands() {
        val minuteAngle = binding.minuteHand.rotation
        val hourAngle = binding.hourHand.rotation

        val minutes = ((minuteAngle % 360) / 6).roundToInt() % 60
        val hours = ((hourAngle % 360) / 30).roundToInt() % 12

        val time = String.format("%02d:%02d", hours, minutes)
        binding.currentTimeText.text = time // Add a TextView in your layout to show this


        if (hours != 0 || minutes != 0) {
            binding.next.isEnabled = true
            binding.next.setBackgroundResource(R.drawable.save_button_background)
        } else {
            binding.next.isEnabled = false
            binding.next.setBackgroundResource(R.drawable.next_button_disable_background)
        }


        binding.next.setOnClickListener {
            val correctTime = correctTime.values.firstOrNull()

            if (correctTime == time) {

                val answer = AnswerRequest(
                    question = "6814a04add60aa3d3424040e",
                    isCorrect = true,
                    points = 1,
                    isSkipped = false
                )

                Log.d("answer2", answer.toString())
                AnswerCollector.addAnswer(answer)
                if (SoundEffectManager.isSoundOn) {
                    soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                }

                val intent = Intent(this, moneySpend::class.java)
                intent.putExtra("type", type)
                startActivity(intent)
                Timer.cancel()
                finish()

                Log.d("correcttime", "$time ctime $correctTime")
            } else {
                Log.d("wrong", "$time  ctime $correctTime")

                val answer = AnswerRequest(
                    question = "6814a04add60aa3d3424040e",
                    isCorrect = false,
                    points = 0,
                    isSkipped = false
                )

                Log.d("answer3", answer.toString())
                AnswerCollector.addAnswer(answer)

                val intent = Intent(this, moneySpend::class.java)
                intent.putExtra("type", type)
                startActivity(intent)
                Timer.cancel()
                finish()

            }
        }
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
                viewModel.dataQuestion.observe(this@MoveClock) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question =
                                        questionResponse.data.find { it._id == "6814a04add60aa3d3424040e" }

                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        Log.d("Questionsss", q.question.toString())

                                        val baseUrl = "http://172.104.206.4:5000/uploads/"

                                        correctTime = q.correctObject

                                        // If you want to load a clock face image in the future
                                        // Picasso.get().load(baseUrl + "clock_face.png").into(binding.clockFace)
                                    } ?: androidExtension.alertBox(
                                        "Question not found",
                                        this@MoveClock
                                    )
                                } else {
                                    androidExtension.alertBox(
                                        questionResponse.msg ?: "Question fetch failed",
                                        this@MoveClock
                                    )
                                }
                            } ?: androidExtension.alertBox(
                                "Unexpected empty response",
                                this@MoveClock
                            )
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Something went wrong",
                                this@MoveClock
                            )
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@MoveClock)
                            Log.d("API_RESPONSE", "🔄 Loading question data...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state: $response", this@MoveClock)
                        }
                    }
                }
            }
        }
    }

}