package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivitySubtractNumberBinding
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
class SubtractNumber : BaseActivity() {

    private lateinit var binding: ActivitySubtractNumberBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private var score = 0
    private lateinit var type: String
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {

        }
        binding = ActivitySubtractNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getStringExtra("type").toString()

        binding.cross.setOnClickListener {
            finish()
        }

        binding.next.isEnabled = false

        //     sound
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



        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable =
                drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 15 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }

        binding.next.setOnClickListener {

            if (score > 0) {
                Log.d("coooooorrrrr", "$score")

                val answer = AnswerRequest(
                    question = "68149d98dd60aa3d34240404",
                    isCorrect = true,
                    points = score,
                    isSkipped = false
                )

                Log.d("answer1", answer.toString())
                AnswerCollector.addAnswer(answer)

                if (SoundEffectManager.isSoundOn) {
                    soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                }

                val intent = Intent(this, HiddenObject::class.java)
                intent.putExtra("type", type)
                startActivity(intent)
                Timer.cancel()
                finish()
            } else {

                val answer = AnswerRequest(
                    question = "68149d98dd60aa3d34240404",
                    isCorrect = false,
                    points = 0,
                    isSkipped = false
                )
                Log.d("answer2", answer.toString())

                AnswerCollector.addAnswer(answer)

                val intent = Intent(this, HiddenObject::class.java)
                intent.putExtra("type", type)
                startActivity(intent)
                Timer.cancel()
                finish()
            }
        }

        //Timer
        Timer = Timer(
            context = this,
            textView = binding.timertext,
            totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {

                    val answer = AnswerRequest(
                        question = "68149d98dd60aa3d34240404",
                        isCorrect = false,
                        points = score,
                        isSkipped = true
                    )

                    Log.d("answer", answer.toString())

                    AnswerCollector.addAnswer(answer)


                    val intent = Intent(this, HiddenObject::class.java)
                    intent.putExtra("type", type)
                    startActivity(intent)
                    Timer.cancel()
                    finish() // Optional: remove this activity from back stack
                }
            }) // 1 min)
        Timer.start()

        viewModel.loadQuestionData()
        observeDataQuestion()

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
                viewModel.dataQuestion.observe(this@SubtractNumber) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question =
                                        questionResponse.data.find { it._id == "68149d98dd60aa3d34240404" }

                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        binding.instruction2.text = q.instruction.toString()
                                        Log.d("Questionsss", q.question.toString())

                                        val option = q.options
                                        val subtractAmount = q.correctObject

                                        var totalNumber =
                                            option?.values?.firstOrNull()?.toIntOrNull() ?: 0
                                        val subtract =
                                            subtractAmount?.values?.firstOrNull()?.toIntOrNull()
                                                ?: 0
                                        var subtractionCount = 0
                                        val totalSubtractionsRequired = 3
                                        score = 0

                                        binding.number.text = totalNumber.toString()

                                        binding.done.setOnClickListener {
                                            val inputAns =
                                                binding.inputans.text.toString().toIntOrNull()

                                            if (inputAns != null) {
                                                val expectedAnswer = totalNumber - subtract

                                                if (inputAns == expectedAnswer) {
                                                    score += 1
                                                }

                                                totalNumber = inputAns
                                                subtractionCount++

                                                binding.number.text = totalNumber.toString()
                                                binding.inputans.text?.clear()

                                                if (subtractionCount == totalSubtractionsRequired) {
                                                    binding.next.isEnabled = true
                                                    binding.next.setBackgroundResource(R.drawable.save_button_background)
                                                    binding.centerOverlayImage.visibility =
                                                        View.VISIBLE
                                                }
                                            } else {
                                                Toast.makeText(
                                                    this@SubtractNumber,
                                                    "Please enter a valid number",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        Log.d("OPTION_CHECK", "Options: $option")
                                    } ?: androidExtension.alertBox(
                                        "Question not found",
                                        this@SubtractNumber
                                    )

                                } else {
                                    androidExtension.alertBox(
                                        questionResponse.msg ?: "Failed to fetch question",
                                        this@SubtractNumber
                                    )
                                }
                            } ?: androidExtension.alertBox(
                                "Empty response from server",
                                this@SubtractNumber
                            )
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Something went wrong",
                                this@SubtractNumber
                            )
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@SubtractNumber)
                            Log.d("API_RESPONSE", "🔄 Loading...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox(
                                "Unexpected state: $response",
                                this@SubtractNumber
                            )
                        }
                    }
                }
            }
        }
    }


}