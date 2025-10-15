package com.careavatar.core_model


data class RecentJoinMemberList(
    val message: String,
    val radius: Long,
    val totalCount: Long,
    val notificationCount: Long,
    val groupedByHobbies: List<GroupedByHobby>,
)

data class GroupedByHobby(
    val hobbyId: String,
    val hobbyName: String,
    val image: String,
    val count: Long,
)
