package com.asyscraft.alzimer_module.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager

class SavedPrefManager(var context: Context) {

    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor


    private fun getBooleanValue(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }


    private fun setBooleanValue(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    private fun getStringValue(key: String): String? {
        return preferences.getString(key, "")
    }


    private fun setStringValue(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }


    private fun getIntValue(key: String): Int {
        return preferences.getInt(key, 0)
    }


    private fun setIntValue(key: String, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }


    fun getLongValue(key: String?): Long {
        return preferences.getLong(key, 0L)
    }


    fun setLongValue(key: String?, value: Long) {
        editor.putLong(key, value)
        editor.commit()
    }

    fun removeFromPreference(key: String?) {
        editor.remove(key)
        editor.commit()
    }

    companion object {
        //preferences variables
        const val TOKEN = "TOKEN"
        const val PATIENT_ID = "PATIENT_ID"
        const val CAREGIVER_ID = "CAREGIVER_ID"
        const val PHONENO = "PHONENO"
        const val FCMTOKEN = "FCMTOKEN"
        const val RADIUS = "RADIUS"
        const val USERID = "USERID"
        const val RECUITERID = "RECUITERID"
        const val BOOKMARKSTATUS = "BOOKMARKSTATUS"
        const val ISAPPLIED = "ISAPPLIED"
        const val FCMtoken = "FCMtoken"
        const val UserName = "UserName"
        var IS_LOGIN = "IS_LOGIN"
        var APP_USED = "APP_USED"
        var ON_BOARDING = "ON_BOARDING"
        var LANGUAGE = "LANGUAGE"
        var IS_RESUME_UPLOADED = "IS_RESUME_UPLOADED"



        @SuppressLint("StaticFieldLeak")
        private var instance: SavedPrefManager? = null
        private const val PREF_HIGH_QUALITY = "pref_high_quality"


        fun getInstance(context: Context): SavedPrefManager? {
            if (instance == null) {
                synchronized(SavedPrefManager::class.java) {
                    if (instance == null) {
                        instance = SavedPrefManager(context)
                    }
                }
            }
            return instance
        }


        fun saveStringPreferences(context: Context?, key: String, value: String?): String {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                editor.apply()
            }
            return key
        }

        fun saveIntPreferences(context: Context?, key: String?, value: Int?) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            if (value != null) {
                editor.putInt(key, value)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                editor.apply()
            }
        }

        fun saveFloatPreferences(context: Context?, key: String?, value: Float) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putFloat(key, value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                editor.apply()
            }
        }




        fun saveDoubleValue(context: Context?, key: String, value: Double) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putLong(key, java.lang.Double.doubleToRawLongBits(value))
            editor.commit()
        }

        /*
  This method is used to get string values from shared preferences.
   */
        fun getStringPreferences(context: Context?, key: String?): String? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString(key, "")
        }

        /*
     This method is used to get string values from shared preferences.
      */
        fun getIntPreferences(context: Context?, key: String?): Int {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getInt(key, 0)
        }

        fun savePreferenceBoolean(context: Context?, key: String?, b: Boolean) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putBoolean(key, b)
            editor.apply()
        }

        fun getDoubleValue(context: Context?, key: String): Double {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return java.lang.Double.longBitsToDouble(sharedPreferences.getLong(key, java.lang.Double.doubleToRawLongBits(0.0)))
        }
        /*
      This method is used to get string values from shared preferences.
       */
        fun getBooleanPreferences(context: Context?, key: String?): Boolean {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getBoolean(key, false)
        }

        /**
         * Removes all the fields from SharedPrefs
         */
        fun clearPrefs(context: Context?) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
        }

        fun deleteAllKeys(context: Context?) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()

            editor.remove("TOKEN")
            editor.remove("PATIENT_ID")
            editor.remove("CAREGIVER_ID")
            editor.remove("PHONENO")
            editor.remove("FCMTOKEN")
            editor.remove("RADIUS")
            editor.remove("USERID")
            editor.remove("BOOKMARKSTATUS")
            editor.remove("ISAPPLIED")
            editor.remove("FCMtoken")
            editor.remove("IS_LOGIN")
            editor.remove("APP_USED")

            editor.apply()
        }


    }


    init {
        preferences = context.getSharedPreferences("JOB_SEEKER", Context.MODE_PRIVATE)
        editor = preferences.edit()
    }
}
