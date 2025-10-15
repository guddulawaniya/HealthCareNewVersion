package com.careavatar.core_model

data class SearchCommunityResponse(
    val communities: ArrayList<Community>
) {
    data class Community(
        val __v: Int,
        val _id: String,
        val communityLogo: String,
        val createdAt: String,
        val creator: String,
        val type: String,
        val hobbies: List<Any>,
        val memberCount: Int,
        val members: List<String>,
        val name: String,
        val updatedAt: String
    )
}