package com.careavatar.core_model.alzimer

data class memory_response(
    val success: Boolean,
    val data: List<MemoryItem>
)

data class MemoryItem(
    val _id: String,
    val userId: String,
    val thumbnail: String,
    val title: String,
    val color: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)