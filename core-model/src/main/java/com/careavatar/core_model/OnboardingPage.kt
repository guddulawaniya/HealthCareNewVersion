package com.careavatar.core_model

import android.content.Context


data class OnboardingPage(
    val titleRes: Int,
    val descriptionRes: Int,
    val imageRes: Int
) {
    fun getTitle(context: Context) = context.getString(titleRes)
    fun getDescription(context: Context) = context.getString(descriptionRes)
}
