package com.careavatar.core_utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPref @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_TOKEN = stringPreferencesKey("user_token")
        private val KEY_MOBILE_NUMBER = stringPreferencesKey("mobile_number")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
    }

    @Volatile private var cachedToken: String? = null

    // Save username
    suspend fun setUserName(name: String) {
        dataStore.edit { prefs -> prefs[KEY_USER_NAME] = name }
    }

    // Observe username
    val userName: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_USER_NAME] }

    // Save token
    suspend fun setToken(token: String) {
        cachedToken = token
        dataStore.edit { prefs -> prefs[KEY_TOKEN] = token }
    }

    fun setCachedToken(token: String) {
        cachedToken = token
    }


    // Instant token getter (non-suspend, for Interceptors)
    fun getCachedToken(): String? = cachedToken

    // Observe token
    val token: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_TOKEN] }

    // Save mobile number
    suspend fun setMobileNumber(number: String) {
        dataStore.edit { prefs -> prefs[KEY_MOBILE_NUMBER] = number }
    }

    // Observe mobile number
    val mobileNumber: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_MOBILE_NUMBER] }

    // Save user id
    suspend fun setUserId(userId: String) {
        dataStore.edit { prefs -> prefs[KEY_USER_ID] = userId }
    }

    // Observe user id
    val userId: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_USER_ID] }

    // Save login state
    suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_IS_LOGGED_IN] = loggedIn }
    }

    suspend fun clearData() {
        cachedToken = null
        dataStore.edit { it.clear() }
    }

    // Observe login state
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { prefs -> prefs[KEY_IS_LOGGED_IN] == true }
}
