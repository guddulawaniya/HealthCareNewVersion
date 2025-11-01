package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityMoneySpendBinding
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
class moneySpend : BaseActivity() {

    private lateinit var binding: ActivityMoneySpendBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private var totalAmount: Map<String, String> = mapOf()
    private lateinit var type: String

    // sound
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMoneySpendBinding.inflate(layoutInflater)
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

            val filledParts = 11 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }


        setupMoneyBoxClickListeners()
        //timer
        Timer = Timer(
            context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {

                    // Get selected boxes' total (same as in setupMoneyBoxClickListeners)
                    val textViews = listOf(
                        binding.text1,
                        binding.text2,
                        binding.text3,
                        binding.text4,
                        binding.text5
                    )

                    var selectedTotal = 0
                    textViews.forEach { tv ->
                        if (tv.currentTextColor == ContextCompat.getColor(this, R.color.purple)) {
                            selectedTotal += (tv.tag as? String)?.toIntOrNull() ?: 0
                        }
                    }

                    // Correct total from API
                    val correctTotal = totalAmount.entries.firstOrNull()?.value?.toIntOrNull() ?: 0

                    val isCorrect = (selectedTotal == correctTotal)


                    val answer = AnswerRequest(
                        question = "68149d27dd60aa3d342403ff",
                        isCorrect = isCorrect,
                        points = if (isCorrect) 1 else 0,
                        isSkipped = if (isCorrect) false else true
                    )

                    AnswerCollector.addAnswer(answer)

                    val intent = Intent(this, MatchingWords::class.java)
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


    private fun setupMoneyBoxClickListeners() {
        val boxes = listOf(
            binding.monebox1,
            binding.monebox2,
            binding.monebox3,
            binding.monebox4,
            binding.monebox5
        )
        val textViews =
            listOf(binding.text1, binding.text2, binding.text3, binding.text4, binding.text5)

        val selectedBoxes = mutableSetOf<Int>() // store indices of selected boxes

        boxes.forEachIndexed { index, box ->
            box.setOnClickListener {
                val textView = textViews[index]
                val keyAmount = (textView.tag as? String)?.toIntOrNull() ?: 0

                if (selectedBoxes.contains(index)) {
                    selectedBoxes.remove(index)
                    box.setBackgroundResource(R.drawable.box)
                    textView.setTextColor(ContextCompat.getColor(this, R.color.black))
                } else {
                    if (selectedBoxes.size < 3) {
                        selectedBoxes.add(index)
                        box.setBackgroundResource(R.drawable.select_money_box)
                        textView.setTextColor(ContextCompat.getColor(this, R.color.purple))
                    } else {
                        Toast.makeText(this, "You can select only 3 boxes", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                }

                // Enable Next button if at least one box is selected
                binding.next.isEnabled = selectedBoxes.isNotEmpty()
                binding.next.setBackgroundResource(
                    if (binding.next.isEnabled) R.drawable.save_button_background
                    else R.drawable.next_button_disable_background
                )

                // Calculate selected total from keys
                val selectedTotal = selectedBoxes.sumOf { idx ->
                    (textViews[idx].tag as? String)?.toIntOrNull() ?: 0
                }
                Log.d("TOTAL_SELECTED_AMOUNT", "Selected total = $selectedTotal")

                // Calculate correct total from target map keys
                val correctTotal = totalAmount.entries.firstOrNull()?.value?.toIntOrNull() ?: 0
                Log.d("CORRECT_TOTAL", "Correct total = $correctTotal")

                binding.next.setOnClickListener {
                    if (selectedTotal == correctTotal) {

                        val answer = AnswerRequest(
                            question = "68149d27dd60aa3d342403ff",
                            isCorrect = true,
                            points = 1,
                            isSkipped = false
                        )

                        AnswerCollector.addAnswer(answer)

                        if (SoundEffectManager.isSoundOn) {
                            soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                        }

                        val intent = Intent(this, MatchingWords::class.java)
                        intent.putExtra("type", type)
                        startActivity(intent)
                        Timer.cancel()
                        finish()

                        Log.d("answermatch", "${selectedTotal} , ${correctTotal}")
                    } else {

                        val answer = AnswerRequest(
                            question = "68149d27dd60aa3d342403ff",
                            isCorrect = false,
                            points = 0,
                            isSkipped = false
                        )

                        AnswerCollector.addAnswer(answer)

                        val intent = Intent(this, MatchingWords::class.java)
                        intent.putExtra("type", type)
                        startActivity(intent)
                        Timer.cancel()
                        finish()
                        Log.d("notttmatch", "${selectedTotal} , ${correctTotal}")
                    }
                }
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
                viewModel.dataQuestion.observe(this@moneySpend) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question =
                                        questionResponse.data.find { it._id == "68149d27dd60aa3d342403ff" }

                                    question?.let { q ->
                                        binding.question.text = q.question.toString()
                                        Log.d("Questionsss", q.question.toString())

                                        totalAmount = q.correctObject

                                        q.options?.entries?.toList()?.let { entries ->
                                            if (entries.size >= 5) {
                                                binding.text1.apply {
                                                    text = entries[0].value
                                                    tag = entries[0].key
                                                }
                                                binding.text2.apply {
                                                    text = entries[1].value
                                                    tag = entries[1].key
                                                }
                                                binding.text3.apply {
                                                    text = entries[2].value
                                                    tag = entries[2].key
                                                }
                                                binding.text4.apply {
                                                    text = entries[3].value
                                                    tag = entries[3].key
                                                }
                                                binding.text5.apply {
                                                    text = entries[4].value
                                                    tag = entries[4].key
                                                }
                                            } else {
                                                Log.w("API_RESPONSE", "Not enough options provided")
                                            }
                                        }
                                    } ?: Log.w("API_RESPONSE", "Question not found for given ID")
                                } else {
                                    androidExtension.alertBox(
                                        questionResponse.msg ?: "Question fetch failed",
                                        this@moneySpend
                                    )
                                }
                            } ?: androidExtension.alertBox(
                                "Empty question response",
                                this@moneySpend
                            )
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Something went wrong",
                                this@moneySpend
                            )
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@moneySpend)
                            Log.d("API_RESPONSE", "🔄 Loading...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox(
                                "Unexpected state: $response",
                                this@moneySpend
                            )
                        }
                    }
                }
            }
        }
    }


}