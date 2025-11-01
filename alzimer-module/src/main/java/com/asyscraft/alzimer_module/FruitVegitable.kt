package com.asyscraft.alzimer_module

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityFruitVegitableBinding
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
class FruitVegitable : BaseActivity() {

    private lateinit var binding: ActivityFruitVegitableBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer

    private lateinit var  type : String


    //    sound
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0
//    var isSoundOn = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFruitVegitableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getStringExtra("type").toString()

        binding.cross.setOnClickListener{
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





        val pickImage5 = binding.pickImage5 // your LinearLayout (basket)
        val pickImage6 = binding.pickImage6 // Right basket (Vegetables)
        val pickImages = listOf(binding.pickImage1, binding.pickImage2, binding.pickImage3, binding.pickImage4)

        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 14 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()
            clipDrawable.level = level

        }



        //Timer
        Timer = Timer(context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {
                    handleTimeoutAnswer()
//                    val intent = Intent(this, SubtractNumber::class.java)
//                    intent.putExtra("type",type)
//                    startActivity(intent)
//                    Timer.cancel()
//                    finish() // Optional: remove this activity from back stack
                }
            }) // 1 min)
        Timer.start()


        // For each image in the pickgrid, make it draggable
        pickImages.forEach { imageView ->
            imageView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val shadowBuilder = View.DragShadowBuilder(v)
                    v.startDragAndDrop(null, shadowBuilder, v, 0)
                    true
                } else {
                    false
                }
            }
        }



        setupDropTarget(pickImage5)
        setupDropTarget(pickImage6)


        viewModel.loadQuestionData()

        observeDataQuestion()

    }



    private fun adjustImagesInBasket(container: LinearLayout) {
        val totalImages = container.childCount

        for (i in 0 until totalImages) {
            val imageView = container.getChildAt(i) as ImageView

            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f // Equal weight
            )
            params.marginStart = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp)
            params.marginEnd = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp)

            imageView.layoutParams = params
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }



    fun setupDropTarget(basket: LinearLayout) {

        val originalBackground = basket.background

        basket.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as ImageView

                    // Remove from old parent
                    (draggedView.parent as? ViewGroup)?.removeView(draggedView)

                    // Add into this basket
                    basket.addView(draggedView)

                    // Adjust images in this basket
                    adjustImagesInBasket(basket)

                    // ✨ Hide the background of basket when an image is added
//                    basket.background = null


                    // After dropping, check basket correctness
                    checkBaskets()


                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    // 🧠 After drag ends, check if basket has any children (images)
                    if (basket.childCount == 0) {
                        // If no images inside, show background again
                        basket.background = originalBackground
                        // 🔥 Replace `your_background_drawable` with whatever you were using
                    }
                    true
                }
                else -> true
            }
        }
    }



    private fun handleTimeoutAnswer() {
        var fruitsCorrect = false
        var vegetablesCorrect = false

        // --- Check fruits basket ---
        if (binding.pickImage5.childCount == 2) {
            val fruitTags = mutableListOf<String>()
            for (i in 0 until binding.pickImage5.childCount) {
                val imageView = binding.pickImage5.getChildAt(i) as? ImageView
                val description = imageView?.tag?.toString()
                description?.let { fruitTags.add(it) }
            }
            if (fruitTags.containsAll(listOf("cartFruit1", "cartFruit2"))) {
                fruitsCorrect = true
            }
        }

        // --- Check vegetables basket ---
        if (binding.pickImage6.childCount == 2) {
            val vegTags = mutableListOf<String>()
            for (i in 0 until binding.pickImage6.childCount) {
                val imageView = binding.pickImage6.getChildAt(i) as? ImageView
                val description = imageView?.tag?.toString()
                description?.let { vegTags.add(it) }
            }
            if (vegTags.containsAll(listOf("cartVeg1", "cartVeg2"))) {
                vegetablesCorrect = true
            }
        }

        // --- Scoring when timer finishes ---
        when {
            fruitsCorrect && vegetablesCorrect -> {
                giveAnswer(points = 2, isCorrect = true, logTag = "timeout_answer2")
            }
            fruitsCorrect || vegetablesCorrect -> {
                giveAnswer(points = 1, isCorrect = true, logTag = "timeout_answer1")
            }
            else -> {
                giveAnswer(points = 0, isCorrect = false, logTag = "timeout_answer0")
            }
        }
    }





    private fun checkBaskets() {
        var fruitsCorrect = false
        var vegetablesCorrect = false

        // --- Check fruits basket (must contain BOTH cartFruit1 & cartFruit2) ---
        if (binding.pickImage5.childCount == 2) { // only valid if exactly 2 items
            val fruitTags = mutableListOf<String>()
            for (i in 0 until binding.pickImage5.childCount) {
                val imageView = binding.pickImage5.getChildAt(i) as? ImageView
                val description = imageView?.tag?.toString()
                Log.d("fruiitttt", "$description")
                description?.let { fruitTags.add(it) }
            }

            // Check if both required fruits are present
            if (fruitTags.containsAll(listOf("cartFruit1", "cartFruit2"))) {
                fruitsCorrect = true
            }
        }

        // --- Check vegetables basket (must contain BOTH cartVeg1 & cartVeg2) ---
        if (binding.pickImage6.childCount == 2) { // only valid if exactly 2 items
            val vegTags = mutableListOf<String>()
            for (i in 0 until binding.pickImage6.childCount) {
                val imageView = binding.pickImage6.getChildAt(i) as? ImageView
                val description = imageView?.tag?.toString()
                Log.d("vegiiitable", "$description")
                description?.let { vegTags.add(it) }
            }

            // Check if both required vegetables are present
            if (vegTags.containsAll(listOf("cartVeg1", "cartVeg2"))) {
                vegetablesCorrect = true
            }
        }

        val hasImageInAnyBasket = binding.pickImage5.childCount > 0 || binding.pickImage6.childCount > 0

        if (hasImageInAnyBasket) {
            binding.next.isEnabled = true
            binding.next.setBackgroundResource(R.drawable.save_button_background)

            binding.next.setOnClickListener {
                when {
                    fruitsCorrect && vegetablesCorrect -> {
                        giveAnswer(points = 2, isCorrect = true, logTag = "answer1")
                    }
                    fruitsCorrect || vegetablesCorrect -> {
                        giveAnswer(points = 1, isCorrect = true, logTag = "answer2")
                    }
                    else -> {
                        giveAnswer(points = 0, isCorrect = false, logTag = "answer")
                    }
                }
            }
        } else {
            binding.next.isEnabled = false
            binding.next.setBackgroundResource(R.drawable.next_button_disable_background)
        }
    }




    private fun giveAnswer(points: Int, isCorrect: Boolean, logTag: String) {
        val answer = AnswerRequest(
            question = "68149fafdd60aa3d34240409",
            isCorrect = isCorrect,
            points = points,
            isSkipped = false
        )

        Log.d("answer", answer.toString())
        AnswerCollector.addAnswer(answer)

        if (SoundEffectManager.isSoundOn) {
            soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
        }

        val intent = Intent(this, SubtractNumber::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
        Timer.cancel()
        finish()
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
                viewModel.dataQuestion.observe(this@FruitVegitable) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question = questionResponse.data.find { it._id == "68149fafdd60aa3d34240409" }

                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        binding.instruction2.text = q.instruction.toString()
                                        Log.d("Questionsss", "${q.question}")

                                        val baseUrl = "http://172.104.206.4:5000/uploads/"
                                        val imageList = q.image.entries.toList()

                                        if (imageList.size >= 6) {
                                            binding.pickImage1.apply {
                                                Picasso.get().load(baseUrl + imageList[5].value).into(this)
                                                tag = imageList[5].key
                                            }

                                            binding.pickImage2.apply {
                                                Picasso.get().load(baseUrl + imageList[4].value).into(this)
                                                tag = imageList[4].key
                                            }

                                            binding.pickImage3.apply {
                                                Picasso.get().load(baseUrl + imageList[2].value).into(this)
                                                tag = imageList[2].key
                                            }

                                            binding.pickImage4.apply {
                                                Picasso.get().load(baseUrl + imageList[3].value).into(this)
                                                tag = imageList[3].key
                                            }
                                        } else {
                                            androidExtension.alertBox("Not enough images to load", this@FruitVegitable)
                                            Log.e("API_RESPONSE", "❌ Not enough images in list")
                                        }
                                    } ?: androidExtension.alertBox("Question not found", this@FruitVegitable)
                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Question fetch failed", this@FruitVegitable)
                                }
                            } ?: androidExtension.alertBox("Unexpected empty response", this@FruitVegitable)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@FruitVegitable)
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@FruitVegitable)
                            Log.d("API_RESPONSE", "🔄 Loading question data...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state", this@FruitVegitable)
                        }
                    }
                }
            }
        }
    }



}










































