package com.careavatar.core_model

data class GroupMessageResponse(
    val messages: List<Message>
) {
    data class Message(
        val __v: Int,
        val _id: String,
        val community: String,
        val createdAt: String,
        val message: String,
        val recipients: List<Recipient>,
        val senderId: SenderId,
        val type: String,
        val eventId: String? = null

    ) {
        data class Recipient(
            val _id: String,
            val messageStatus: String,
            val userId: String
        )

        data class SenderId(
            val _id: String,
            val avatar: String,
            val name: String,
            val phoneNumber: String
        )
    }
}

data class GroupChatMessageModel(
    val type : String,
    val event: String?,
    val messageId: String,
    val from: String,
    val message: String,
    val isSender: Boolean,
    val messagetime: String,
    val profileimage: String,
    val username: String,
    val userId: String,
    val mobilenumber: String
)

