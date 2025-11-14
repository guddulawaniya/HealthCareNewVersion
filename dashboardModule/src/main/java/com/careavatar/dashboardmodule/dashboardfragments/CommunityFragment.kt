package com.careavatar.dashboardmodule.dashboardfragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
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
import com.asyscraft.community_module.adpaters.ImageAdapter
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.CommunityPostDatalist
import com.careavatar.core_network.base.BaseFragment
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable1
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


    @RequiresApi(Build.VERSION_CODES.O)
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

        setUpCommunityPostRecyclerview()

        observationAPi()
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

            }
        })
    }

    override fun onResume() {
        super.onResume()
        hitTodayEventList()
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun observationAPi() {

        collectApiResultOnStarted(viewModel.upComingEventList) {
            if (it.events.isEmpty()){
                binding.eventLayout.visibility = View.GONE
            }else{
                binding.eventLayout.visibility = View.VISIBLE

                val item = it.events[0]

                binding.upcomingEventLayout.mainLayout.setOnClickListener {
                    val intent = Intent(requireContext(), EventDetailsActivity::class.java)
                    intent.putExtra("eventId",item._id)
                    startActivity(intent)
                }

                binding.upcomingEventLayout.apply {
                    eventTitle.text = item.title
                    tvDescription.text = item.description
                    eventDate.text = formatDateToReadable1(item.eventDate)


                    arrowBtn.setOnClickListener {
                        if (imageRecylerview.isVisible) {
                            imageRecylerview.visibility = View.GONE
                        }else{
                            imageRecylerview.visibility = View.VISIBLE
                        }

                    }
                    imageRecylerview.layoutManager = LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL,false)
                    imageRecylerview.adapter = ImageAdapter(item.attachment?.toMutableList() ?: ArrayList(),true,{

                    })
                }
            }






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