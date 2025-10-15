package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import com.asyscraft.community_module.databinding.ActivityCommunityCreatedBinding
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityCreatedActivity : BaseActivity() {

    private lateinit var binding: ActivityCommunityCreatedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityCreatedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.buttonNext.text = "Go to community"
        val communityId = intent.getStringExtra("communityId").toString()

        binding.toolbar.buttonNext.setOnClickListener{
            startActivity(Intent(this, CommunityMessageActivity::class.java).putExtra("communityId",communityId))
        }

    }
}