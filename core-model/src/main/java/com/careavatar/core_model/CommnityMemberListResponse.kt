package com.careavatar.core_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class CommnityMemberListResponse :
    ArrayList<CommnityMemberListResponse.CommnityMemberListResponseItem>() {
    data class CommnityMemberListResponseItem(
        val community: Community,
        val memberCount: Int
    ) {
        @Parcelize
        data class Community(
            val _id: String,
            val creatorId: String,
            val category: Category,
            val name: String,
            val type: String,
            val status: Long,
            val members: List<Member>,
            val communityLogo: String?,
            val createdAt: String,
            val updatedAt: String,
            val __v: Long,
            val creators: Creators,
            ) : Parcelable {
            @Parcelize
            data class Creators(
                val _id: String,
                val phoneNumber: String,
                val avatar: String,
                val email: String,
                val name: String,
            ) : Parcelable
            @Parcelize
            data class Category(
                val _id: String,
                val name: String,
                val image: String,
            ): Parcelable

            @Parcelize
            data class Creatorcommunity(
                val _id: String,
                val avatar: String,
                val email: String,
                val name: String,
                val phoneNumber: String
            ) : Parcelable

            @Parcelize
            data class Member(
                val _id: String,
                val avatar: String,
                val email: String,
                val name: String,
                val phoneNumber: String,
                val joinStatus: String?
            ) : Parcelable
        }
    }
}
