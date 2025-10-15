package com.asyscraft.community_module

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.asyscraft.community_module.databinding.ActivityAddMemberCommunityBinding
import com.asyscraft.community_module.viewModels.SocialMeetViewmodel
import com.careavatar.core_model.AddMemberCommunityRequest
import com.careavatar.core_model.UsersInApp
import com.careavatar.core_network.base.BaseActivity
import com.careavatar.core_service.repository.viewModels.ContactViewModel
import dagger.hilt.android.AndroidEntryPoint
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

    var communityId: String? = null

    companion object {
        const val CONTACT_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemberCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        requestContactPermission()
        viewModellocal = ContactViewModel(application)

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