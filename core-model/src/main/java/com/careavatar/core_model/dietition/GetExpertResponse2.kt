package com.careavatar.core_model.dietition

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetExpertResponse2(
    val experts: ArrayList<Expert>,
    val success: Boolean
) : Parcelable {

    @Parcelize
    data class Expert(
        val averageRating: String? = "0",
        val totalReviews: String? = "0",
        var isSelected : Boolean = false,
        val expert: ExpertInfo,
        val ratingDetails: List<RatingDetail>? = emptyList()
    ) : Parcelable {

        @Parcelize
        data class ExpertInfo(
            val __v: Int,
            val _id: String,
            val address: String,
            val avatar: String,
            val description: String,
            val category: List<Category>,
            val createdAt: String,
            val dob: String? = "",
            val email: String,
            val gender: String,
            val isFirstTime: Boolean,
            val categoryName: String? = "",
            val name: String,
            val phoneNumber: String,
            val status: String,
            val updatedAt: String
        ) : Parcelable {

            @Parcelize
            data class Category(
                val __v: Int,
                val _id: String,
                val categoryIcon: String,
                val name: String
            ) : Parcelable
        }

        @Parcelize
        data class RatingDetail(
            val count: Int,
            val percentage: String,
            val rating: Int
        ) : Parcelable
    }
}
