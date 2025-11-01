package com.asyscraft.alzimer_module

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.adapter.AdapterMusic
import com.careavatar.core_model.alzimer.MeditationItem
import com.asyscraft.alzimer_module.databinding.ActivityTimerPageBinding
import com.asyscraft.alzimer_module.utils.Meditation_timer
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Timer_page : BaseActivity() {

    private lateinit var  binding: ActivityTimerPageBinding
    private lateinit var Timer: Meditation_timer

    var isTimerOn = false

    private lateinit var adapter: AdapterMusic
    private val viewModel: YourViewModel by viewModels()
    private var datalist: ArrayList<MeditationItem> = ArrayList()
    private lateinit var recyclerview : RecyclerView

    private lateinit var dialog: BottomSheetDialog



    private var mediaPlayer: MediaPlayer? = null
    private var currentMusicUrl: String? = null
    private var isMediaPrepared = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedTime = intent.getIntExtra("selected_time", 15)
        val timeInMillis = selectedTime * 60 * 1000L


        // Set selected time to TextView
        binding.time.text = "$selectedTime:00"

        // Timer

        Timer = Meditation_timer(context = this, textView = binding.time, totalTimeInMillis = timeInMillis,
            onFinish = {
                if (!isFinishing && !isDestroyed) {
                    Timer.cancel()

                }}) // 1 min)
//        Timer.start()


        binding.playBtn.setOnClickListener{
            isTimerOn = !isTimerOn
            if(isTimerOn){
                Timer.start()
                binding.playBtn.setImageResource(R.drawable.pause_btn)
            }else{
                Timer.pause()  // Pauses the timer without resetting
                binding.playBtn.setImageResource(R.drawable.polygon_2)
            }

            toggleMediaPlayback()

        }


        binding.cross.setOnClickListener{
            finish()
            Timer.cancel()
        }

        binding.setting.setOnClickListener{
            selectImage()
            setupRecyclerView()

            viewModel.Getmusic()
        }


        observeResponsegetmusic()

//        binding.main.setBackgroundResource()
    }




    private fun selectImage() {
        dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_drawer, null)
        dialog.setContentView(view)

        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.findViewById<ImageButton>(R.id.cross).setOnClickListener {
            dialog.dismiss()
        }

        recyclerview = view.findViewById(R.id.musicplayer)



        val backgrounds = listOf(
            R.drawable.forest_img,
            R.drawable.sunset_img,
            R.drawable.sunrise_img
        )

        val container = view.findViewById<LinearLayout>(R.id.backgroundContainer)

        backgrounds.forEach { resId ->
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(330, 500).apply {
                    setMargins(20, 0, 20, 0) // Add spacing between items
                }
                setImageResource(resId)
                scaleType = ImageView.ScaleType.CENTER_CROP
                background = ContextCompat.getDrawable(context, R.drawable.card_image_background)
                clipToOutline = true


                setOnClickListener {
                    binding.main.setBackgroundResource(resId)


                    // Clear current background from this image
                    background = null

                    // Save selected image view
                    dialog.dismiss()
                }
            }
            container.addView(imageView)
        }




        dialog.show()
    }


    private fun setupRecyclerView() {

        adapter = AdapterMusic(this, datalist) { game ->
            Toast.makeText(this, "Clicked on: ${game.title}", Toast.LENGTH_SHORT).show()


            val musicUrl = Constants.IMAGE_BASEURL + game.musicUrl

            Timer.start()
            dialog.dismiss()

            if (currentMusicUrl != musicUrl) {
                currentMusicUrl = musicUrl
                prepareAndPlayMusic(musicUrl)
            } else {
                toggleMediaPlayback()
            }

        }

        recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerview.adapter = adapter

    }



    private fun observeResponsegetmusic() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.Getmusic.observe(this@Timer_page) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Timer_page)
                            Log.d("GetMusic", "🎵 Loading music list...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val musicData = response.data

                            if (musicData?.success == true) {
                                datalist.clear()
                                datalist.addAll(musicData.data)
                                adapter.notifyDataSetChanged()
                            } else {
                                androidExtension.alertBox(musicData?.msg ?: "Failed to load music", this@Timer_page)
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("GetMusic", "❌ Error: ${response.message}")
                            androidExtension.alertBox(response.message ?: "Something went wrong", this@Timer_page)
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@Timer_page)
                        }
                    }

                }
            }
        }
    }




    private fun prepareAndPlayMusic(url: String) {
        mediaPlayer?.release() // Release any existing media player
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
            isMediaPrepared = false

            mediaPlayer?.setOnPreparedListener {
                isMediaPrepared = true
                mediaPlayer?.start()
                binding.playBtn.setImageResource(R.drawable.pause_btn)
            }

            mediaPlayer?.setOnCompletionListener {
                binding.playBtn.setImageResource(R.drawable.polygon_2)
                Timer.pause()
                isTimerOn = false
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error playing music", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


    private fun toggleMediaPlayback() {
        if (mediaPlayer != null && isMediaPrepared) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                Timer.pause()
                binding.playBtn.setImageResource(R.drawable.polygon_2)
            } else {
                mediaPlayer!!.start()
                Timer.start()
                binding.playBtn.setImageResource(R.drawable.pause_btn)
            }
        }
    }






    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}