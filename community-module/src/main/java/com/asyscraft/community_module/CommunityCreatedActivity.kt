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

            binding.toolbar.buttonNext.text = "Done"
            binding.titleText.text = "Transferred Successfully"
            binding.descriptionText.text = "You have successfully transferred admin rights to Riya Dixit. They now have full control over community settings"
        }
        else
        {

            binding.toolbar.buttonNext.text = "Go to community"

            val array = intent.getParcelableExtra<Data>("communityData")

            binding.descriptionText.text =
                "Your community '${array?.name}' has been successfully created. You can now start sharing content and engaging with members."

            val memberIds = ArrayList<String>()

            array?.members?.let { members ->
                for (member in members) {
                    memberIds.add(member)
                }
            } ?: run {
                Log.e("CommunityCreatedActivity", "Members list is null")
            }

            binding.toolbar.buttonNext.setOnClickListener {
                if (array != null) {
                    startActivity(
                        Intent(this, CommunityMessageActivity::class.java)
                            .putExtra("communityId", array._id)
                            .putExtra("creatorId", array.creatorId)
                            .putStringArrayListExtra("members", memberIds)
                            .putExtra("communityName", array.name)
                            .putExtra("communityImage", array.communityLogo)
                            .putExtra("type1", array.type)
                    )
                } else {
                    Log.e("CommunityCreatedActivity", "Community data is null")
                }
            }

        }



    }
}