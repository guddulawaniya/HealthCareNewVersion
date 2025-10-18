package com.asyscraft.community_module

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.community_module.databinding.ActivityMessageBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageActivity : BaseActivity() {
    private lateinit var binding : ActivityMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}