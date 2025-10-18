package com.asyscraft.community_module

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.community_module.databinding.ActivityLeaveCommunityBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaveCommunityActivity : BaseActivity() {
    private lateinit var binding: ActivityLeaveCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}