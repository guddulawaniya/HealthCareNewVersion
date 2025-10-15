package com.asyscraft.service_module.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.asyscraft.service_module.databinding.ActivityOrderDetailsBinding
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_ui.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            btnBack.setOnClickListener {
                finish()
            }
            tvTitle.text = getString(R.string.order_details)
        }


        binding.apply {
            etQuantity.addTextChangedListener(formWatcher)
            etDeliveryAddress.addTextChangedListener(formWatcher)
            etContactNumber.addTextChangedListener(formWatcher)
        }
        binding.btnConfirmOrder.setOnClickListener {
            startActivity(Intent(this, OrderSummaryActivity::class.java))
        }

        setupUrgencySwitch()

    }

    private fun setupUrgencySwitch() {

        val activeBg = R.drawable.height_active_tab_bg


        // Default: Normal selected
        binding.tvNormal.setBackgroundResource(activeBg)
        binding.tvUrgent.setBackgroundResource(0)

        binding.tvNormal.setOnClickListener {
            binding.tvNormal.setBackgroundResource(activeBg)
            binding.tvUrgent.setBackgroundResource(0)
        }

        binding.tvUrgent.setOnClickListener {
            binding.tvUrgent.setBackgroundResource(activeBg)
            binding.tvNormal.setBackgroundResource(0)
        }

    }


    private val formWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            checkFormValid()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun checkFormValid() {
        val quantity = binding.etQuantity.text.toString().trim()
        val address = binding.etDeliveryAddress.text.toString().trim()
        val contact = binding.etContactNumber.text.toString().trim()

        val isValid = quantity.isNotEmpty() && address.isNotEmpty() && contact.isNotEmpty()
        binding.btnConfirmOrder.isEnabled = isValid
    }
}