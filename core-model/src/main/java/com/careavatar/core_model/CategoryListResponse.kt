package com.careavatar.core_model

data class CategoryListResponse(
    val status: Boolean,
    val categories: List<Category>,
)

data class Category(
    val _id: String,
    val name: String,
    val image: String,
    val userCount: Long,
    val __v: Long?,
)


data class CategoryListResponsePost(
    val status: Boolean,
    val categories: List<CategoryPost>,
)

data class CategoryPost(
    val id: String,
    val name: String,
    val CategoryImg: String,
    val count: Long,
)
data class GetCategoryRquest(
    val radius : String
)
