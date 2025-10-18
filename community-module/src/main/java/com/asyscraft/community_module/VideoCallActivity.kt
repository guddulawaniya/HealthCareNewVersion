package com.asyscraft.community_module

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.community_module.databinding.ActivityVideoCallBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoCallActivity : BaseActivity() {
    private lateinit var binding: ActivityVideoCallBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}