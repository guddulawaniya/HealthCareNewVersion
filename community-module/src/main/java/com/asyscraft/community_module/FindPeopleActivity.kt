package com.asyscraft.community_module

import android.content.Intent
import android.os.Bundle
import com.asyscraft.community_module.databinding.ActivityFindPeopleBinding
import com.careavatar.core_network.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindPeopleActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityFindPeopleBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindPeopleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener { finish() }
        binding.filterimage.setOnClickListener {
            startActivity(Intent(this, PeopleFilterActivity::class.java))
        }

        // Setup map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Called when map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Example: Add a marker in Delhi and move the camera
        val delhi = LatLng(28.6139, 77.2090)
        mMap.addMarker(MarkerOptions().position(delhi).title("Marker in Delhi"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhi, 12f))
    }
}
