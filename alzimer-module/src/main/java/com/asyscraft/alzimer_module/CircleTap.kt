package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityCircleTapBinding
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
class CircleTap : BaseActivity() {


    //    sound
    lateinit var soundPool: SoundPool
    var soundId: Int = 0
    var soundId2: Int = 0
//    var isSoundOn = true

    private lateinit var Timer: Timer
    private lateinit var correctObjectMap: Map<String, String>
    private lateinit var tag: String
    private val viewModel: YourViewModel by viewModels()
    private var selectedScore: Int? = null
    private var isAnswerSelected = false

    private lateinit var binding: ActivityCircleTapBinding
    private val handler = Handler(Looper.getMainLooper())
    private var mutableDuplicatedImages: MutableList<Pair<String, String>>? = null
    private val baseUrl = "http://172.104.206.4:5000/uploads/"
    private var Usercorrectans = 0




    private val imageRotationRunnable = object : Runnable {
        override fun run() {
            mutableDuplicatedImages?.let {
                // Rotate the images
                val temp = it[0]
                it[0] = it[2]
                it[2] = it[1]
                it[1] = temp

                // Load the rotated images
                Picasso.get().load(baseUrl + it[0].second).into(binding.image1)
                Picasso.get().load(baseUrl + it[1].second).into(binding.image2)
                Picasso.get().load(baseUrl + it[2].second).into(binding.image3)


                // Update the tags accordingly
                binding.image1.tag = it[0].first
                binding.image2.tag = it[1].first
                binding.image3.tag = it[2].first


                // Schedule next rotation
                handler.postDelayed(this, 2000)
            }
        }
    }

    private fun startImageRotation() {
        handler.post(imageRotationRunnable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCircleTapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("type")

        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 2 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }


        binding.next.isEnabled = false
        binding.next.alpha = 0.5f

        //     sound
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        // Load the sound
        soundId = soundPool.load(this, R.raw.clicksound, 1)
        soundId2 = soundPool.load(this, R.raw.match_complete, 1)


        SoundEffectManager.loadState(this)
        updateSoundIcon()

        binding.music.setOnClickListener {
            SoundEffectManager.toggleSound(this)
            updateSoundIcon()
        }

        binding.next.setOnClickListener {
            if(Usercorrectans >=12){
                val answer = AnswerRequest(
                    question = "681492a9dd60aa3d342403e6",
                    isCorrect = true,
                    points = 1,
                    isSkipped = false
                )

                AnswerCollector.addAnswer(answer)
            }else{
                val answer = AnswerRequest(
                    question = "681492a9dd60aa3d342403e6",
                    isCorrect = false,
                    points = 0,
                    isSkipped = true
                )

                AnswerCollector.addAnswer(answer)
            }

            val intent = Intent(this@CircleTap, ObjectsName::class.java)
//            val intent = Intent(this@CircleTap, HiddenObject::class.java)
            intent.putExtra("type",type)
            startActivity(intent)
            Timer.cancel()
            finish()
        }



        //Timer
        Timer = Timer(context = this, textView = binding.timertext, totalTimeInMillis = 30_000L,
            onFinish = {
                binding.centerOverlayImage.visibility = View.VISIBLE
                if (!isFinishing && !isDestroyed) {

                    if(Usercorrectans >=12){
                        val answer = AnswerRequest(
                            question = "681492a9dd60aa3d342403e6",
                            isCorrect = true,
                            points = 1,
                            isSkipped = false
                        )

                        AnswerCollector.addAnswer(answer)


                        if(SoundEffectManager.isSoundOn){
                            soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                        }

                        val intent = Intent(this@CircleTap, ObjectsName::class.java)
//                    val intent = Intent(this@CircleTap, HiddenObject::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    }else{
                        val answer = AnswerRequest(
                            question = "681492a9dd60aa3d342403e6",
                            isCorrect = false,
                            points = 0,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)

//                        if(isSoundOn){
//                            soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
//                        }

                        val intent = Intent(this@CircleTap, ObjectsName::class.java)
//                    val intent = Intent(this@CircleTap, HiddenObject::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    }


                }
            }) // 1 min)
        Timer.start()



        viewModel.loadQuestionData()
//        viewModel.dataQuestion.observe(this) { response ->
//
//            if (response.success) {
//                Log.d("API_RESPONSE", "✅ Success: ${response.msg}")
//
//                val question = response.data.find { it._id == "681492a9dd60aa3d342403e6"}
//
//                question?.let { q ->
//                    // Get the list of images
//                    correctObjectMap = q.correctObject
//
//
//                    binding.instruction.text = q.question.toString()
//                    val imageList = q.image.toList()
//                    Log.d("Questionsss","${q.question}")
//                    Log.d("imagesss circle","${q.image}")
//
//                    // Ensure you have 4 images to display
//                    if (imageList.size >= 3) {
//                        // Duplicate the list to get 3 images
//                        val duplicatedImages = imageList
//                        mutableDuplicatedImages = duplicatedImages.toMutableList()
//
//                        Log.d("Images and tag", "$mutableDuplicatedImages")
//
//                        // Shuffle the list to randomize the order
//                        mutableDuplicatedImages?.shuffle()
//
//                        mutableDuplicatedImages?.let {
//                            binding.image1.tag = it[0].first
//                            binding.image2.tag = it[1].first
//                            binding.image3.tag = it[2].first
//
//
//
//
//
//
//
//                            Picasso.get().load(baseUrl + it[0].second).into(binding.image1)
//                            Picasso.get().load(baseUrl + it[1].second).into(binding.image2)
//                            Picasso.get().load(baseUrl + it[2].second).into(binding.image3)
//
//
//
//                            startImageRotation() // Start rotating every 2 seconds
//                        }
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


        observeDataQuestion()





        val clickListener = View.OnClickListener { view ->
            tag = view.tag.toString()

//            val clickedTag = view.tag
//
//            Log.d("taaaaagclick", "$clickedTag")
//            if (clickedTag != null) {
//                tag = clickedTag.toString()
//                checkAnswer()
//            } else {
//                Log.e("ClickError", "Image tag was null!")
//            }



//            val correct = correctObjectMap.containsKey(tag)
//            Toast.makeText(this, "selected image is ${tag}    = ${correct}", Toast.LENGTH_SHORT).show()
            checkAnswer()
        }

        binding.image1.setOnClickListener(clickListener)
        binding.image2.setOnClickListener(clickListener)
        binding.image3.setOnClickListener(clickListener)



    }


    private fun checkAnswer() {


        if (isAnswerSelected) return // Prevent multiple select

        val isCorrect = correctObjectMap.containsKey(tag)

        if (isCorrect) {
//            selectedScore = 1

            if(SoundEffectManager.isSoundOn){
                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
            }

            Usercorrectans++
//            Toast.makeText(this, "correct ans ${Usercorrectans}", Toast.LENGTH_SHORT).show()
            Log.d("AnswerCheck", "✅ Correct Answer Selected")
        } else {
            selectedScore = 0
            Log.d("AnswerCheck", "❌ Wrong Answer Selected")
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
                viewModel.dataQuestion.observe(this@CircleTap) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question = questionResponse.data.find { it._id == "681492a9dd60aa3d342403e6" }

                                    question?.let { q ->
                                        correctObjectMap = q.correctObject

                                        binding.instruction.text = q.question.toString()
                                        val imageList = q.image.toList()

                                        Log.d("Questionsss", "${q.question}")
                                        Log.d("imagesss circle", "${q.image}")

                                        if (imageList.size >= 3) {
                                            mutableDuplicatedImages = imageList.toMutableList()
                                            mutableDuplicatedImages?.shuffle()

                                            mutableDuplicatedImages?.let {
                                                binding.image1.tag = it[0].first
                                                binding.image2.tag = it[1].first
                                                binding.image3.tag = it[2].first

                                                Picasso.get().load(baseUrl + it[0].second).into(binding.image1)
                                                Picasso.get().load(baseUrl + it[1].second).into(binding.image2)
                                                Picasso.get().load(baseUrl + it[2].second).into(binding.image3)

                                                startImageRotation()
                                            }
                                        } else {
                                            Log.e("API_RESPONSE", "❌ Not enough images to proceed.")
                                            androidExtension.alertBox("Not enough images to show the question.", this@CircleTap)
                                        }
                                    }
                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Question fetch failed", this@CircleTap)
                                }
                            } ?: androidExtension.alertBox("Unexpected empty response", this@CircleTap)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@CircleTap)
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@CircleTap)
                            Log.d("API_RESPONSE", "🔄 Loading question data...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state: $response", this@CircleTap)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(imageRotationRunnable) // Clean up
        soundPool.release()
    }
}