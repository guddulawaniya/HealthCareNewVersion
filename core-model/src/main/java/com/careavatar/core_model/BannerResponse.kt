package com.careavatar.core_model

class BannerResponse : ArrayList<BannerResponse.BannerResponseItem>(){
    data class BannerResponseItem(
        val __v: Int,
        val _id: String,
        val dashBoardBanner: String
    )
}