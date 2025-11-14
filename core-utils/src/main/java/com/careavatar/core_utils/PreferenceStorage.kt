package com.careavatar.core_utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.careavatar.core_model.UserDetailsResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        private val KEY_RADUIS_Circle = stringPreferencesKey("RaduisCircle")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_DATA = stringPreferencesKey("user_data")

    }


    // ✅ Save full user model
    suspend fun saveUser(user: UserDetailsResponse.User) {
        val userJson = Gson().toJson(user)
        dataStore.edit { prefs ->
            prefs[KEY_USER_DATA] = userJson
        }
    }

    // ✅ Retrieve full user model as Flow
    val user: Flow<UserDetailsResponse.User?> = dataStore.data.map { prefs ->
        prefs[KEY_USER_DATA]?.let { json ->
            Gson().fromJson(json, UserDetailsResponse.User::class.java)
        }
    }

    // ✅ Retrieve once (non-flow, suspend)
    suspend fun getUser(): UserDetailsResponse.User? {
        val prefs = dataStore.data.first()
        val json = prefs[KEY_USER_DATA]
        return json?.let { Gson().fromJson(it, UserDetailsResponse.User::class.java) }
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


   // Save mobile number
    suspend fun setRaduisCircle(number: String) {
        dataStore.edit { prefs -> prefs[KEY_RADUIS_Circle] = number }
    }

    // Observe mobile number
    val raduisCircle: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_RADUIS_Circle] }

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
