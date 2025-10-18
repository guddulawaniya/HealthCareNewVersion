package com.careavatar.core_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CommunityPostResponse(
    val success: Boolean,
    val msg: String,
    val data: List<CommunityPostDatalist>,
) : Parcelable


@Parcelize
data class CommunityPostDatalist(
    val _id: String,
    val title: String,
    val description: String,
    val latitude: String?,
    val longitude: String?,
    val image: String?,
    val date: String,
    val hobbiesId: HobbiesId?,
    val user: UserdataPost?,
)  : Parcelable


@Parcelize
data class HobbiesId(
    val _id: String,
    val name: String,
    val image: String,
)  : Parcelable

@Parcelize
data class UserdataPost(
    var status: String? = null,
    val id: String,
    val name: String,
    val image: String? = null
)  : Parcelable
