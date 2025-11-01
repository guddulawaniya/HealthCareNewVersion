package com.careavatar.core_model.medicalReminder

data class CreateMedicalReminderResponse(
    val success: Boolean,
    val message: String,
    val data: DataReminder,
)

data class DataReminder(
    val _id: String,
    val user: String,
    val medicine: String,
    val disease: String,
    val medicineForm: Any?,
    val strength: String,
    val unit: String,
    val stock: Long,
    val relativeNumber: Long,
    val scheduleMode: String,
    val intervalDays: String,
    val status: String,
    val scheduleTimings: List<ScheduleTimingCreate>,
    val startDate: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
)

data class ScheduleTimingCreate(
    val day: String,
    val dosage: String,
    val time: String,
    val _id: String,
)





data class CreateMedicalReminderRequest(
    val medicine: String,
    val medicineForm: String,
    val disease: String,
    val strength: String,
    val unit: String,
    val stock: Int,
    val title: String,
    val scheduleMode: String,
    val intervalDays: String,
    val startDate: String,
    val status: String,
    val scheduleTimings: List<ScheduleTiming>
)
data class ScheduleTiming(
    val dosage: String,
    val time: String,
    val day: String
)