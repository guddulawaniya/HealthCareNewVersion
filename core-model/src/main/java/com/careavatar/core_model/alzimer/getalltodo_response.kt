package com.careavatar.core_model.alzimer

data class getalltodo_response(
    val success: Boolean,
    val data: List<TodoItem>
)


data class TodoItem(
    val _id: String,
    val title: String,
    val patientId: String,
    var status: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)