package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
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
    private var isMenuVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupHandleBtnClick()

    }
    private fun setupHandleBtnClick() {
        binding.backbtn.setOnClickListener { finish() }
        binding.userProfile.setOnClickListener {
            startActivity(Intent(this, ChatDetailsActivity::class.java))
        }

        binding.plusbutton.setOnClickListener {
            if (isMenuVisible) hideMenu() else showMenu()
        }

        // Handle menu item clicks
        binding.documentChatLayout.setOnClickListener {
            hideMenu()
        }

        binding.imageChatLayout.setOnClickListener {
            hideMenu()
        }

        binding.audioChatLayout.setOnClickListener {
            hideMenu()
        }
    }

    private fun showMenu() {
        val slideUp =
            AnimationUtils.loadAnimation(this, com.careavatar.core_ui.R.anim.slide_up_fade_in)
        binding.selectImageMenuLayout.visibility = View.VISIBLE
        binding.selectImageMenuLayout.startAnimation(slideUp)
        isMenuVisible = true
    }

    private fun hideMenu() {
        val slideDown =
            AnimationUtils.loadAnimation(this, com.careavatar.core_ui.R.anim.slide_down_fade_out)
        binding.selectImageMenuLayout.startAnimation(slideDown)
        binding.selectImageMenuLayout.postDelayed({
            binding.selectImageMenuLayout.visibility = View.GONE
        }, 200)
        isMenuVisible = false
    }
}