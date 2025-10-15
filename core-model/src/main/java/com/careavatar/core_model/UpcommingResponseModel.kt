package com.careavatar.core_model


data class UpcommingResponseModel(
    val success: Boolean,
    val msg: String,
    val classes: List<Class>,
)

data class Class(
    val _id: String,
    val noOfCandidates: Long,
    val time: String,
    val date: String,
    val title: String,
    val description: String,
    val url: String,
    val creator: CreatorClass,
    val user: List<ClassUser>,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
)

data class CreatorClass(
    val _id: String,
    val phoneNumber: String,
    val email: String,
    val name: String,
)

data class ClassUser(
    val _id: String,
    val phoneNumber: String,
    val email: String,
    val name: String,
)
