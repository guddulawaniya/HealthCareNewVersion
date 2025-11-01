package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.asyscraft.community_module.databinding.ActivityCommunityCallHistoryBinding
import com.asyscraft.community_module.fragments.AllCallHistoryFragment
import com.asyscraft.community_module.fragments.CallHistoryFragment
import com.asyscraft.community_module.fragments.ShareImageFragment
import com.asyscraft.community_module.fragments.SharedAudioFragment
import com.asyscraft.community_module.fragments.SharedDocumentFragment
import com.asyscraft.community_module.fragments.VideoCallHistoryFragment
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityCallHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityCommunityCallHistoryBinding
    private var activeTab: TextView? = null
    private var isSearchVisible : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityCallHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Default tab
        replaceFragment(AllCallHistoryFragment())
        setActiveTab(binding.tabDocuments)

        // Click listeners
        binding.tabImages.setOnClickListener {
            replaceFragment(AllCallHistoryFragment())
            setActiveTab(binding.tabImages)
        }

        binding.tabDocuments.setOnClickListener {
            replaceFragment(CallHistoryFragment())
            setActiveTab(binding.tabDocuments)
        }

        binding.tabAudio.setOnClickListener {
            replaceFragment(VideoCallHistoryFragment())
            setActiveTab(binding.tabAudio)
        }

        // Toolbar navigation back
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }


        // Handle toolbar menu clicks (if you have menu/chat_menu)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {
                    if (isSearchVisible){
                        binding.searchBar.etSearch.visibility = View.GONE
                        isSearchVisible = false
                    }else{
                        binding.searchBar.etSearch.visibility = View.VISIBLE
                        isSearchVisible = true
                    }
                    true
                }
                R.id.action_history -> {
                    startActivity(Intent(this, CommunityCallHistoryActivity::class.java))
                    true
                }
                else -> false
            }
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