package com.asyscraft.alzimer_module

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityMatchingWordsBinding
import com.asyscraft.alzimer_module.singltondata.AnswerCollector
import com.asyscraft.alzimer_module.utils.DottedLineView1
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
class MatchingWords : BaseActivity() {

    private lateinit var binding: ActivityMatchingWordsBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private lateinit var dottedLineView: DottedLineView1
    private lateinit var type: String

    // ✅ Global matches list (shared by Next button & Timer)
    private val matches = mutableListOf<Pair<String, String>>()

    // sound
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private var soundId2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchingWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type").toString()
        dottedLineView = findViewById(R.id.dottedLineView)

        binding.cross.setOnClickListener { finish() }
        binding.next.isEnabled = false

        // sound setup
        soundPool = SoundPool.Builder().setMaxStreams(1).build()
        soundId = soundPool.load(this, R.raw.line_draw, 1)
        soundId2 = soundPool.load(this, R.raw.match_complete, 1)

        SoundEffectManager.loadState(this)
        updateSoundIcon()

        binding.music.setOnClickListener {
            SoundEffectManager.toggleSound(this)
            updateSoundIcon()
        }

        // progress bar
        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable
            val filledParts = 12
            val level = (filledParts / 16f * 10000).toInt()
            clipDrawable.level = level
        }

        // ✅ Timer
        Timer = Timer(
            context = this,
            textView = binding.timertext,
            totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {
                    val matchedCount = matches.count { (key, value) -> key == value }
                    val hasCorrect = matchedCount > 0

                    val answer = AnswerRequest(
                        question = "6814a29bdd60aa3d34240419",
                        isCorrect = if (matchedCount > 0) true else false,
                        points = matchedCount,
                        isSkipped = if (matchedCount > 0) false else true
                    )

                    Log.d("answer", answer.toString())
                    AnswerCollector.addAnswer(answer)

                    val intent = Intent(this, CorrectSentence::class.java)
                    intent.putExtra("type", type)
                    startActivity(intent)
                    Timer.cancel()
                    finish()
                }
            }
        )
        Timer.start()

        viewModel.loadQuestionData()
        observeDataQuestion()
        setupTouchListenersAndSelection()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListenersAndSelection() {
        val textViews = listOf(binding.textChair, binding.textWatch, binding.textTelephone, binding.bed)
        val imageViews = listOf(binding.igChair, binding.imgClock, binding.imgPhone, binding.imgBed)

        var selectedTextView: TextView? = null
        var selectedImageView: ImageView? = null
        val matchedImages = mutableSetOf<String>()
        val matchedTexts = mutableSetOf<String>()

        textViews.forEach { textView ->
            textView.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val text = (view as TextView).text.toString()
                    if (matchedTexts.contains(text)) return@setOnTouchListener true
                    selectedTextView = view
                    textView.setTextColor(getColor(R.color.purple))
                }
                true
            }
        }

        imageViews.forEach { imageView ->
            imageView.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    selectedImageView = view as ImageView
                    val imageTag = selectedImageView?.tag?.toString() ?: return@setOnTouchListener true
                    if (matchedImages.contains(imageTag)) return@setOnTouchListener true

                    if (selectedTextView != null) {
                        val start = getViewCenter(selectedTextView!!)
                        val end = getViewCenter(selectedImageView!!)
                        dottedLineView.setPoints(start, end, finalize = true)
                        if (SoundEffectManager.isSoundOn) soundPool.play(soundId, 1f, 1f, 0, 0, 1f)

                        val matchedText = selectedTextView!!.tag.toString()
                        matches.add(Pair(matchedText, imageTag))
                        matchedImages.add(imageTag)
                        matchedTexts.add(matchedText)

                        val matchedCount = matches.count { (key, value) -> key == value }
                        Log.d("MATCHES", "matches=$matches, correctCount=$matchedCount")

                        if (matches.size == 4) {
                            binding.next.isEnabled = true
                            binding.next.setBackgroundResource(R.drawable.save_button_background)

                            binding.next.setOnClickListener {
                                val hasCorrect = matchedCount > 0
                                val answer = AnswerRequest(
                                    question = "6814a29bdd60aa3d34240419",
                                    isCorrect = hasCorrect,
                                    points = matchedCount,
                                    isSkipped = false
                                )
                                AnswerCollector.addAnswer(answer)

                                if (hasCorrect && SoundEffectManager.isSoundOn) {
                                    soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                                }

                                val intent = Intent(this, CorrectSentence::class.java)
                                intent.putExtra("type", type)
                                startActivity(intent)
                                Timer.cancel()
                                finish()
                            }
                        }

                        selectedTextView = null
                        selectedImageView = null
                    }
                }
                true
            }
        }
    }

    private fun getViewCenter(view: View, yOffset: Float = 100f): Pair<Float, Float> {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val x = location[0] + view.width / 2f
        val y = location[1] + view.height / 2f - yOffset
        return Pair(x, y)
    }

    private fun updateSoundIcon() {
        binding.music.setImageResource(if (SoundEffectManager.isSoundOn) R.drawable.imageon else R.drawable.imageoff)
    }

    private fun observeDataQuestion() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataQuestion.observe(this@MatchingWords) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    val question = questionResponse.data.find { it._id == "6814a29bdd60aa3d34240419" }
                                    question?.let { q ->
                                        binding.instruction.text = q.question.toString()
                                        val baseUrl = "http://172.104.206.4:5000/uploads/"
                                        val imageList = q.image.entries.toList()
                                        if (imageList.size >= 4) {
                                            binding.imgBed.apply {
                                                Picasso.get().load(baseUrl + imageList[1].value).into(this)
                                                tag = imageList[1].key
                                            }
                                            binding.imgClock.apply {
                                                Picasso.get().load(baseUrl + imageList[0].value).into(this)
                                                tag = imageList[0].key
                                            }
                                            binding.igChair.apply {
                                                Picasso.get().load(baseUrl + imageList[2].value).into(this)
                                                tag = imageList[2].key
                                            }
                                            binding.imgPhone.apply {
                                                Picasso.get().load(baseUrl + imageList[3].value).into(this)
                                                tag = imageList[3].key
                                            }
                                        } else androidExtension.alertBox("Not enough images", this@MatchingWords)

                                        q.options?.entries?.toList()?.let { optionList ->
                                            if (optionList.size >= 4) {
                                                binding.textChair.apply {
                                                    text = optionList[0].value
                                                    tag = optionList[0].key
                                                }
                                                binding.textWatch.apply {
                                                    text = optionList[1].value
                                                    tag = optionList[1].key
                                                }
                                                binding.textTelephone.apply {
                                                    text = optionList[2].value
                                                    tag = optionList[2].key
                                                }
                                                binding.bed.apply {
                                                    text = optionList[3].value
                                                    tag = optionList[3].key
                                                }
                                            }
                                        }
                                    } ?: androidExtension.alertBox("Question not found", this@MatchingWords)
                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Question fetch failed", this@MatchingWords)
                                }
                            } ?: androidExtension.alertBox("Unexpected empty response", this@MatchingWords)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@MatchingWords)
                        }

                        is Resource.Loading -> Progresss.start(this@MatchingWords)
                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected state: $response", this@MatchingWords)
                        }
                    }
                }
            }
        }
    }
}
