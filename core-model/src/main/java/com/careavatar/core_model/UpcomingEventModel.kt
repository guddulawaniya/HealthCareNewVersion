package com.careavatar.core_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class UpcomingEventModel(
    val message: String,
    val count: Long,
    val events: List<UpcomingTodayEventList>,
)

@Parcelize

data class UpcomingTodayEventList(
    val _id: String,
    val communityId: CommunityId,
    val creatorId: CreatorId,
    val title: String,
    val description: String,
    val latitude: String,
    val longitude: String,
    val location: String,
    val eventLink: String,
    val eventMode: String,
    val eventDate: String,
    val eventTime: String,
    val visibility: String,
    val attachment: List<String>,
    val notifiedMembers: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
): Parcelable

@Parcelize
data class CommunityIdUpComing(
    val _id: String,
    val category: String,
    val name: String,
    val communityLogo: String,
): Parcelable


@Parcelize
data class CreatorId(
    val _id: String,
    val phoneNumber: String,
    val avatar: String,
    val email: String,
    val name: String,
): Parcelable




data class Sender(
    val _id: String,
    val phoneNumber: String,
    val avatar: String,
    val email: String,
    val name: String,
)





