package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.utils.Timer
import com.squareup.picasso.Picasso
import android.media.SoundPool
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityPlaceBinding
import com.asyscraft.alzimer_module.singltondata.AnswerCollector
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SoundEffectManager
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Place : BaseActivity() {

    private lateinit var binding: ActivityPlaceBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private lateinit var  type : String
    private lateinit var typedCity: String

    // sound
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0
    //  var isSoundOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type").toString()

        val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val savedLocation = sharedPref.getString("selected_location", null)

        binding.cross.setOnClickListener {
            finish()
        }


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

        binding.next.isEnabled = false


        binding.entercity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // check every time user types
                val input = s.toString().trim()
                if (input.isNotEmpty()) {
                    binding.next.isEnabled = true
                    binding.next.setBackgroundResource(R.drawable.save_button_background)
                } else {
                    binding.next.isEnabled = false
                    binding.next.setBackgroundResource(R.drawable.next_button_disable_background)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // no action needed
            }
        })


        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable =
                drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 6 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }


        binding.next.setOnClickListener {
            typedCity = binding.entercity.text.toString().trim().lowercase()
            val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val savedLocation = sharedPref.getString("selected_location", null)?.trim()?.lowercase()

            Log.d("locaaatttiioon", "${savedLocation}")
            Log.d("typeeedans", "${typedCity}")

            if (savedLocation != null) {

                if (typedCity == savedLocation) {

                    val answer = AnswerRequest(
                        question = "68149a83dd60aa3d342403f0",
                        isCorrect = true,
                        points = 1,
                        isSkipped = false
                    )

                    AnswerCollector.addAnswer(answer)

                    if (SoundEffectManager.isSoundOn) {
                        soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                    }
                    val intent = Intent(this, MatchingChallenge2::class.java)
                    intent.putExtra("type",type)
                    startActivity(intent)
                    Timer.cancel()
                    finish()
                } else {

                    val answer = AnswerRequest(
                        question = "68149a83dd60aa3d342403f0",
                        isCorrect = false,
                        points = 0,
                        isSkipped = false
                    )

                    AnswerCollector.addAnswer(answer)
                    val intent = Intent(this, MatchingChallenge2::class.java)
                    intent.putExtra("type",type)
                    startActivity(intent)
                    Timer.cancel()
                    finish()

                }
            }
        }

        //Timer
        Timer = Timer(context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {
                    if(typedCity == savedLocation){
                        val answer = AnswerRequest(
                            question = "68149a83dd60aa3d342403f0",
                            isCorrect = false,
                            points = 1,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)

                        val intent = Intent(this, MatchingChallenge2::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    }else{
                        val answer = AnswerRequest(
                            question = "68149a83dd60aa3d342403f0",
                            isCorrect = false,
                            points = 0,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)

                        val intent = Intent(this, MatchingChallenge2::class.java)
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
                viewModel.dataQuestion.observe(this@Place) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question = questionResponse.data.find { it._id == "68149a83dd60aa3d342403f0" }

                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        Log.d("Questionsss", q.question.toString())
                                        Log.d("imagesss circle", q.image.toString())

                                        val baseUrl = "http://172.104.206.4:5000/uploads/"
                                        val imageList = q.image

                                        if (imageList.isNotEmpty()) {
                                            val firstEntry = imageList.entries.first()
                                            Picasso.get()
                                                .load(baseUrl + firstEntry.value)
                                                .into(binding.image)
                                        } else {
                                            Log.e("API_RESPONSE", "❌ No images found in question")
                                            androidExtension.alertBox("No images available", this@Place)
                                        }

                                    } ?: androidExtension.alertBox("Question not found", this@Place)

                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Failed to fetch question", this@Place)
                                }
                            } ?: androidExtension.alertBox("Empty response from server", this@Place)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Place)
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@Place)
                            Log.d("API_RESPONSE", "🔄 Loading...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state: $response", this@Place)
                        }
                    }
                }
            }
        }
    }


}