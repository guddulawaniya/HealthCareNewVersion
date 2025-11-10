package com.careavatar.core_model.dietition

data class DietQuestionPostRequest(
    val dailyroutineId: String?,
    val professionId: String?,
    val sleepTime: String?,
    val wakeTime: String?,
    val fitnessGoalId: String?,
    val preexistMedicalCondition: String?,
    val medicineId: String?,
    val specifidietId: String?,
    val familyHistorydataId: String?,
    val foodallergiesId: String?,
    val followspecifidietId: String?,
    val workoutdataId: String?,
    val activitydataId: String?,
    val pregrancyState: String?,
    val monthstage: String?,
    val selectedMeals: List<String>?,
)

data class DietQuestionResponse(
    val success: Boolean,
    val msg: String,
)
