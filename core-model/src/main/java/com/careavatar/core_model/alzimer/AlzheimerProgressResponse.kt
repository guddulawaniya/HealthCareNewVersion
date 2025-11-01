package com.careavatar.core_model.alzimer

data class AlzheimerProgressResponse(
    val success: Boolean,
     val msg: String,
     val overall: OverallStats,
     val categoryWise: List<CategoryStats>
)

data class OverallStats(
    val totalCorrect: Int,
    val totalWrong: Int,
    val totalSkipped: Int,
    val totalScored: Int,
    val totalPoints: Int,
    val percentageScore: String
)

data class CategoryStats(
    val categoryId: String,
    val categoryName: String,
    val maxPoints: Int,
    val obtainedPoints: Int,
    val percentageScore: String,
    val correctAnswers: Int,
    val totalQuestions: Int
)

