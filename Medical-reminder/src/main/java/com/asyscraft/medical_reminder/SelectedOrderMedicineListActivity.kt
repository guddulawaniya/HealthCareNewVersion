package com.asyscraft.medical_reminder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.medical_reminder.databinding.ActivitySelectedOrderMedicineListBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectedOrderMedicineListActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectedOrderMedicineListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectedOrderMedicineListBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}