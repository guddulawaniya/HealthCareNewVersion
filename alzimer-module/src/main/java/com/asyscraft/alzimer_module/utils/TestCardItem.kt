package com.asyscraft.alzimer_module.utils

import com.careavatar.core_model.alzimer.ActivityDetail
import com.careavatar.core_model.alzimer.TaskDetail

sealed class TestCardItem {
    data class Task(val task: TaskDetail) : TestCardItem()
    data class Activity(val activity: ActivityDetail) : TestCardItem()
}