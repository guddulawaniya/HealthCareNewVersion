package com.asyscraft.alzimer_module

import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asyscraft.alzimer_module.viewModel.YourViewModel
import com.asyscraft.alzimer_module.databinding.ActivityResultSummeryBinding
import com.asyscraft.alzimer_module.utils.Progresss
import com.asyscraft.alzimer_module.utils.Resource
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class ResultSummery : BaseActivity() {

    private lateinit var binding: ActivityResultSummeryBinding
    private val viewModel: YourViewModel by viewModels()
    private lateinit var  type : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultSummeryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getStringExtra("type").toString()

        binding.progressView1.post{
            updateClipProgress(binding.progressView1, 80)
        }

        binding.progressView2.post{
            updateClipProgress(binding.progressView2, 20)
        }

        binding.progressView3.post{
            updateClipProgress(binding.progressView3, 90)
        }

        binding.progressView4.post{
            updateClipProgress(binding.progressView4, 40)
        }

        binding.progressView5.post{
            updateClipProgress(binding.progressView5, 30)
        }

        binding.getStarted.setOnClickListener {
            val intent = Intent(this,HomePage::class.java)
            intent.putExtra("type",type)
            intent.putExtra("isassesment", true)
            startActivity(intent)
            finish()
        }


        viewModel.NewProgress()
        observeProgressData()

    }

    private fun updateClipProgress(progressView: View, filledParts: Int) {
        val drawable = progressView.background as LayerDrawable
        val clipDrawable = drawable.findDrawableByLayerId(R.id.fill) as android.graphics.drawable.ClipDrawable

        val level = (filledParts / 100f * 10000).toInt()
        clipDrawable.level = level
    }



    private fun observeProgressData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.Newprogress.observe(this@ResultSummery) { result ->
                    when (result) {
                        is Resource.Loading -> {
                            Progresss.start(this@ResultSummery)
                            Log.d("PROGRESS_RESPONSE", "🔄 Loading...")
                        }

                        is Resource.Success -> {
                            Progresss.stop()
                            val response = result.data

                            if (response?.success == true) {
                                Log.d("API_RESPONSE", "✅ Success: ${response.success}")

                                val percentage = response.overall.percentageScore
                                    .toDoubleOrNull()?.roundToInt() ?: 0

                                binding.circularProgress.progress = percentage
                                binding.progressText.text = "$percentage%"

                                // Memory
                                binding.progressView.post {
                                    val category = response.categoryWise.find {
                                        it.categoryId == "680a1ef188b17de8f9df2db6"
                                    }
                                    val fillPercent = category?.percentageScore
                                        ?.toFloatOrNull()?.toInt() ?: 0
                                    updateClipProgress(binding.progressView, fillPercent)
                                    binding.memory.text = "$fillPercent%"
                                }

                                // Attention
                                binding.progressView1.post {
                                    val category = response.categoryWise.find {
                                        it.categoryId == "680a1e9188b17de8f9df2d80"
                                    }
                                    val fillPercent = category?.percentageScore
                                        ?.toFloatOrNull()?.toInt() ?: 0
                                    updateClipProgress(binding.progressView1, fillPercent)
                                    binding.attention.text = "$fillPercent%"
                                }


                                // language
                                binding.progressView2.post {
                                    val category = response.categoryWise.find {
                                        it.categoryId == "680a1ea688b17de8f9df2d84"
                                    }
                                    val fillPercent = category?.percentageScore
                                        ?.toFloatOrNull()?.toInt() ?: 0
                                    updateClipProgress(binding.progressView2, fillPercent)
                                    binding.language.text = "$fillPercent%"
                                }

                                // Ability
                                binding.progressView3.post {
                                    val category = response.categoryWise.find {
                                        it.categoryId == "680a1ec588b17de8f9df2daa"
                                    }
                                    val fillPercent = category?.percentageScore
                                        ?.toFloatOrNull()?.toInt() ?: 0
                                    updateClipProgress(binding.progressView3, fillPercent)
                                    binding.ability.text = "$fillPercent%"
                                }

                                // Executive Function
                                binding.progressView4.post {
                                    val category = response.categoryWise.find {
                                        it.categoryId == "680a1ed588b17de8f9df2dae"
                                    }
                                    val fillPercent = category?.percentageScore
                                        ?.toFloatOrNull()?.toInt() ?: 0
                                    updateClipProgress(binding.progressView4, fillPercent)
                                    binding.executive.text = "$fillPercent%"
                                }

                                // Orientation
                                binding.progressView5.post {
                                    val category = response.categoryWise.find {
                                        it.categoryId == "680a1ee588b17de8f9df2db2"
                                    }
                                    val fillPercent = category?.percentageScore
                                        ?.toFloatOrNull()?.toInt() ?: 0
                                    updateClipProgress(binding.progressView5, fillPercent)
                                    binding.orientation.text = "$fillPercent%"
                                }

                            } else {
                                Log.e("API_RESPONSE", "❌ API responded with failure status")
                            }
                        }

                        is Resource.Error -> {
                            Progresss.stop()
                            Log.e("PROGRESS_RESPONSE", "❌ Error: ${result.message}")
                        }

                        else -> {
                            Progresss.stop()
                            Log.w("PROGRESS_RESPONSE", "⚠️ Unexpected state: $result")
                        }
                    }
                }
            }
        }
    }

}