package com.careavatar.core_model


data class SearchCommunityResponse(
    val communities: List<Community>,
) {


    data class Community(
        val _id: String,
        val creatorId: String,
        val category: String,
        val name: String,
        val type: String,
        val flag: Boolean,
        val status: Long,
        val hobbies: List<String>,
        val members: List<String>,
        val communityLogo: String,
        val messageAt: String,
        val createdAt: String,
        val updatedAt: String,
        val __v: Long,
        val memberCount: Long,
        val sortField: String,
    )
}
