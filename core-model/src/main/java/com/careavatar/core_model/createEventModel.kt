package com.careavatar.core_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class EventPostRequestModel(
    val communityId: String,
    val title: String,
    val description: String,
    val eventDate: String,
    val eventTime: String,
    val visibility: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val eventMode: String,
    val eventLink: String,
    val notifiedMembers: List<String>? = null,
)


data class EventPostResponse(
    val message: String,
    val event: Event,
)



data class Event(
    val communityId: String,
    val creatorId: String,
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
    val attachment: List<Any?>,
    val notifiedMembers: List<String>,
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
)



typealias getallEventsList = List<getallEvents>

@Parcelize
data class getallEvents(
    val _id: String,
    val communityId: String,
    val creatorId: CreatorId,
    val title: String,
    val description: String,
    val eventDate: String,
    val eventTime: String,
    val visibility: String,
    val location: String,
    val eventLink: String,
    val eventMode: String,
    val attachment: List<String>?=null,
    val notifiedMembers: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
): Parcelable




data class deleteResponse(
    val message: String,
)



data class GetEventdataById(
    val success: Boolean,
    val data: DataGetEventdataById,
    val msg: String,
)

data class DataGetEventdataById(
    val _id: String,
    val communityId: CommunityId,
    val creatorId: GeteventCreatorId,
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
)

@Parcelize
data class CommunityId(
    val _id: String,
    val name: String,
    val communityLogo: String?=null,
): Parcelable

data class GeteventCreatorId(
    val _id: String,
    val phoneNumber: String,
    val avatar: String,
    val email: String,
    val name: String,
)




data class EventImageUploadReponse(
    val success: Boolean,
    val msg: String,
    val attachments: List<String>,
)

data class DeleteEventRequest(
    val filePath : String
)

