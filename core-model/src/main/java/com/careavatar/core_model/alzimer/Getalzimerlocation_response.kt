package com.careavatar.core_model.alzimer

data class Getalzimerlocation_response(
    val success: Boolean,
    val data: UserData,
    val msg: String
)
data class UserData(
    val id: String,
    val name: String,
    val phone: String,
    val location: String,
    val latitude: Double,
    val image: String,
    val longitude: Double,
    val type: String
)