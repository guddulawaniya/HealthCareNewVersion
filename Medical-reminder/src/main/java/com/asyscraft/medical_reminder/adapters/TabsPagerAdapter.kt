package com.asyscraft.medical_reminder.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.asyscraft.medical_reminder.fragments.AddManuallyFragment
import com.asyscraft.medical_reminder.fragments.ScanMedicineFragment

class TabsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        ScanMedicineFragment(),
        AddManuallyFragment()
    )

    private val titles = listOf("Scan Medicine", "Add Manually")

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun getTitle(position: Int): String = titles[position]
}
