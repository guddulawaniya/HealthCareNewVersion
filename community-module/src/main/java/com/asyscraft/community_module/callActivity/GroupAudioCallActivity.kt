package com.asyscraft.community_module.callActivity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.community_module.R
import com.asyscraft.community_module.databinding.ActivityGroupAudioCallBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupAudioCallActivity : BaseActivity() {
    private lateinit var binding : ActivityGroupAudioCallBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupAudioCallBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}