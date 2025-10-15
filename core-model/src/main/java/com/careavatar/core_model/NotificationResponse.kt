package com.careavatar.core_model

class NotificationResponse : ArrayList<NotificationResponse.NotificationResponseItem>(){
    data class NotificationResponseItem(
        val __v: Int,
        val _id: String,
        val communityId: CommunityId,
        val createdAt: String,
        val joinId: JoinId,
        val message: String,
        val read: Boolean,
        val recipient: Recipient,
        val sender: Sender,
        val type: String
    ) {
        data class CommunityId(
            val __v: Int,
            val _id: String,
            val communityLogo: String,
            val createdAt: String,
            val creator: String,
            val hobbies: List<String>,
            val members: List<String>,
            val name: String,
            val updatedAt: String
        )
    
        data class JoinId(
            val __v: Int,
            val _id: String,
            val communityId: String,
            val createdAt: String,
            val status: String,
            val userId: String
        )
    
        data class Recipient(
            val DonateItemList: List<String>,
            val __v: Int,
            val _id: String,
            val address: String,
            val avatar: String,
            val communityMessages: List<Any>,
            val communityPageFirstTime: Boolean,
            val dob: String,
            val donatePageFirstTime: Boolean,
            val email: String,
            val gender: String,
            val hobbies: List<String>,
            val isFirstTime: Boolean,
            val name: String,
            val phoneNumber: String,
            val receivedMessages: List<String>,
            val recentItems: List<RecentItem>,
            val sendMessages: List<String>,
            val subscriptions: List<Any>
        ) {
            data class RecentItem(
                val _id: String,
                val itemId: String,
                val itemType: String
            )
        }
    
        data class Sender(
            val DonateItemList: List<String>,
            val __v: Int,
            val _id: String,
            val address: String,
            val avatar: String,
            val communityMessages: List<Any>,
            val communityPageFirstTime: Boolean,
            val dob: String,
            val donatePageFirstTime: Boolean,
            val email: String,
            val gender: String,
            val hobbies: List<String>,
            val isFirstTime: Boolean,
            val name: String,
            val phoneNumber: String,
            val receivedMessages: List<String>,
            val recentItems: List<RecentItem>,
            val sendMessages: List<String>,
            val subscriptions: List<Any>
        ) {
            data class RecentItem(
                val _id: String,
                val itemId: String,
                val itemType: String
            )
        }
    }
}