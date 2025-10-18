package com.asyscraft.community_module

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asyscraft.community_module.databinding.ActivityWebViewBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewActivity : BaseActivity() {
    private lateinit var binding: ActivityWebViewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url").toString()
        binding.webView.loadUrl(url)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.databaseEnabled = true
        binding.webView.settings.setSupportZoom(false)



    }
}