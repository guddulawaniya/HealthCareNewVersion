package com.careavatar.core_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class UpdateCommunityResponse(
    val status: Boolean,
    val message: String
)



data class CreateCommunityResponse(
    val status: Boolean,
    val data: Data,
)

@Parcelize
data class Data(
    val _id: String,
    val creatorId: String,
    val category: CategoryCreateCommunity,
    val name: String,
    val type: String,
    val flag: Boolean,
    val status: Long,
    val hobbies: List<String>,
    val members: List<String>,
    val communityLogo: String? =null,
    val messageAt: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
) : Parcelable

@Parcelize
data class CategoryCreateCommunity(
    val _id: String,
    val name: String,
    val image: String,
): Parcelable
