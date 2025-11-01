package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityCorrectSentenceBinding
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
class CorrectSentence : BaseActivity() {

    private lateinit var binding: ActivityCorrectSentenceBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private lateinit var correctObjectMap: Map<String, String>

    private lateinit var  type : String

    //    sound
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0
//    var isSoundOn = true

    // ✅ store last selected answer
    private var selectedTagValue: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCorrectSentenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getStringExtra("type").toString()

        binding.cross.setOnClickListener{
            finish()
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



        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 13 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }

        binding.next.isEnabled = false


//        binding.next.setOnClickListener{
////            val intent = Intent(this, FruitVegitable::class.java)
////            startActivity(intent)
//
//
//            val answersList = listOf(
//                Answer("6806327dc1980def07ed8fcf", true, 4, "2025-04-24T12:00:00.000Z")
//            )
//            viewModel.submitAnswers(answersList)
//        }

        setupBoxClickListeners()

        //Timer
        Timer = Timer(context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,onFinish = {
            if (!isFinishing && !isDestroyed) {

                val tagValue = selectedTagValue
                val isCorrect = tagValue != null && correctObjectMap.keys.contains(tagValue)
                val points = if (isCorrect) 1 else 0

                val answer = AnswerRequest(
                    question = "6814a54add60aa3d3424042e",
                    isCorrect = isCorrect,
                    points = points,
                    isSkipped = if (isCorrect) false else true
                )

                Log.d("answer", answer.toString())
                AnswerCollector.addAnswer(answer)
                val intent = Intent(this, FruitVegitable::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
                Timer.cancel()
                finish() // Optional: remove this activity from back stack
            }
        }) // 1 min)
        Timer.start()

        viewModel.loadQuestionData()
//        viewModel.dataQuestion.observe(this) { response ->
//
//            if (response.success) {
//                Log.d("API_RESPONSE", "✅ Success: ${response.msg}")
//
//                val question = response.data.find { it._id == "6814a54add60aa3d3424042e"}
//
//                question?.let { q ->
//                    // Get the list of images
//
//                    binding.instruction.text = q.question.toString()
//                    Log.d("Questionsss","${q.question}")
//                    correctObjectMap = q.correctObject
//
//                    Log.d("cooooorrr", "${correctObjectMap}")
//
//                    if(q.options != null){
//
//                        q.options?.entries?.toList()?.let { optionList ->
//                            binding.text1.text = optionList[0].value
//                            binding.text1.tag = optionList[0].key
//
//                            binding.text2.text = optionList[1].value
//                            binding.text2.tag = optionList[1].key
//
//                            binding.text3.text = optionList[2].value
//                            binding.text3.tag = optionList[2].key
//
//                            binding.text4.text = optionList[3].value
//                            binding.text4.tag = optionList[3].key
//                        }
//
//                    }
//
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
//                val intent = Intent(this, FruitVegitable::class.java)
//                startActivity(intent)
//                finish()
//
//                Log.d("CorrectSentence", response.msg)
//                // Navigate user to next screen or show success
//            } else {
//                Log.e("CorrectSentence", response.msg)
//                // Show error to user
//            }
//        }


        observeDataQuestion()
    }

    private fun setupBoxClickListeners() {
        val boxes = listOf(binding.box1, binding.box2, binding.box3, binding.box4)
        val texts = listOf(binding.text1, binding.text2, binding.text3, binding.text4)

        boxes.forEachIndexed { index, box ->
            box.setOnClickListener {

                // Reset all boxes and text colors
                boxes.forEach { it.setBackgroundResource(R.drawable.money_selector) }
                texts.forEach { it.setTextColor(ContextCompat.getColor(this, R.color.black)) }

                // Highlight selected one
                box.setBackgroundResource(R.drawable.select_money_box)
                texts[index].setTextColor(ContextCompat.getColor(this, R.color.purple)) // Use a visible color on the selected background

                // Optional: Check tag of selected TextView
                selectedTagValue = texts[index].tag?.toString()
                val correctans = correctObjectMap.keys


                Log.d("taggggvalue", "$selectedTagValue")

                // Enable Next button
                binding.next.isEnabled = true
                binding.next.setBackgroundResource(R.drawable.save_button_background)



                binding.next.setOnClickListener{
                    val tagValue = selectedTagValue
                    if (correctObjectMap.keys.contains(tagValue)) {

                        val answer = AnswerRequest(
                            question = "6814a54add60aa3d3424042e",
                            isCorrect = true,
                            points = 1,
                            isSkipped = false
                        )

                        AnswerCollector.addAnswer(answer)

                        if(SoundEffectManager.isSoundOn){
                            soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                        }
                        // tagValue is a correct answer key
                        val intent = Intent(this , FruitVegitable::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish()

                    }else
                    {

                        val answer = AnswerRequest(
                            question = "6814a54add60aa3d3424042e",
                            isCorrect = false,
                            points = 0,
                            isSkipped = false
                        )

                        AnswerCollector.addAnswer(answer)
                        val intent = Intent(this , FruitVegitable::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish()
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
                viewModel.dataQuestion.observe(this@CorrectSentence) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question = questionResponse.data.find { it._id == "6814a54add60aa3d3424042e" }

                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        correctObjectMap = q.correctObject
                                        Log.d("Questionsss", q.question.toString())
                                        Log.d("cooooorrr", correctObjectMap.toString())

                                        q.options?.entries?.toList()?.let { optionList ->
                                            if (optionList.size >= 4) {
                                                binding.text1.text = optionList[0].value
                                                binding.text1.tag = optionList[0].key

                                                binding.text2.text = optionList[1].value
                                                binding.text2.tag = optionList[1].key

                                                binding.text3.text = optionList[2].value
                                                binding.text3.tag = optionList[2].key

                                                binding.text4.text = optionList[3].value
                                                binding.text4.tag = optionList[3].key
                                            } else {
                                                androidExtension.alertBox("Not enough options provided", this@CorrectSentence)
                                            }
                                        }
                                    } ?: androidExtension.alertBox("Question not found", this@CorrectSentence)
                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Question fetch failed", this@CorrectSentence)
                                }
                            } ?: androidExtension.alertBox("Unexpected empty response", this@CorrectSentence)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@CorrectSentence)
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@CorrectSentence)
                            Log.d("API_RESPONSE", "🔄 Loading question data...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state", this@CorrectSentence)
                        }
                    }
                }
            }
        }
    }



}
