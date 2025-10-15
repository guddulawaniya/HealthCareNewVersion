package com.careavatar.userapp

import android.app.Application
import com.careavatar.core_utils.UserPref
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class HealthCareApplication : Application(){
    @Inject lateinit var userPref: UserPref

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            userPref.token.collect { token ->
                token?.let { userPref.setCachedToken(it) }
            }
        }
    }

}
