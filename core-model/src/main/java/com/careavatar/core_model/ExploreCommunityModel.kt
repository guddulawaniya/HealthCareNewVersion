package com.careavatar.core_model



data class ExploreCommunityModel(
    val success: Boolean,
    val message: String,
    val currentPage: Long,
    val totalPages: Long,
    val totalCommunities: Long,
    val data: List<CommunityData>,
){
    data class CommunityData(
        val _id: String,
        val creatorId: String,
        val category: AllCategory,
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
    )

    data class AllCategory(
        val _id: String,
        val name: String,
        val image: String,
    )
}


