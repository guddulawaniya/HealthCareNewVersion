package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityRotateGameBinding
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

@AndroidEntryPoint
class Rotate_game : BaseActivity() {

    private lateinit var binding: ActivityRotateGameBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private lateinit var type: String
    private var currentImageIndex: String = "left"
    private var imageMap: Map<String, String> = mapOf()
    private var correctObject: Map<String, String> = mapOf()
    private val baseUrl = "http://172.104.206.4:5000/uploads/"

    //    sound
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRotateGameBinding.inflate(layoutInflater)
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

            val filledParts = 8 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }


        //     sound
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()

        soundId2 = soundPool.load(this, R.raw.match_complete, 1)

        SoundEffectManager.loadState(this)
        updateSoundIcon()

        binding.music.setOnClickListener {
            SoundEffectManager.toggleSound(this)
            updateSoundIcon()
        }


        binding.next.setOnClickListener {


            val answer = AnswerRequest(
                question = "68149b8ddd60aa3d342403f5",
                isCorrect = true,
                points = 1,
                isSkipped = false
            )

            AnswerCollector.addAnswer(answer)

            if (SoundEffectManager.isSoundOn) {
                soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
            }

            val intent = Intent(this, ConnectNumber::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
            Timer.cancel()
            finish()

        }

        //Timer
        Timer = Timer(
            context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {
                    if (binding.next.isEnabled) {
                        val answer = AnswerRequest(
                            question = "68149b8ddd60aa3d342403f5",
                            isCorrect = false,
                            points = 1,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)

                        val intent = Intent(this, ConnectNumber::class.java)
                        intent.putExtra("type", type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    } else {
                        val answer = AnswerRequest(
                            question = "68149b8ddd60aa3d342403f5",
                            isCorrect = false,
                            points = 0,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)

                        val intent = Intent(this, ConnectNumber::class.java)
                        intent.putExtra("type", type)
                        startActivity(intent)
                        Timer.cancel()
                        finish()
                    }

                }
            })
        Timer.start()


        binding.upbutton.setOnClickListener {
            changeImageByDirection("top")

        }

        binding.downbutton.setOnClickListener {
            changeImageByDirection("bottom")
        }

        binding.leftbutton.setOnClickListener {
            changeImageByDirection("left")
        }

        binding.rightbutton.setOnClickListener {
            changeImageByDirection("right")
        }


        viewModel.loadQuestionData()
        observeDataQuestion()

    }


    private fun checkIfCorrect() {

        val correctImage = correctObject.values.firstOrNull()
        val selectedImage = imageMap[currentImageIndex]
        val selectedKey = imageMap.entries.find { it.value == selectedImage }?.key


        if (selectedKey != null && selectedKey == correctImage) {
            binding.centerOverlayImage.visibility = View.VISIBLE
            binding.next.isEnabled = true
            binding.next.setBackgroundResource(R.drawable.save_button_background)
        } else {
            binding.next.isEnabled = false
            binding.next.setBackgroundResource(R.drawable.next_button_disable_background)
            binding.centerOverlayImage.visibility = View.GONE
        }
    }


    private fun changeImageByDirection(direction: String) {
        currentImageIndex = direction
        val selectedImage = imageMap[currentImageIndex]

        if (!selectedImage.isNullOrEmpty()) {
            val fullUrl = baseUrl + selectedImage

            Picasso.get()
                .load(fullUrl)
                .into(binding.userimg, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        checkIfCorrect()
                    }

                    override fun onError(e: Exception?) {
                        Log.e("changeImage", "Error loading image for $direction", e)
                    }
                })
        } else {
            Log.e("changeImage", "No image found for key: $direction")
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
                viewModel.dataQuestion.observe(this@Rotate_game) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {


                                    val question =
                                        questionResponse.data.find { it._id == "68149b8ddd60aa3d342403f5" }

                                    question?.let { q ->
                                        binding.question.text = q.question.toString()
                                        Log.d("Questionsss", q.question.toString())

                                        imageMap = q.image ?: emptyMap()
                                        correctObject = q.correctObject
                                        val baseUrl = "http://172.104.206.4:5000/uploads/"

                                        if (imageMap.isNotEmpty()) {
                                            imageMap["top"]?.let { correctUrl ->
                                                Picasso.get()
                                                    .load(baseUrl + correctUrl)
                                                    .into(binding.correctimg)
                                            }

                                            imageMap["left"]?.let { leftUrl ->
                                                Picasso.get()
                                                    .load(baseUrl + leftUrl)
                                                    .into(binding.userimg)
                                            }

                                            checkIfCorrect()
                                        } else {
                                            Log.e("API_RESPONSE", "❌ No images found in imageMap")
                                            androidExtension.alertBox(
                                                "No images available",
                                                this@Rotate_game
                                            )
                                        }
                                    } ?: androidExtension.alertBox(
                                        "Question not found",
                                        this@Rotate_game
                                    )

                                } else {
                                    androidExtension.alertBox(
                                        questionResponse.msg ?: "Failed to fetch question",
                                        this@Rotate_game
                                    )
                                }
                            } ?: androidExtension.alertBox(
                                "Empty response from server",
                                this@Rotate_game
                            )
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Something went wrong",
                                this@Rotate_game
                            )
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@Rotate_game)
                            Log.d("API_RESPONSE", "🔄 Loading...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox(
                                "Unexpected state: $response",
                                this@Rotate_game
                            )
                        }
                    }
                }
            }
        }
    }


}