package com.asyscraft.medical_reminder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.medical_reminder.databinding.ActivityOrderMedicineListBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderMedicineListActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderMedicineListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderMedicineListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}