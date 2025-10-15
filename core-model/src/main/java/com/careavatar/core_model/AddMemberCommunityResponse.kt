package com.careavatar.core_model

data class AddMemberCommunityResponse(
    val communityId: String,
    val usersNotInApp: List<UsersNotInApp>,
    val usersInApp: List<UsersInApp>,
)

data class UsersNotInApp(
    val phoneNumber: String,
    val status: String,
)

data class UsersInApp(
    val _id: String?,
    val name: String,
    val avatar: String?,
    val phoneNumber: String?,
    val userId: String?,
    val email: String?,
    var status: Boolean,
)

data class AddMemberCommunityRequest(
    val communityId:String,
    val contacts:ArrayList<String>
)


data class AddToCommunityRequest(
    val communityId:String,
    val userId:ArrayList<String>
)