package com.careavatar.dashboardmodule.dashboardfragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.careavatar.core_model.UpcomingTodayEventList
import com.careavatar.core_ui.R
import com.careavatar.dashboardmodule.adapters.TabsPagerAdapter
import com.careavatar.dashboardmodule.adapters.UpcomingEventAdapter
import com.careavatar.dashboardmodule.databinding.FragmentCommunityBinding
import com.asyscraft.community_module.ChatActivity
import com.asyscraft.community_module.CreateCommunityActivity
import com.asyscraft.community_module.CreateHightlightActivity
import com.asyscraft.community_module.EventDetailsActivity
import com.asyscraft.community_module.FindPeopleActivity
import com.asyscraft.community_module.HightLightPreviewActivity
import com.asyscraft.community_module.adpaters.CommunityPostAdapter
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.CommunityPostDatalist
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_utils.ImagePickerManager
import com.careavatar.core_utils.FileUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class CommunityFragment : BaseFragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var adapter: UpcomingEventAdapter
    private lateinit var communityAdapter: CommunityPostAdapter
    private var datalist = mutableListOf<UpcomingTodayEventList>()
    private var communityPostDataList = mutableListOf<CommunityPostDatalist>()
    private val viewModel: SocialMeetViewmodel by viewModels()
    private val tabTitles = arrayOf("Explore", "Joined")
    private var selectDay = "1"
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        ImagePickerManager.init(this, requireActivity())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.chatMessage.setOnClickListener {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }

        binding.findNewPeople.setOnClickListener {
            startActivity(Intent(requireContext(), FindPeopleActivity::class.java))
        }

        binding.addPostbtn.setOnClickListener {
            showImageSourceDialog()
        }

        val adapter = TabsPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        // Attach TabLayout and ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        setUpRecyclerview()
        setUpCommunityPostRecyclerview()

        obervationAPi()
        hitCategoryData()
        tabFeature()



    }

    private fun tabFeature(){
        // 1. Set the default selected tab (if needed)
        binding.tabLayout.getTabAt(0)?.select() // selects the first tab by default

        // 2. Apply margin for the default selected tab
        val initialTabView = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(binding.tabLayout.selectedTabPosition)
        val initialLayoutParams = initialTabView.layoutParams as ViewGroup.MarginLayoutParams
        initialLayoutParams.setMargins(8, 8, 8, 8) // same margin as onTabSelected
        initialTabView.layoutParams = initialLayoutParams

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(tab.position)
                val layoutParams = tabView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(8, 8, 8, 8) // <-- set your margin for selected tab
                tabView.layoutParams = layoutParams
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabView = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(tab.position)
                val layoutParams = tabView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 0, 0, 0) // remove margin when unselected
                tabView.layoutParams = layoutParams
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adjustViewPagerHeight()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        hitTodayEventList()
    }

    fun adjustViewPagerHeight() {
        val recyclerView = binding.viewPager.getChildAt(0) as RecyclerView
        recyclerView.post {
            val currentView = recyclerView.getChildAt(0) ?: return@post
            currentView.measure(
                View.MeasureSpec.makeMeasureSpec(binding.viewPager.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val layoutParams = binding.viewPager.layoutParams
            layoutParams.height = currentView.measuredHeight
            binding.viewPager.layoutParams = layoutParams
        }
    }

    fun showImageSourceDialog() {

        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        val dialogView =
            LayoutInflater.from(context).inflate(
                com.careavatar.dashboardmodule.R.layout.community_post_create_bottom_sheet,
                null
            )
        dialog.setContentView(dialogView)

        // Transparent background to show rounded corners properly
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val createCommunity =
            dialogView.findViewById<CardView>(com.careavatar.dashboardmodule.R.id.createCommunity)
        val createHighlight =
            dialogView.findViewById<CardView>(com.careavatar.dashboardmodule.R.id.createHighlight)
        val crossbtn = dialogView.findViewById<ImageView>(R.id.crossbtn)


        createCommunity.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(requireContext(), CreateCommunityActivity::class.java))

        }
        createHighlight.setOnClickListener {
            dialog.dismiss()
            pickImage()
        }
        crossbtn.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }


    private fun pickImage() {
        ImagePickerManager.showImageSourceDialog(requireContext()) { uri ->
            uri?.let {
                selectedImageUri = it

                startActivity(
                    Intent(requireContext(), CreateHightlightActivity::class.java).putExtra(
                        "uri",
                        selectedImageUri.toString()
                    )
                )
                // Convert Uri → File
                selectedImageFile = FileUtils.uriToFile(requireContext(), it)

            }
        }
    }

    private fun hitCategoryData() {
        launchIfInternetAvailable {
            viewModel.hitGetCategoryList()
        }
    }


    private fun obervationAPi() {

        collectApiResultOnStarted(viewModel.upComingEventList) {
            if (it.events.isEmpty()){
                binding.eventLayout.visibility = View.GONE
            }else{
                binding.eventLayout.visibility = View.VISIBLE
            }

            datalist.clear()
            datalist.addAll(it.events)
            adapter.notifyDataSetChanged()
        }

        collectApiResultOnStarted(viewModel.communityPostResponseList) {
            if (it.data.isEmpty()){
                binding.communityPostRecylerview.visibility = View.GONE
            }else{
                binding.communityPostRecylerview.visibility = View.VISIBLE
            }
            communityPostDataList.clear()
            communityPostDataList.addAll(it.data)
            communityAdapter.notifyDataSetChanged()
        }

        collectApiResultOnStarted(viewModel.recentJoinMemberList) {
            binding.totalNearYou.text = it.totalCount.toString() + " Near You"

        }

    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return sdf.format(currentDate)
    }

    private fun hitTodayEventList() {
        launchIfInternetAvailable {
            viewModel.hitTodayUpcomingEventList(getCurrentDate())
            viewModel.hitCommunityPostList()
            viewModel.hitRecentJoinMember(selectDay)
        }
    }

    private fun setUpRecyclerview() {
        adapter = UpcomingEventAdapter(requireContext(), datalist,listener={
            val intent = Intent(requireContext(), EventDetailsActivity::class.java)
            intent.putExtra("eventId", it._id)
            startActivity(intent)
        },onClickItem = {

        })
        binding.upComingEventRecyclerview.adapter = adapter
        binding.upComingEventRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setUpCommunityPostRecyclerview() {
        communityAdapter = CommunityPostAdapter(requireContext(), communityPostDataList, onClick = {
            startActivity(Intent(requireContext(), HightLightPreviewActivity::class.java)
                .putExtra("highLightpost",it)
            )
        })
        binding.communityPostRecylerview.adapter = communityAdapter
        binding.communityPostRecylerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }


}