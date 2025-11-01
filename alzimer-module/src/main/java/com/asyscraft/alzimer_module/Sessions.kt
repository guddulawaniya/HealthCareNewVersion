package com.asyscraft.alzimer_module

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.adapter.AdapterVideo
import com.careavatar.core_model.alzimer.VideoItem
import com.asyscraft.alzimer_module.databinding.ActivitySessionsBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.asyscraft.alzimer_module.utils.androidExtension
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class Sessions : BaseActivity() {

    private lateinit var binding: ActivitySessionsBinding
    private val viewModel: YourViewModel by viewModels()

    private lateinit var adapter: AdapterVideo
    private var datalist: ArrayList<VideoItem> = ArrayList()
    var isFilter = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySessionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postervideo.visibility = View.GONE
        binding.posterimg.visibility = View.VISIBLE


        binding.back.setOnClickListener {
            finish()
        }

        setupRecyclerView()

        binding.filter.setOnClickListener {
            isFilter = !isFilter

            if (isFilter) {
                binding.filtercard.visibility = View.VISIBLE

                val buttons = listOf(binding.newest, binding.oldest)

                val resetBackgrounds = {
                    buttons.forEach { it.setBackgroundResource(0) } // Remove background from all
                }


                binding.newest.setOnClickListener {
                    resetBackgrounds()
                    binding.newest.setBackgroundResource(R.drawable.filter_icon_bg)
                    viewModel.Getallvideos(sort = "new")

                    binding.filtercard.visibility = View.GONE
                }

                binding.oldest.setOnClickListener {
                    resetBackgrounds()
                    binding.oldest.setBackgroundResource(R.drawable.filter_icon_bg)
                    viewModel.Getallvideos(sort = "old")

                    binding.filtercard.visibility = View.GONE
                }

            } else {
                binding.filtercard.visibility = View.GONE
            }
        }

        binding.main.setOnClickListener {
            binding.filtercard.visibility = View.GONE
        }


        binding.searchvideo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Optional: debounce logic here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.Getallvideos(title = query) // Call your API here
                }
            }
        })

        viewModel.Getallvideos()
        observeResponsegetallvideos()
        observeResponsegetvideodetails()

    }


    private fun setupRecyclerView() {


        adapter = AdapterVideo(this, datalist) { game ->
            Toast.makeText(this, "Clicked on: ${game.title}", Toast.LENGTH_SHORT).show()

            viewModel.GetvideosDetails(game._id)
        }

        binding.recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerview.adapter = adapter

    }


    private fun observeResponsegetallvideos() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.Getallvideos.observe(this@Sessions) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Sessions)
                            Log.d("VideoList", "🔄 Loading videos...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            response.data?.let { data ->
                                if (data.success) {
                                    datalist.clear()
                                    datalist.addAll(data.data)
                                    adapter.notifyDataSetChanged()

                                } else {
                                    androidExtension.alertBox(
                                        data.msg ?: "Failed to load videos",
                                        this@Sessions
                                    )
                                }
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("VideoList", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Something went wrong",
                                this@Sessions
                            )
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected response state", this@Sessions)
                        }
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeResponsegetvideodetails() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.GetvideosDetails.observe(this@Sessions) { response ->

                    when (response) {
                        is Resource.Loading -> {
                            Progresss.start(this@Sessions)
                            Log.d("VideoDetails", "🔄 Loading video details...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()

                            val videoData = response.data
                            if (videoData?.success == true) {

                                // Show video content
                                binding.postervideo.visibility = View.VISIBLE
                                binding.headingv.visibility = View.VISIBLE
                                binding.videodate.visibility = View.VISIBLE
                                binding.viewv.visibility = View.VISIBLE
                                binding.live.visibility = View.VISIBLE
                                binding.posterimg.visibility = View.GONE

                                binding.headingv.text = videoData.video.title
                                binding.viewv.text = "${videoData.totalViews} view"

                                // Format and display date
                                try {
                                    val parsedDate = OffsetDateTime.parse(videoData.video.createdAt)
                                    val formattedDate =
                                        parsedDate.format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy"))
                                    binding.videodate.text = formattedDate
                                } catch (e: Exception) {
                                    Log.e("DateParsing", "Error: ${e.localizedMessage}")
                                    binding.videodate.text = "Unknown date"
                                }

                                // Load and play video
                                val videoUrl = Constants.IMAGE_BASEURL + videoData.video.video
                                binding.postervideo.setVideoPath(videoUrl)
                                binding.postervideo.start()

                            } else {
                                androidExtension.alertBox(
                                    videoData?.msg ?: "Failed to load video",
                                    this@Sessions
                                )
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("VideoDetails", "❌ Error: ${response.message}")
                            androidExtension.alertBox(
                                response.message ?: "Something went wrong",
                                this@Sessions
                            )
                        }

                        else -> {
                            Progresss.stop()
                            androidExtension.alertBox("Unexpected error occurred", this@Sessions)
                        }
                    }
                }
            }
        }
    }

}