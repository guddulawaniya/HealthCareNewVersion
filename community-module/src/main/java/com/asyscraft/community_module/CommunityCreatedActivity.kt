package com.asyscraft.community_module

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.asyscraft.community_module.databinding.ActivityCommunityCreatedBinding
import com.careavatar.core_model.CreateCommunityResponse
import com.careavatar.core_model.Data
import com.careavatar.core_network.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityCreatedActivity : BaseActivity() {

    private lateinit var binding: ActivityCommunityCreatedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityCreatedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getBooleanExtra("leaveCommunity",false)){

            val adminName = intent.getStringExtra("adminName")
            binding.toolbar.buttonNext.text = "Done"
            binding.titleText.text = "Transferred Successfully"
            binding.descriptionText.text = "You have successfully transferred admin rights to $adminName. They now have full control over community settings"

            binding.toolbar.buttonNext.setOnClickListener {
                navigateDashboard(2)
            }
        }
        else
        {

            binding.toolbar.buttonNext.text = "Go to community"
            val memberIds = ArrayList<String>()
            val array = intent.getParcelableExtra<Data>("communityData")

            array?.let { data ->
                binding.descriptionText.text =
                    "Your community '${data.name}' has been successfully created. You can now start sharing content and engaging with members."

                for (member in data.members) {
                    memberIds.add(member)
                }

                binding.toolbar.buttonNext.setOnClickListener {

                    startActivity(Intent(this, CommunityMessageActivity::class.java)
                        .putExtra("createdCommunity", true)
                        .putExtra("communityId", array._id)
                        .putExtra("creatorId", array.creatorId)
                        .putStringArrayListExtra("members", memberIds)
                        .putExtra("communityName", array.name)
                        .putExtra("communityImage", array.communityLogo)
                        .putExtra("count", array.members.size.toString())
                        .putExtra("type1", array.type)

                    )

                }
            }


        }



    }
}