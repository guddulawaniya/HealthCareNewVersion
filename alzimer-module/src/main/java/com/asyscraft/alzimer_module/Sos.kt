package com.asyscraft.alzimer_module

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Sos : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos)

        val back = findViewById<TextView>(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val type = intent.getStringExtra("type").toString()
        // Initialize the SOS button
        val sosButton = findViewById<ImageView>(R.id.sos_btn)
        sosButton.setOnClickListener {

            val intent = Intent(this, SosStart::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
        }
    }

}
