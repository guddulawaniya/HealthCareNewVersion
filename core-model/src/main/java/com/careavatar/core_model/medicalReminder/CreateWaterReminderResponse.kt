package com.careavatar.core_model.medicalReminder


data class CreateWaterReminderRequest(
   val isWaterRemainder: Boolean,
   val startDate: String,
   val targetWaterIntake: String,
   val intervalTime: String,
   val BMI: String,
   val perSip: String,
   val user: String,
)

data class MedicalQuestionRequestStatus(
   val isTaken: Boolean,
)


data class CreateWaterReminderResponse(
   val success: Boolean,
   val msg: String,
   val data: WaterReminderData,
)


data class WaterReminderData(
   val startDate: String,
   val targetWaterIntake: String,
   val intervalTime: String,
   val user: String,
   val isBP: Boolean,
   val isSugar: Boolean,
   val BMI: String,
   val isWaterRemainder: Boolean,
   val _id: String,
   val createdAt: String,
   val updatedAt: String,
   val __v: Long,
)