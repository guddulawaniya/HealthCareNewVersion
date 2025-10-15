package com.careavatar.core_network

import android.util.Log
import com.careavatar.core_utils.UserPref
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(private val userPref: UserPref) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = userPref.getCachedToken()

        Log.d("AuthInterceptor", "Token: $token")

        val newRequest = chain.request().newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(newRequest)
    }
}
