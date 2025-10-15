package com.careavatar.core_model

data class UserHobbiesResponse(
    val hobbies: Hobbies,
    val message: String
) {
    data class Hobbies(
        val __v: Int,
        val _id: String,
        val address: String,
        val donatePageFirstTime: Boolean,
        val gender: String,
        val hobbies: List<Hobby>,
        val isFirstTime: Boolean,
        val phoneNumber: String,
        val receivedMessages: List<Any>,
        val recentItems: List<Any>,
        val sendMessages: List<Any>,
        val subscriptions: List<Any>
    ) {
        data class Hobby(
            val _id: String,
            val name: String
        )
    }
}

data class UserHobbiessResquest(
    val userHobbie: ArrayList<String>,
    val address: String

)