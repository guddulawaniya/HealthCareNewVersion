package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.SubmitAnswerViewModelFactory
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityHiddenObjectBinding
import com.asyscraft.alzimer_module.singltondata.AnswerCollector
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.SavedPrefManager
import com.asyscraft.alzimer_module.utils.SoundEffectManager
import com.asyscraft.alzimer_module.utils.Timer
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_service.repository.ApiServices
import com.careavatar.core_service.repository.UserRepository
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HiddenObject : BaseActivity() {

    private lateinit var binding: ActivityHiddenObjectBinding
    private lateinit var Timer: Timer
    private val viewModel: YourViewModel by viewModels()

    private lateinit var  type : String

    private var correctObjectCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHiddenObjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type").toString()

        binding.cross.setOnClickListener{
            finish()
        }

        binding.next.isEnabled = false


        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 16 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()
            clipDrawable.level = level

        }

        binding.next.setOnClickListener{

            val points = when (correctObjectCount) {
                 4 -> 4
                 3 -> 3
                 2 -> 2
                 1 -> 1
                else -> 0
            }

            val answer = AnswerRequest(
                question = "6814a80fdd60aa3d34240433",
                isCorrect = true,
                points = points,
                isSkipped = false
            )

            Log.d("answer1", answer.toString())


            AnswerCollector.addAnswer(answer)
            val userid = SavedPrefManager.getStringPreferences(this, SavedPrefManager.USERID)

            if (userid != null) {
                Log.d("userid", userid).toString()
            };

            if(type == "Caregiver"){
                viewModel.submitAllAnswers(userid)
            }else{
                viewModel.submitAllAnswers()
            }


            val intent = Intent(this, ResultSummery::class.java)
            intent.putExtra("type",type)
            startActivity(intent)
            finish()
        }

        //Timer
        Timer = Timer(context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {

                    val points = when (correctObjectCount) {
                        4 -> 4
                        3 -> 3
                        2 -> 2
                        1 -> 1
                        else -> 0
                    }

                    val answer = AnswerRequest(
                        question = "6814a80fdd60aa3d34240433",
                        isCorrect = false,
                        points = points,
                        isSkipped = true
                    )

                    Log.d("answer", answer.toString())
                    AnswerCollector.addAnswer(answer)

                    val intent = Intent(this, ResultSummery::class.java)
                    intent.putExtra("type",type)
                    startActivity(intent)
                    Timer.cancel()
                    finish() // Optional: remove this activity from back stack
                }
            }) // 1 min)
        Timer.start()

        binding.first.visibility = View.GONE
        binding.second.visibility = View.GONE
//        binding.third.visibility = View.GONE
        binding.fourth.visibility = View.GONE
        binding.fifth.visibility = View.GONE


        SoundEffectManager.loadState(this)
        updateSoundIcon()

        binding.music.setOnClickListener {
            SoundEffectManager.toggleSound(this)
            updateSoundIcon()
        }

        viewModel.loadQuestionData()

        observeDataQuestion()





        viewModel.submissionResult.observe(this) { response ->
            if (response.isSuccessful) {
                val intent = Intent(this, ResultSummery::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
                Timer.cancel()

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
                viewModel.dataQuestion.observe(this@HiddenObject) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    val question = questionResponse.data.find { it._id == "6814a80fdd60aa3d34240433" }

                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        val baseUrl = "http://172.104.206.4:5000/uploads/"

                                        val imageMap = q.image // Map<String, String>
                                        val correctMap = q.correctObject // Map<String, String>
                                        val correctKeyToIndexMap = correctMap.keys.withIndex().associate { it.value to it.index }

                                        // Load background image
                                        val backgroundImageUrl = imageMap["first"]
                                        backgroundImageUrl?.let { imageName ->
                                            Picasso.get()
                                                .load(baseUrl + imageName)
                                                .into(object : com.squareup.picasso.Target {
                                                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                                        bitmap?.let {
                                                            val drawable = BitmapDrawable(resources, it)
                                                            binding.background.background = drawable
                                                        }
                                                    }

                                                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                                                        Log.e("Picasso", "Failed to load background", e)
                                                    }

                                                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                                                })
                                        }

                                        // Load correct images into pickImage1 to pickImage5
//                                        correctMap.entries.take(5).forEachIndexed { index, entry ->
//                                            val correctImageViewId = resources.getIdentifier("pickImage${index + 1}", "id", packageName)
//                                            val correctImageView = findViewById<ImageView>(correctImageViewId)
//                                            Picasso.get().load(baseUrl + entry.value).into(correctImageView)
//                                        }


                                        // Load 4 correct images into pickImage1, pickImage2, pickImage4, and pickImage5 (skip pickImage3)
                                        val imageViewIds = listOf(
                                            R.id.pickImage1,
                                            R.id.pickImage2,
                                            R.id.pickImage4,
                                            R.id.pickImage5
                                        )

                                        correctMap.entries
                                            .filter { it.key != "flower" } // optional: filter out "flower" if needed
                                            .take(4)
                                            .forEachIndexed { index, entry ->
                                                val imageView = findViewById<ImageView>(imageViewIds[index])
                                                Picasso.get().load(baseUrl + entry.value).into(imageView)
                                            }


                                        // Manual image mapping for specific keys
                                        val manualImageMap = mapOf(
                                            "giraffe" to binding.imagecard6,
                                            "gorilla" to binding.imagecard7,
                                            "foxRelaxing" to binding.imagecard8,
                                            "grass" to binding.imagecard9,
                                            "mountain" to binding.imagecard10,
                                            "flower" to binding.imagecard11,
                                            "foxEatingWatermelon" to binding.imagecard12,
                                            "crocodile" to binding.imagecard13,
                                            "flamingo" to binding.imagecard14
                                        )

                                        // Setup correct key -> index map once

                                        manualImageMap.forEach { (imageKey, cardView) ->
                                            cardView.setOnClickListener {
                                                if (correctKeyToIndexMap.containsKey(imageKey)) {
                                                    val correctIndex = correctKeyToIndexMap[imageKey] ?: return@setOnClickListener
                                                    when (correctIndex) {
                                                        0 -> markCorrect(binding.first)
                                                        1 -> markCorrect(binding.second)
//                                                        2 -> markCorrect(binding.third)
                                                        3 -> markCorrect(binding.fourth)
                                                        4 -> markCorrect(binding.fifth)
                                                    }
                                                }
                                            }
                                        }

                                        manualImageMap.forEach { (key, frameLayout) ->
                                            val url = imageMap[key]
                                            if (url != null) {
                                                Picasso.get().load(baseUrl + url).into(object : com.squareup.picasso.Target {
                                                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                                        bitmap?.let {
                                                            val drawable = BitmapDrawable(frameLayout.resources, it)
                                                            frameLayout.background = drawable
                                                        }
                                                    }

                                                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                                                        // Optional: Set a fallback background or handle error
                                                        frameLayout.setBackgroundColor(Color.TRANSPARENT)
                                                    }

                                                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                                        // Optional: You can set a placeholder background here
                                                    }
                                                })
                                            }
                                        }


                                    } ?: androidExtension.alertBox("Question not found", this@HiddenObject)
                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Question fetch failed", this@HiddenObject)
                                }
                            } ?: androidExtension.alertBox("Unexpected empty response", this@HiddenObject)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@HiddenObject)
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@HiddenObject)
                            Log.d("API_RESPONSE", "🔄 Loading question data...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state: $response", this@HiddenObject)
                        }
                    }
                }
            }
        }
    }




    private fun markCorrect(view: View) {
        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            binding.next.isEnabled = true
            binding.next.setBackgroundResource(R.drawable.save_button_background)
            correctObjectCount++
            Log.d("CorrectCount", "Correct objects found: $correctObjectCount")
        }
    }

}