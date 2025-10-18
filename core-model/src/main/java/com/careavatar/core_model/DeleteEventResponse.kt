package com.careavatar.core_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize



data class DeleteEventResponse(
    val message: String,
    val count: Long,
    val events: List<Eventdata>,
)
@Parcelize
data class Eventdata(
    val _id: String,
    val communityId: CommunityId,
    val creatorId: CreatorId,
    val title: String,
    val description: String,
    val eventDate: String,
    val eventTime: String,
    val visibility: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val eventLink: String,
    val eventMode: String,
    val attachment: List<String>?=null,
    val notifiedMembers: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
): Parcelable







