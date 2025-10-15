package com.careavatar.core_model


data class RequestChatUserModel(
    val name : String,
    val lastMessage : String,
    val time : String,
    val image : Int,
    val newMessagecount : String
)