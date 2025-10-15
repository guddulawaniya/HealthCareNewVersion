package com.careavatar.core_model


data class UserDetailsResponse(
    val msg: String,
    val success: Boolean,
    val user: User
) {
    data class User(
        val __v: Int,
        val _id: String,
        val avatar: String,
        var communityPageFirstTime: Boolean,
        val diseasePageFirstTime: Boolean,
        val dob: String,
        val donatePageFirstTime: Boolean,
        val email: String,
        val hobbies: List<Hobby>?=null,
        val fitnessPageFirstTime: Boolean,
        var isChild: Boolean,
        var isFirstTime: Boolean,
        var isParent: Boolean,
        val gender: String,
        val sugar: String,
        val weight: String,
        val height: String,
        val BP: String,
        val isBP: Boolean,
        val isSugar: Boolean,
        val latitude: String,
        val longitude: String,
        val name: String,
        val notificationCount: Int,
        val parentalControlePageFirstTime: Boolean,
        val phoneNumber: String
    )

    data class Hobby(
        val _id: String,
        val name: String,
        val image: String,
    )
}