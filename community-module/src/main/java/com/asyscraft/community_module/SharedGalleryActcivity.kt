package com.asyscraft.community_module

import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.asyscraft.community_module.databinding.ActivitySharedGalleryActcivityBinding
import com.asyscraft.community_module.fragments.ShareImageFragment
import com.asyscraft.community_module.fragments.SharedAudioFragment
import com.asyscraft.community_module.fragments.SharedDocumentFragment
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SharedGalleryActcivity : BaseActivity() {

    private lateinit var binding: ActivitySharedGalleryActcivityBinding
    private var activeTab: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedGalleryActcivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbar.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.tvTitle.text = "Shared Gallery"

        // Default tab
        replaceFragment(ShareImageFragment())
        setActiveTab(binding.tabImages)

        // Click listeners
        binding.tabImages.setOnClickListener {
            replaceFragment(ShareImageFragment())
            setActiveTab(binding.tabImages)
        }

        binding.tabDocuments.setOnClickListener {
            replaceFragment(SharedDocumentFragment())
            setActiveTab(binding.tabDocuments)
        }

        binding.tabAudio.setOnClickListener {
            replaceFragment(SharedAudioFragment())
            setActiveTab(binding.tabAudio)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentContainer, fragment)
            .commit()
    }

    private fun setActiveTab(selectedTab: TextView) {
        // Reset all tabs
        binding.tabImages.setBackgroundResource(0)
        binding.tabDocuments.setBackgroundResource(0)
        binding.tabAudio.setBackgroundResource(0)

        binding.tabImages.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.hintColor))
        binding.tabDocuments.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.hintColor))
        binding.tabAudio.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.hintColor))

        // Highlight selected
        selectedTab.setBackgroundResource(com.careavatar.core_ui.R.drawable.height_active_tab_bg)
        selectedTab.setTextColor(ContextCompat.getColor(this, com.careavatar.core_ui.R.color.black))

        activeTab = selectedTab
    }
}
