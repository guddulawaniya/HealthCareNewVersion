package com.asyscraft.alzimer_module

import android.content.ClipData
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityJigsawPuzzleBinding
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
class JigsawPuzzle : BaseActivity() {

    private lateinit var binding: ActivityJigsawPuzzleBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer


    private val pickImages = mutableListOf<ImageView>()
    private var lastCorrectCount = 0

    private lateinit var  type : String


    //    sound
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0
//    var isSoundOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJigsawPuzzleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type").toString()

        binding.next.isEnabled = false
        binding.cross.setOnClickListener{
            finish()
        }

        binding.coverImage4.setOnClickListener {
            binding.popupImage.setImageDrawable(binding.coverImage4.drawable)
            binding.imagePopupOverlay.visibility = View.VISIBLE
        }

        binding.closePopup.setOnClickListener {
            binding.imagePopupOverlay.visibility = View.GONE
        }

        setupProgressBar()
        setupDragAndDrop()



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


        binding.next.setOnClickListener{

            if(lastCorrectCount == 4){
                val answer = AnswerRequest(
                    question = "6814a398dd60aa3d3424041e",
                    isCorrect = true,
                    points = 1,
                    isSkipped = false
                )

                AnswerCollector.addAnswer(answer)

                if(SoundEffectManager.isSoundOn){
                    soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                }

                val intent = Intent(this, DateSelector::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
                Timer.cancel()
                finish()
            }else{
                val answer = AnswerRequest(
                    question = "6814a398dd60aa3d3424041e",
                    isCorrect = false,
                    points = 0,
                    isSkipped = false
                )

                AnswerCollector.addAnswer(answer)

                val intent = Intent(this, DateSelector::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
                Timer.cancel()
                finish()
            }


        }

        //Timer
        Timer = Timer(context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {
                    if(lastCorrectCount == 4){
                        val answer = AnswerRequest(
                            question = "6814a398dd60aa3d3424041e",
                            isCorrect = false,
                            points = 1,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)


                        val intent = Intent(this, DateSelector::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    }else{
                        val answer = AnswerRequest(
                            question = "6814a398dd60aa3d3424041e",
                            isCorrect = false,
                            points = 0,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)


                        val intent = Intent(this, DateSelector::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    }


                }}) // 1 min)
        Timer.start()




        viewModel.loadQuestionData()

        observeDataQuestion()


    }


    private fun setupProgressBar() {
        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable
            val filledParts = 4 // Change this based on progress
            val level = (filledParts / 16f * 10000).toInt()
            clipDrawable.level = level
        }
    }

    private fun setupDragAndDrop() {
        // Add all bottom grid ImageViews
        pickImages.addAll(
            listOf(
                binding.pickImage1,
                binding.pickImage2,
                binding.pickImage3,
                binding.pickImage4
            )
        )

        // Set long click listener for bottom images
        pickImages.forEach { image ->
            image.setOnLongClickListener {
                val clipData = ClipData.newPlainText("imageTag", it.tag?.toString() ?: "")
                val shadow = View.DragShadowBuilder(it)
                it.startDragAndDrop(clipData, shadow, it, 0)
                true
            }
        }

        // Top grid targets
        val dropTargets = listOf(
            binding.card1,
            binding.card2,
            binding.card3,
            binding.card4
        )

        dropTargets.forEach { target ->
            target.setOnDragListener { view, event ->
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> true
                    DragEvent.ACTION_DRAG_ENTERED -> {
                        view.alpha = 0.5f
                        true
                    }
                    DragEvent.ACTION_DRAG_EXITED -> {
                        view.alpha = 1.0f
                        true
                    }
                    DragEvent.ACTION_DROP -> {
                        view.alpha = 1.0f
                        val draggedView = event.localState as? ImageView ?: return@setOnDragListener false
                        val sourceImageView = draggedView

                        val dropTarget = view as FrameLayout

                        val existingImage = dropTarget.getChildAt(0) as? ImageView

                        if (pickImages.contains(sourceImageView)) {
                            // Dragged from bottom grid
                            if (existingImage != null) {
                                // Swap: move existing top image to bottom
                                dropTarget.removeView(existingImage)
                                placeImageInBottomGrid(existingImage.drawable, existingImage.tag)
                            }
                            dropTarget.removeAllViews()

                            // Add the new image to top
                            val newImage = createDraggableImage(sourceImageView.drawable, sourceImageView.tag)
                            dropTarget.addView(newImage)

                            // Clear image in bottom grid
                            sourceImageView.setImageDrawable(null)
                            sourceImageView.tag = null
                            shiftBottomImages()
                        } else {
                            // Dragged from another top grid
                            val sourceParent = sourceImageView.parent as? FrameLayout
                            if (sourceParent != null && sourceParent != dropTarget) {
                                if (existingImage != null) {
                                    // Swap images
                                    dropTarget.removeView(existingImage)
                                    sourceParent.removeView(sourceImageView)

                                    dropTarget.addView(sourceImageView)
                                    sourceParent.addView(existingImage)
                                } else {
                                    // Just move it
                                    sourceParent.removeView(sourceImageView)
                                    dropTarget.addView(sourceImageView)
                                }
                            }
                        }

                        if (areAllCardsFilled()) {
                            checkPuzzleCorrectness()
                        }

                        updateHintVisibility()
                        true
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {
                        view.alpha = 1.0f
                        true
                    }
                    else -> false
                }
            }
        }

        // Set bottom grid as drop target
        binding.pickgrid.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as? ImageView ?: return@setOnDragListener false
                    val drawable = draggedView.drawable
                    val tag = draggedView.tag
                    (draggedView.parent as? FrameLayout)?.removeView(draggedView)
                    placeImageInBottomGrid(drawable, tag)
                    true
                }
                else -> true
            }
        }
    }


    private fun shiftBottomImages() {
        // Collect all drawable-tag pairs from current bottom grid
        val drawableTagPairs = pickImages
            .filter { it.drawable != null }
            .map { it.drawable to it.tag }

        // Clear all image views
        pickImages.forEach {
            it.setImageDrawable(null)
            it.tag = null
        }

        // Re-apply drawable-tag pairs in order
        drawableTagPairs.forEachIndexed { index, (drawable, tag) ->
            if (index < pickImages.size) {
                pickImages[index].apply {
                    setImageDrawable(drawable)
                    this.tag = tag
                }
            }
        }
    }
    private fun placeImageInBottomGrid(drawable: android.graphics.drawable.Drawable?, tag: Any?) {
        val emptySlot = pickImages.firstOrNull { it.drawable == null }
        emptySlot?.apply {
            setImageDrawable(drawable)
            this.tag = tag
        }
    }



    private fun checkPuzzleCorrectness() {

        var correctCount = 0

        val cardIds = listOf(
            binding.card1 to "topLeft",
            binding.card2 to "topRight",
            binding.card3 to "bottomLeft",
            binding.card4 to "bottomRight"
        )

        for ((card, underImage) in cardIds) {
            val droppedImage = card.getChildAt(0) as? ImageView

            if (droppedImage != null) {
                val droppedTag = droppedImage.tag?.toString()
                val correctTag = underImage

                if (droppedTag == correctTag) {
                    correctCount++
                }
            }
        }

        lastCorrectCount = correctCount

        if (correctCount == cardIds.size) {
//            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
            binding.next.isEnabled = true
            binding.next.setBackgroundResource(R.drawable.save_button_background)
            binding.centerOverlayImage.visibility = View.VISIBLE

        } else {
//            Toast.makeText(this, "Wrong! Try again. $correctCount", Toast.LENGTH_SHORT).show()

            val allEmpty = pickImages.all { it.drawable == null }

            // Show the 'next' button if all slots are empty, otherwise hide it
            if (allEmpty) {
                binding.next.isEnabled = true
                binding.next.setBackgroundResource(R.drawable.save_button_background)
            } else {
                binding.next.isEnabled = false
                binding.next.setBackgroundResource(R.drawable.next_button_disable_background)
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


    private fun createDraggableImage(drawable: Drawable?, tag: Any?): ImageView {
        return ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageDrawable(drawable)
            this.tag = tag
            setOnLongClickListener { v ->
                val clipData = ClipData.newPlainText("imageTag", tag?.toString() ?: "")
                val shadow = View.DragShadowBuilder(v)
                v.startDragAndDrop(clipData, shadow, v, 0)
                true
            }
        }
    }


    private fun areAllCardsFilled(): Boolean {
        val cards = listOf(binding.card1, binding.card2, binding.card3, binding.card4)
        return cards.all { card ->
            val child = card.getChildAt(0) as? ImageView
            child?.drawable != null
        }
    }



    private fun updateHintVisibility() {
        val hasImage = listOf(binding.card1, binding.card2, binding.card3, binding.card4).any { card ->
            card.children.any { it is ImageView && (it.drawable != null && it.tag != null) }
        }

        binding.hintText.visibility = if (hasImage) View.GONE else View.VISIBLE
    }




    private fun observeDataQuestion() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataQuestion.observe(this@JigsawPuzzle) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question = questionResponse.data.find { it._id == "6814a398dd60aa3d3424041e" }

                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        Log.d("Questionsss", q.question.toString())

                                        val imageList = q.image.toList()
                                        val baseUrl = "http://172.104.206.4:5000/uploads/"
                                        Log.d("imagesss circle", q.image.toString())

                                        if (imageList.isNotEmpty()) {
                                            // Load full correct image
                                            val correctImage = imageList.find { it.first == "correct" }
                                            correctImage?.let {
                                                Picasso.get()
                                                    .load(baseUrl + it.second)
                                                    .into(binding.coverImage4)
                                            } ?: Log.e("ImageLoad", "No matching image found for key: correct")

                                            // Load puzzle pieces
                                            val topLeft = imageList.find { it.first == "topLeft" }
                                            val topRight = imageList.find { it.first == "topRight" }
                                            val bottomLeft = imageList.find { it.first == "bottomLeft" }
                                            val bottomRight = imageList.find { it.first == "bottomRight" }

                                            topLeft?.let {
                                                binding.pickImage1.tag = it.first
                                                Picasso.get().load(baseUrl + it.second).into(binding.pickImage1)
                                            }

                                            topRight?.let {
                                                binding.pickImage2.tag = it.first
                                                Picasso.get().load(baseUrl + it.second).into(binding.pickImage2)
                                            }

                                            bottomLeft?.let {
                                                binding.pickImage3.tag = it.first
                                                Picasso.get().load(baseUrl + it.second).into(binding.pickImage3)
                                            }

                                            bottomRight?.let {
                                                binding.pickImage4.tag = it.first
                                                Picasso.get().load(baseUrl + it.second).into(binding.pickImage4)
                                            }
                                        } else {
                                            Log.e("API_RESPONSE", "❌ Not enough images in list")
                                            androidExtension.alertBox("No images found", this@JigsawPuzzle)
                                        }
                                    } ?: androidExtension.alertBox("Question not found", this@JigsawPuzzle)
                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Question fetch failed", this@JigsawPuzzle)
                                }
                            } ?: androidExtension.alertBox("Unexpected empty response", this@JigsawPuzzle)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@JigsawPuzzle)
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@JigsawPuzzle)
                            Log.d("API_RESPONSE", "🔄 Loading question data...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state: $response", this@JigsawPuzzle)
                        }
                    }
                }
            }
        }
    }




}