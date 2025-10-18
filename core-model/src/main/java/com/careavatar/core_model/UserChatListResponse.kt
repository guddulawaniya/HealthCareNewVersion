package com.careavatar.core_model

class UserChatListResponse : ArrayList<UserChatListResponse.UserChatListResponseItem>(){


    data class UserChatListResponseItem(
        val _id: String,
        val avatar: String,
        val group: Group,
        val memberCount: Int,
        val name: String,
        val unreadCount: Int,
        val isFirstTime: Int,
        val requestSent: Int,
        val latestMessage: String,
        val phoneNumber: String,
        val messagetype: String
    ) {

        data class Group(
            val __v: Int,
            val _id: String,
            val category: Category,
            val communityLogo: String,
            val createdAt: String,
            val creator: Creator,
            val hobbies: List<String>,
            val members: List<String>,
            val name: String,
            val type: String,
            val updatedAt: String
        ) {
            data class Category(
                val _id: String,
                val image: String,
                val name: String
            )

            data class Creator(
                val _id: String,
                val avatar: String,
                val name: String,
                val phoneNumber: String
            )
        }
    }




}