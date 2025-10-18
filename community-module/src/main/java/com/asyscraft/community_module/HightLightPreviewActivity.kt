package com.asyscraft.community_module

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.asyscraft.community_module.databinding.ActivityHightLightPreviewBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_network.base.BaseActivity
import com.bumptech.glide.Glide
import com.careavatar.core_model.CommunityPostDatalist
import com.careavatar.core_utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class HightLightPreviewActivity : BaseActivity() {
    private lateinit var binding: ActivityHightLightPreviewBinding
    private val viewModel: SocialMeetViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHightLightPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.crossbtn.setOnClickListener {
            finish()
        }

        binding.deletebtn.setOnClickListener {
            deleteEvent()
        }
        setData()

    }
    private fun setData() {
        val item = intent.getParcelableExtra<CommunityPostDatalist>("highLightpost")

        lifecycleScope.launch {
            val currentUserId = userPref.userId.first()
            if (item?.user?.id == currentUserId) {
                binding.deletebtn.visibility = View.VISIBLE
            }else
            {
                binding.deletebtn.visibility = View.GONE
            }
        }


        if (item != null) {

            Glide.with(this)
                .load(item.image?.let { Constants.IMAGE_BASEURL + it } ?: com.careavatar.core_ui.R.drawable.logo)
                .into(binding.postImage)

            binding.title.text = item.title
            binding.description.text = item.description
            binding.locationtext.text = getAddressFromLatLng(
                this,
                item.latitude?.toDoubleOrNull() ?: 0.0,
                item.longitude?.toDoubleOrNull() ?: 0.0
            )
        }
    }



    fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                // You can combine multiple parts of the address if needed
                "${address.thoroughfare ?: ""} ${address.subThoroughfare ?: ""}, " +
                        "${address.locality ?: ""}, ${address.adminArea ?: ""}, ${address.countryName ?: ""}"
            } else {
                "Unknown address"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown address"
        }
    }

    private fun deleteEvent() {
        val eventId = intent.getStringExtra("event_id").toString()
        launchIfInternetAvailable {

            viewModel.hitEventsDelete(eventId)
        }

    }
}