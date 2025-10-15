package com.careavatar.core_model

data class AddToCommunityResponse(
    val community: Community,
    val memberCount: Int
) {
    data class Community(
        val __v: Int,
        val _id: String,
        val category: String,
        val communityLogo: String,
        val createdAt: String,
        val creator: Creator,
        val hobbies: List<String>,
        val members: List<Member>,
        val name: String,
        val type: String,
        val updatedAt: String
    ) {
        data class Creator(
            val _id: String,
            val avatar: Any,
            val email: String,
            val name: String,
            val phoneNumber: String
        )

        data class Member(
            val _id: String,
            val avatar: String,
            val email: String,
            val name: String,
            val phoneNumber: String
        )
    }
}

data class DeleteCommunitesResponse(
    val message : String
)