package com.careavatar.core_model


data class UpdateCommunityResponse(
    val status: Boolean,
    val message: String
)



data class CreateCommunityResponse(
    val status: Boolean,
    val data: Data,
)

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
    val communityLogo: Any?,
    val messageAt: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
)

data class CategoryCreateCommunity(
    val _id: String,
    val name: String,
    val image: String,
)
