package com.asyscraft.medical_reminder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.medical_reminder.databinding.ActivityReviewYourRequestBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewYourRequestActivity : BaseActivity() {

    private lateinit var binding: ActivityReviewYourRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewYourRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.tvTitle.text = "Review Your Request"
        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
    }
}