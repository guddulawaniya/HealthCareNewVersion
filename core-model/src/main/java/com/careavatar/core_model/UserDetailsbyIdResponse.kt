package com.careavatar.core_model

data class UserDetailsbyIdResponse(
    val success: Boolean,
    val msg: String,
    val data: List<Communitymember>?,
    val user: UserdetailsInfo,
)

data class Communitymember(
    val _id: String,
    val category: String,
    val name: String,
    val type: String,
    val status: Long,
    val creatorId: String,
    val memberCount: String,
    val hobbies: List<Hobby>,
    val members: List<Member>,
    val communityLogo: String?,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
)

data class Hobby(
    val _id: String,
    val name: String,
    val image: String,
)

data class Member(
    val _id: String,
    val avatar: String,
    val email: String,
    val name: String,
)

data class UserdetailsInfo(
    val _id: String,
    val phoneNumber: String,
    val avatar: String,
    val email: String,
    val name: String,
    val status: String,
)



