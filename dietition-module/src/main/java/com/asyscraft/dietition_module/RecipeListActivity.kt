package com.asyscraft.dietition_module

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import com.asyscraft.dietition_module.databinding.ActivityRecipeListBinding
import com.asyscraft.dietition_module.fragments.AllRecipeFragment
import com.asyscraft.dietition_module.fragments.PostWorkFragment
import com.asyscraft.dietition_module.fragments.PreWorkOutFragment
import com.careavatar.core_network.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecipeListActivity : BaseActivity() {

    private lateinit var binding: ActivityRecipeListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.btnBack.setOnClickListener { finish() }
        binding.toolbar.tvTitle.text = "Build Your Plan"
        setupTabs()
        setupSearchFilter()
    }

    private fun setupTabs() {

        val tabTitles = listOf("All Recipes", "Pre-Workout", "Post-Workout")

        // Add tabs
        tabTitles.forEach { title ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(title))
        }

        // Load first fragment initially
        loadFragment(AllRecipeFragment())

        // Tab change listener
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadFragment(AllRecipeFragment())
                    1 -> loadFragment(PreWorkOutFragment())
                    2 -> loadFragment(PostWorkFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frameContainer.id, fragment)
            .commit()
    }


    private fun setupSearchFilter() {
        binding.searchBar.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                AllRecipeFragment().setupSearchFilterFragment(s.toString())
                val fragment = supportFragmentManager.findFragmentById(R.id.frameContainer) as? AllRecipeFragment
                fragment?.setupSearchFilterFragment(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}

