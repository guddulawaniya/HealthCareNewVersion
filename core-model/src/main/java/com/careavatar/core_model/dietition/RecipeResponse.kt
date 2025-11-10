package com.careavatar.core_model.dietition
import com.google.gson.annotations.SerializedName

data class RecipeResponsedetails(
    val success: Boolean,
    val data: Recipedata,
)


data class NutritionData(
    val name: String,
    val percentage: String
)



data class RecipeResponse(
    val success: Boolean,
    val userNeedCalories: Long = 0,
    val totalCalories: Long = 0,
    val data: List<Recipedata>,
)


data class Nutrent(
    val name: String,
    @SerializedName("amount_g")
    val amountG: Double,
)

data class Ingredient(
    val name: String,
    @SerializedName("amount_g")
    val amountG: Long?,
)


data class Recipedata(
    @SerializedName("_id")
    val id: String,
    val type: String,
    @SerializedName("recipe_name")
    val recipeName: String,
    @SerializedName("other_names")
    val otherNames: List<String>,
    val quantity: String,
    val ingredients: List<Ingredient>,
    @SerializedName("ideal_food_habit")
    val idealFoodHabit: List<String>,
    @SerializedName("ideal_time")
    val idealTime: List<String>,
    @SerializedName("restricted_in_disease")
    val restrictedInDisease: List<String>,
    @SerializedName("ideal_for_disease")
    val idealForDisease: List<String>,
    @SerializedName("inflammation_causing")
    val inflammationCausing: Boolean,
    @SerializedName("gi_problem_causing")
    val giProblemCausing: Boolean,
    @SerializedName("workout_time")
    val workoutTime: String?,
    @SerializedName("workout_type")
    val workoutType: Any?,
    @SerializedName("meal_category")
    val mealCategory: MealCategory,
    val notes: String,
    val createdAt: String,
    val updatedAt: String,
    val preptime: String,
    @SerializedName("Video")
    val video: String,
    val image: String,
    @SerializedName("meal_time")
    val mealTime: String,
    @SerializedName("recipe_step")
    val recipeStep: String,
    val nutrents: List<Nutrent>,
    var isInCart: Boolean,
)

data class MealCategory(
    @SerializedName("_id")
    val id: String,
)





