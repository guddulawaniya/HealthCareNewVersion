package com.careavatar.core_model


data class HightlightPostResponse(
    val success: Boolean,
    val msg: String,
    val data: List<communityPostdata>,
)

data class communityPostdata(
    val _id: String,
    val title: String,
    val description: String,
    val image: Any?,
    val date: String,
    val latitude: String?,
    val longitude: String?,
    val hobbiesId: CommunityHobbiesId,
    val user: CommunityPostUser,
)

data class CommunityHobbiesId(
    val _id: String,
    val name: String,
    val image: String,
)

data class CommunityPostUser(
    val status: String,
    val id: String,
    val name: String,
    val image: Any?,
)

data class DeleteHighLightPostResponse(
    val success: Boolean,
    val msg: String,
)


