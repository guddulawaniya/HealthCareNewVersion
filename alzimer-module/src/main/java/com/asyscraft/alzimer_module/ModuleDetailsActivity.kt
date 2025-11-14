package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.alzimer_module.databinding.ActivityModuleDetailsBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ModuleDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityModuleDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModuleDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.tvTitle.text = "Module Details"

        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }

        binding.includeBtn.buttonNext.setOnClickListener {
            startActivity(Intent(this, AlzimerMainActivity::class.java))
        }
    }
}