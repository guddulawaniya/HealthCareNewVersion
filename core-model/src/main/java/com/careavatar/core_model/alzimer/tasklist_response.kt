package com.careavatar.core_model.alzimer

data class tasklist_response(
    val success: Boolean,
    val msg: String,
    val data: List<TaskItem>
)


data class TaskItem(
    val date: String,
    val tasks: List<TaskDetail>,
    val activities: List<ActivityDetail>
)


data class TaskDetail(
    val _id: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val link: String,
    val category: String,
    val isCompleted: Boolean
)


data class ActivityDetail(
    val _id: String,
    val title: String,
    val key: String,
    val isCompleted: Boolean
)