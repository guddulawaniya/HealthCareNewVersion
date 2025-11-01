package com.asyscraft.community_module

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.asyscraft.community_module.adpaters.AddMemberCommunityAdapter
import com.asyscraft.community_module.databinding.ActivityAddMemberCommunityBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.AddMemberCommunityRequest
import com.careavatar.core_model.AddToCommunityRequest
import com.careavatar.core_model.Data
import com.careavatar.core_model.UsersInApp
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_service.repository.viewModels.ContactViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.collections.mapNotNull
import kotlin.collections.orEmpty

@AndroidEntryPoint
class AddMemberCommunityActivity : BaseActivity() {

    private lateinit var viewModellocal: ContactViewModel
    var userappList= mutableListOf<UsersInApp>()
    var contactListGlobal: ArrayList<String> = ArrayList()
    private lateinit var binding: ActivityAddMemberCommunityBinding
    private val viewModel : SocialMeetViewmodel by viewModels()
    private lateinit var adapter: AddMemberCommunityAdapter
    var communityId: String? = null
    var userId: String? = null
    var userSelectedId: ArrayList<String> = ArrayList()

    companion object {
        const val CONTACT_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemberCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.btnBack.setOnClickListener { finish() }

        if (intent.getBooleanExtra("AddMember",false)){
            communityId = intent.getStringExtra("communityId").toString()
            binding.includeBtn.buttonNext.text = "done"
            binding.includeBtn.buttonNext.setOnClickListener { finish() }
        }else
        {
            val array = intent.getParcelableExtra<Data>("communityData")
            communityId = array?._id


            binding.includeBtn.buttonNext.setOnClickListener {
                val intent = Intent(this, CommunityCreatedActivity::class.java).apply {
                    intent.putExtra("communityData",array)
                }
                startActivity(intent)

            }
        }


        lifecycleScope.launch {
            userId = userPref.userId.first()
        }



        requestContactPermission()
        viewModellocal = ContactViewModel(application)

        hitShowMember()
        setRecyclerview()
        observer()
        setupSearchFilter()



    }

    private fun setupSearchFilter() {
        binding.customSearchbar.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val filteredCategories = if (query.isEmpty()) {
                    userappList
                } else {
                    userappList.filter { it.name.contains(query, ignoreCase = true) }
                }
                adapter.updateList(filteredCategories.toMutableList())
                loadContacts(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setRecyclerview(){

        adapter = AddMemberCommunityAdapter(this,userId.toString(),userappList,object :AddMemberCommunityAdapter.OnSelectedUsersListener{
            override fun onUserSelected(userId: String) {
                userSelectedId.clear()
                userSelectedId.add(userId)
                hitAddToCommunity()
            }

        })

        binding.memberRecyclerview.adapter = adapter
        binding.memberRecyclerview.layoutManager = LinearLayoutManager(this)

    }

    private fun observer(){
        collectApiResultOnStarted(viewModel.addMemberCommunityResponse){
            userappList.clear()

            val sortedMembers = it.usersInApp
                .filter { member -> member._id != userId } // Exclude current user
                .sortedWith(
                    compareByDescending { member -> member._id == userId }
                )

            userappList.addAll(sortedMembers)

            adapter.updateList(userappList)

        }
    }

    private fun fetchContactNumber() {
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactListGlobal.add(number)


            } while (cursor.moveToNext())

            cursor.close()
        }

        // Optionally, use or display the contactListGlobal
        for (contact in contactListGlobal) {
            Log.d("contactNumber===", contact)
            //Toast.makeText(this, contact, Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACT_PERMISSION_CODE
            )
        } else {
            fetchContactNumber()
        }
    }

    private fun hitShowMember() {
        launchIfInternetAvailable {
            val request = AddMemberCommunityRequest(
                communityId!!,
                contactListGlobal
            )
            viewModel.hitShowMember(request)

        }
    }

    private fun hitAddToCommunity() {
        launchIfInternetAvailable {
            val request = AddToCommunityRequest(
                communityId!!,
                userSelectedId
            )
            viewModel.hitAddToCommunity(request)

        }
    }

    private fun loadContacts(query: String = "") {
        lifecycleScope.launch {
            val excludedNumbers = userappList.orEmpty()
                .mapNotNull { it.phoneNumber }
                .map { it.replace(" ", "").trim() }

//            viewModellocal.getContacts(query, excludedNumbers).collectLatest { pagingData ->
//                contactAdapterInvite.submitData(pagingData)
//            }
        }
    }
}