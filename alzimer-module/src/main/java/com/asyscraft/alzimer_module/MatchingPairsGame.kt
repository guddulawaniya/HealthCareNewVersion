package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityMatchingPairsGameBinding
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
class MatchingPairsGame : BaseActivity() {

    //    sound
    lateinit var soundPool: SoundPool
    var soundId: Int = 0
    var soundId2: Int = 0
    private lateinit var shuffledImages: List<String>
    private val viewModel: YourViewModel by viewModels()

    private lateinit var Timer: Timer
    private var firstSelected: ImageView? = null
    private var secondSelected: ImageView? = null
    private var firstSelectedIndex: Int = -1
    private var secondSelectedIndex: Int = -1
    private var point = 0


    private lateinit var binding: ActivityMatchingPairsGameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchingPairsGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("type")

        onBackPressedDispatcher.addCallback(this) {
            // Do nothing to disable back button
        }

        //     sound

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()

        // Load the sound
        soundId = soundPool.load(this, R.raw.clicksound, 1)
        soundId2 = soundPool.load(this, R.raw.match_complete, 1)

        binding.cross.setOnClickListener{
            finish()
        }

        SoundEffectManager.loadState(this)
        updateSoundIcon()

        binding.music.setOnClickListener {
            SoundEffectManager.toggleSound(this)
            updateSoundIcon()
        }


        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 1 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }

        binding.next.isEnabled = false

        binding.next.setOnClickListener {

            val answer = AnswerRequest(
                question = "6814a87ddd60aa3d34240438",
                isCorrect = true,
                points = point,
                isSkipped = false
            )

            Log.d("answerrr", answer.toString());

            AnswerCollector.addAnswer(answer)

            if(SoundEffectManager.isSoundOn){
                soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
            }

            val intent = Intent(this, CircleTap::class.java)
            intent.putExtra("type",type)
            startActivity(intent)
            finish()
            Timer.cancel()

        }

        val coverImages = listOf(
            binding.coverImage1,
            binding.coverImage2,
            binding.coverImage3,
            binding.coverImage4,
            binding.coverImage5,
            binding.coverImage6,
            binding.coverImage7,
            binding.coverImage8
        )


        coverImages.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {







                if(SoundEffectManager.isSoundOn) {

                    soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                }else{

                }



                if (firstSelected == null) {
                    firstSelected = imageView
                    firstSelectedIndex = index
                    imageView.visibility = View.GONE
                } else if (secondSelected == null && imageView != firstSelected) {
                    secondSelected = imageView
                    secondSelectedIndex = index
                    imageView.visibility = View.GONE

                    // After two images selected, check match
                    checkForMatch()
                }
            }
        }



        //timer
        Timer = Timer(
            context = this,
            textView = binding.timertext,
            totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {

                    val answer = AnswerRequest(
                        question = "6814a87ddd60aa3d34240438",
                        isCorrect = false,
                        points = point,
                        isSkipped = true
                    )

                    Log.d("answerrrskip", answer.toString());
                    AnswerCollector.addAnswer(answer)

                    val intent = Intent(this@MatchingPairsGame, CircleTap::class.java)
                    intent.putExtra("type",type)
                    startActivity(intent)
                    finish() // Optional: finish current screen
                    Timer.cancel()
                }
            },
            // 1 min
        )
        Timer.start()

        //Get APi
        viewModel.loadQuestionData()
        observeDataQuestion()

    }


    private fun checkForMatch() {


        val firstImageUrl = shuffledImages[firstSelectedIndex]
        val secondImageUrl = shuffledImages[secondSelectedIndex]

        if (firstImageUrl == secondImageUrl) {
            // Match found! Keep them revealed (do nothing)
            point++
            if(SoundEffectManager.isSoundOn){

                soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
            }
            resetSelection()
        } else {
//            if (point > 0) point--

            // No match, hide them again after delay
            Handler(Looper.getMainLooper()).postDelayed({
                firstSelected?.visibility = View.VISIBLE
                secondSelected?.visibility = View.VISIBLE
                resetSelection()
            }, 1000) // 1 second delay
        }

    }
    private fun resetSelection() {
        firstSelected = null
        secondSelected = null
        firstSelectedIndex = -1
        secondSelectedIndex = -1
        checkIfAllPairsMatched()
    }
    private fun checkIfAllPairsMatched() {
        val allMatched = listOf(
            binding.coverImage1,
            binding.coverImage2,
            binding.coverImage3,
            binding.coverImage4,
            binding.coverImage5,
            binding.coverImage6,
            binding.coverImage7,
            binding.coverImage8
        ).all { it.visibility == View.GONE }

        if (allMatched) {

//            if(isSoundOn){
//                soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
//            }

            binding.next.isEnabled = true
            binding.next.setBackgroundResource(R.drawable.save_button_background)
            binding.centerOverlayImage.visibility = View.VISIBLE
        }
    }


    private fun updateSoundIcon() {
        if (SoundEffectManager.isSoundOn) {
            binding.music.setImageResource(R.drawable.imageon)
        } else {
            binding.music.setImageResource(R.drawable.imageoff)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }



    private fun observeDataQuestion() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataQuestion.observe(this@MatchingPairsGame) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question = questionResponse.data.find { it._id == "6814a87ddd60aa3d34240438" }

                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        Log.d("Questionsss", q.question.toString())

                                        val imageList = q.image.toSortedMap().values.toList()
                                        Log.d("imagesss", imageList.toString())

                                        if (imageList.size >= 4) {
                                            val baseUrl = "http://172.104.206.4:5000/uploads/"
                                            val duplicatedImages = imageList + imageList
                                            val mutableDuplicatedImages = duplicatedImages.toMutableList()

                                            // Optionally shuffle the images
//                                        mutableDuplicatedImages.shuffle()

                                            shuffledImages = mutableDuplicatedImages

                                            val imageViews = listOf(
                                                binding.underImage1, binding.underImage2,
                                                binding.underImage3, binding.underImage4,
                                                binding.underImage5, binding.underImage6,
                                                binding.underImage7, binding.underImage8
                                            )

                                            for (i in imageViews.indices) {
                                                Picasso.get()
                                                    .load(baseUrl + mutableDuplicatedImages[i])
                                                    .into(imageViews[i])
                                            }

                                        } else {
                                            androidExtension.alertBox("Not enough images to load", this@MatchingPairsGame)
                                            Log.e("API_RESPONSE", "❌ Not enough images")
                                        }
                                    } ?: androidExtension.alertBox("Question not found", this@MatchingPairsGame)

                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Question fetch failed", this@MatchingPairsGame)
                                }
                            } ?: androidExtension.alertBox("Unexpected empty response", this@MatchingPairsGame)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@MatchingPairsGame)
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@MatchingPairsGame)
                            Log.d("API_RESPONSE", "🔄 Loading question data...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state: $response", this@MatchingPairsGame)
                        }
                    }
                }
            }
        }
    }

}





