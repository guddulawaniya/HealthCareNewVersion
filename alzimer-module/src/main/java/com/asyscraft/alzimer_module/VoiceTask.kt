package com.asyscraft.alzimer_module

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityVoiceTaskBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class VoiceTask : BaseActivity() {

    private lateinit var binding: ActivityVoiceTaskBinding
    private lateinit var dialog: AlertDialog

    private var isRecording = false
    private var mediaRecorder: MediaRecorder? = null
    private var recordedFile: File? = null

    // for listen
    private var mediaPlayer: MediaPlayer? = null

    private var handler = Handler(Looper.getMainLooper())
    private var updateTimeRunnable: Runnable? = null
    private var startTime = 0L
    private var elapsedTime = 0L

    private var count = 0

    private lateinit var activityid: String
    private lateinit var title: String

    private val viewModel: YourViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = intent.getStringExtra("title").toString()
        activityid = intent.getStringExtra("activityid").toString()

        binding.headingText.setText(title)

        binding.play.setOnClickListener {
            count++

            if (count % 2 != 0) {
                // Start or resume recording
                if (!isRecording) {
                    startRecording()
                    startTimer(binding.time)
                }
                binding.play.setImageResource(R.drawable.pause_icons)
            } else {
                // Pause recording
                pauseRecording()
                pauseTimer()
                binding.play.setImageResource(R.drawable.play_icons)
            }
        }

        binding.retry.setOnClickListener {
            resetRecording()
            resetTimer()
            binding.time.text = "00:00:00"
            binding.play.setImageResource(R.drawable.play_icons)
            count = 0
        }

        binding.stop.setOnClickListener {
            if (isRecording) {
                stopRecording()
                pauseTimer()
                binding.play.setImageResource(R.drawable.play_icons)
                isRecording = false
            }
            showSuccessDialogeofSave()
        }


        binding.back.setOnClickListener{
            finish()
        }



        // for listen
        binding.flower.setOnClickListener {
            if (recordedFile != null && recordedFile!!.exists()) {
                mediaPlayer?.release()  // release if already playing something
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(recordedFile!!.absolutePath)
                    prepare()
                    start()
                }

                mediaPlayer?.setOnCompletionListener {
                    Toast.makeText(this, "Playback finished", Toast.LENGTH_SHORT).show()
                    it.release()
                }

                Toast.makeText(this, "Playing recorded audio...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No recording available", Toast.LENGTH_SHORT).show()
            }
        }



        observeResponseactivityvoice()

    }


    // for listen
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startRecording() {
        val outputFile = "${externalCacheDir?.absolutePath}/recorded_${System.currentTimeMillis()}.3gp"
        recordedFile = File(outputFile)

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile)
            prepare()
            start()
        }
        isRecording = true
    }

    private fun pauseRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isRecording = false
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isRecording = false
    }

    private fun resetRecording() {
        pauseRecording()
        recordedFile?.let {
            if (it.exists()) it.delete()
        }
        recordedFile = null
    }

    private fun startTimer(timeTextView: TextView) {
        startTime = System.currentTimeMillis() - elapsedTime
        updateTimeRunnable = object : Runnable {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                elapsedTime = currentTime - startTime

                val minutes = (elapsedTime / 1000) / 60
                val seconds = (elapsedTime / 1000) % 60
                val milliseconds = (elapsedTime % 1000) / 10

                val formattedTime = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds)
                timeTextView.text = formattedTime

                handler.postDelayed(this, 50)
            }
        }
        handler.post(updateTimeRunnable!!)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(updateTimeRunnable!!)
    }

    private fun resetTimer() {
        pauseTimer()
        elapsedTime = 0L
    }

    @SuppressLint("MissingInflatedId")
    private fun showSuccessDialogeofSave() {
        val dialogView = layoutInflater.inflate(R.layout.save_recording, null)
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
        builder.setView(dialogView)
        dialog = builder.create()

        val save = dialogView.findViewById<TextView>(R.id.save)
        val cancel = dialogView.findViewById<TextView>(R.id.cancel)

        save.setOnClickListener {
            Toast.makeText(this, "Voice saved: ${recordedFile?.name}", Toast.LENGTH_SHORT).show()

            viewModel.ActivitySubmit(
                title = title,
                music = recordedFile,
                thumbnail = null,
                isCompleted = true,
                activityId = activityid
            )

            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun observeResponseactivityvoice() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activitysubmit.observe(this@VoiceTask) { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@VoiceTask)
                            Log.d("VoiceUpload", "⏳ Uploading voice...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            Toast.makeText(
                                this@VoiceTask,
                                "✅ Voice sent successfully: ${recordedFile?.name}",
                                Toast.LENGTH_SHORT
                            ).show()

                            resetRecording()
                            resetTimer()
                            binding.time.text = "00:00:00"
                            binding.play.setImageResource(R.drawable.play_icons)
                            count = 0

                            finish()
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("VoiceUpload", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Failed to upload voice",
                                this@VoiceTask
                            )
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@VoiceTask)
                        }
                    }
                }

                viewModel.error.observe(this@VoiceTask) { errorMsg ->
                    Progresss.stop()
                    Toast.makeText(this@VoiceTask, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}

