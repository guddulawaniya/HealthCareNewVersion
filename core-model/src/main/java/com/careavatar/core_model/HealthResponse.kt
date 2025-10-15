package com.careavatar.core_model

class HealthResponse : ArrayList<HealthResponse.HealthResponseItem>(){
    data class HealthResponseItem(
        val __v: Int,
        val _id: String,
        val dashBoardItemIcon: String,
        val name: String
    )
}