package com.careavatar.core_model

data class GetUserByCategoryResponse(
    val status: Boolean,
    val message: String? = null,
    val users: List<User>? = emptyList()
) {
    data class User(
        val _id: String,
        val avatar: String,
        val distanceInKm: Double,
        val email: String,
        val name: String,
        var status: String?=""
    )
}

data class GetUserByCategoryRequest(
    val cateId : String,
    val radius : Int,
)
