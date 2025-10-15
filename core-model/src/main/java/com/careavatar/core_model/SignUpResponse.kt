package com.careavatar.core_model

data class SignUpResponse(
    val msg: String,
    val success: Boolean,
    val user: User
){
    data class User(
        val __v: Int,
        val _id: String,
        val avatar: String,
        val dob: String,
        val email: String,
        val hobbies: List<Any>,
        val isFirstTime: Boolean,
        val name: String,
        val phoneNumber: String,
        val subscriptions: List<Any>
    )
}