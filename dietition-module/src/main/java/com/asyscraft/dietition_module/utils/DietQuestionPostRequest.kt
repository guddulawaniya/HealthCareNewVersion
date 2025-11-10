package com.asyscraft.dietition_module.utils

import com.careavatar.core_model.dietition.DietQuestionPostRequest


object DietFormHolder {
    var dailyroutineId: String? = null
    var professionId: String? = null
    var sleepTime: String? = null
    var wakeTime: String? = null
    var fitnessGoalId: String? = null
    var preexistMedicine: String? = null
    var MedicineId: String? = null
    var SpecifidietId: String? = null
    var activitydataId: String? = null
    var workoutdataId: String? = null
    var pregrancyState: String? = null
    var monthstage: String? = null
    var FamilyHistorydataId: String? = null
    var FoodallergiesId: String? = null
    var FollowspecifidietId: String? = null
    var medicalCondition: Boolean = false
    var consumeAlcohol: Boolean = false
    var selectedMeals: List<String>? = null

    fun toRequest(): DietQuestionPostRequest {
        // Optionally validate before submission
        return DietQuestionPostRequest(
            dailyroutineId = dailyroutineId,
            professionId = professionId,
            sleepTime = sleepTime,
            wakeTime = wakeTime,
            fitnessGoalId = fitnessGoalId,
            preexistMedicalCondition = preexistMedicine,
            medicineId = MedicineId,
            specifidietId = SpecifidietId,
            familyHistorydataId = FamilyHistorydataId,
            foodallergiesId = FoodallergiesId,
            followspecifidietId = FollowspecifidietId,
            activitydataId = activitydataId,
            workoutdataId = workoutdataId,
            pregrancyState = pregrancyState,
            monthstage = monthstage,
            selectedMeals = selectedMeals
        )
    }

    fun clear() {
        dailyroutineId = null
        professionId = null
        sleepTime = null
        wakeTime = null
        fitnessGoalId = null
        preexistMedicine = null
    }
}
