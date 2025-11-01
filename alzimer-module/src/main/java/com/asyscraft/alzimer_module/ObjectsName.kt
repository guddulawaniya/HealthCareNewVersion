package com.asyscraft.alzimer_module

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.Manifest
import android.media.SoundPool
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.careavatar.core_model.alzimer.AnswerRequest
import com.asyscraft.alzimer_module.databinding.ActivityObjectsNameBinding
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
class ObjectsName : BaseActivity() {

    private lateinit var binding: ActivityObjectsNameBinding

    private val viewModel: YourViewModel by viewModels()
    private lateinit var Timer: Timer
    private lateinit var objectAns: Map<String, String>
    private  var userAns: List<String> = emptyList()
    private var selectedScore: Int = 0
    private var isAnswerEvaluated = false



    //    sound
    lateinit var soundPool: SoundPool
    var soundId2: Int = 0
//    var isSoundOn = true




    private lateinit var editText: EditText
    private lateinit var voiceButton: TextView
    private val REQUEST_PERMISSION_CODE = 1
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var  type : String

    private  var matchedCount: Int =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityObjectsNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra("type").toString()

        editText = findViewById(R.id.answer)
        voiceButton = findViewById(R.id.voice)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)


        voiceButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecognition()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_CODE)
            }
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


        binding.cross.setOnClickListener{
            finish()
        }
        // Initially hide the Next button
        binding.next.isEnabled = false

        binding.progressView.post {
            val drawable = binding.progressView.background as LayerDrawable
            val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

            val filledParts = 3 // or 2, 3... up to 16
            val level = (filledParts / 16f * 10000).toInt()

            clipDrawable.level = level
        }

        //Timer
        Timer = Timer(context = this, textView = binding.timertext, totalTimeInMillis = 60_000L,
            onFinish = {
                if (!isFinishing && !isDestroyed) {

                    if(matchedCount >= 8){
                        val answer = AnswerRequest(
                            question = "681499bddd60aa3d342403eb",
                            isCorrect = false,
                            points = 2,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)

                        val intent = Intent(this, JigsawPuzzle::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    }else if(matchedCount >= 4){
                        val answer = AnswerRequest(
                            question = "681499bddd60aa3d342403eb",
                            isCorrect = false,
                            points = 1,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)

                        val intent = Intent(this, JigsawPuzzle::class.java)
                        intent.putExtra("type",type)
                        startActivity(intent)
                        Timer.cancel()
                        finish() // Optional: remove this activity from back stack
                    }else{
                        val answer = AnswerRequest(
                            question = "681499bddd60aa3d342403eb",
                            isCorrect = false,
                            points = 0,
                            isSkipped = true
                        )

                        AnswerCollector.addAnswer(answer)

                        val intent = Intent(this, JigsawPuzzle::class.java)
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
//                val question = response.data.find { it._id == "681499bddd60aa3d342403eb"}
//
//                question?.let { q ->
//                    // Get the list of images
//
//                    objectAns = q.options
//
//                    binding.instruction.text = q.question.toString()
//                    val imageList = q.image.toList()
//                    Log.d("Questionsss","${q.question}")
//                    Log.d("imagesss circle","${q.image}")
//                    val baseUrl = "http://172.104.206.4:5000/uploads/"
//
//
//                    // Ensure you have 4 images to display
//                    if (imageList!= null) {
//                        // Duplicate the list to get 3 images
//
//                        // Load shuffled images into ImageViews
//                        Picasso.get()
//                            .load(baseUrl+imageList[0].second)
//                            .into(binding.image)
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

        // Check user answer when button is clicked
        binding.answer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // not needed
            }
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
//                userAns = input.split(",")
                userAns = input.split(Regex("[,\\s]+")) // Split on comma or any whitespace
                    .map { it.trim().lowercase() }  // Clean spaces and make lowercase
                    .filter { it.isNotEmpty() }


                binding.next.isEnabled = userAns.isNotEmpty()
                binding.next.setBackgroundResource(R.drawable.save_button_background)
                isAnswerEvaluated = false

            }
        })



        // Next button click
        binding.next.setOnClickListener {

            if (!isAnswerEvaluated) {
                checkAnswer()
            } else {

            }

        }


    }



    private fun checkAnswer() {
        if (::objectAns.isInitialized) {
            val correctAnswers = objectAns.values.map { it.trim().lowercase() }
            matchedCount = userAns.count { it in correctAnswers }

            selectedScore = matchedCount

            if (matchedCount >= 8) {

                val answer = AnswerRequest(
                    question = "681499bddd60aa3d342403eb",
                    isCorrect = true,
                    points = 2,
                    isSkipped = false
                )

                AnswerCollector.addAnswer(answer)

                if(SoundEffectManager.isSoundOn){
                    soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                }
                val intent = Intent(this, JigsawPuzzle::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
                Timer.cancel()
                finish()
//                Toast.makeText(this, "✅ $matchedCount correct answer(s)", Toast.LENGTH_SHORT).show()


            } else if(matchedCount >= 4){
                val answer = AnswerRequest(
                    question = "681499bddd60aa3d342403eb",
                    isCorrect = true,
                    points = 1,
                    isSkipped = false
                )

                AnswerCollector.addAnswer(answer)

                if(SoundEffectManager.isSoundOn){
                    soundPool.play(soundId2, 1f, 1f, 0, 0, 1f)
                }
                val intent = Intent(this, JigsawPuzzle::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
                Timer.cancel()
                finish()
            }
            else {

                val answer = AnswerRequest(
                    question = "681499bddd60aa3d342403eb",
                    isCorrect = false,
                    points = 0,
                    isSkipped = false
                )

                AnswerCollector.addAnswer(answer)
                val intent = Intent(this, JigsawPuzzle::class.java)
                intent.putExtra("type",type)
                startActivity(intent)
                Timer.cancel()
                finish()
//                Toast.makeText(this, "❌ No correct answers", Toast.LENGTH_SHORT).show()

            }

            isAnswerEvaluated = true
        }
    }

    private fun updateSoundIcon() {
        if (SoundEffectManager.isSoundOn) {
            binding.music.setImageResource(R.drawable.imageon)
        } else {
            binding.music.setImageResource(R.drawable.imageoff)
        }
    }


    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now")

        try {
            startActivityForResult(intent, 100)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Speech recognition not supported on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecognition()
        } else {
            Toast.makeText(this, "Permission denied to access microphone", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null) {
                editText.setText(result[0]) // set the first result to the EditText
            }
        }
    }




    private fun observeDataQuestion() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataQuestion.observe(this@ObjectsName) { response ->
                    when (response) {
                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { questionResponse ->
                                if (questionResponse.success) {
                                    Log.d("API_RESPONSE", "✅ Success: ${questionResponse.msg}")

                                    val question = questionResponse.data.find { it._id == "681499bddd60aa3d342403eb" }

                                    question?.let { q ->
                                        objectAns = q.options
                                        binding.instruction.text = q.question.toString()

                                        val imageList = q.image.toList()
                                        val baseUrl = "http://172.104.206.4:5000/uploads/"

                                        Log.d("Questionsss", q.question.toString())
                                        Log.d("imagesss circle", q.image.toString())

                                        if (imageList.isNotEmpty()) {
                                            Picasso.get()
                                                .load(baseUrl + imageList[0].second)
                                                .into(binding.image)
                                        } else {
                                            Log.e("API_RESPONSE", "❌ Not enough images")
                                            androidExtension.alertBox("Not enough images to load", this@ObjectsName)
                                        }

                                    } ?: androidExtension.alertBox("Question not found", this@ObjectsName)

                                } else {
                                    androidExtension.alertBox(questionResponse.msg ?: "Failed to fetch question", this@ObjectsName)
                                }
                            } ?: androidExtension.alertBox("Empty response from server", this@ObjectsName)
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("API_RESPONSE", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@ObjectsName)
                        }

                        is Resource.Loading -> {
                            Progresss.start(this@ObjectsName)
                            Log.d("API_RESPONSE", "🔄 Loading...")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("API_RESPONSE", "⚠️ Unhandled state: $response")
                            androidExtension.alertBox("Unexpected state: $response", this@ObjectsName)
                        }
                    }
                }
            }
        }
    }


}