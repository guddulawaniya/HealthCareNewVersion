package com.careavatar.core_model.medicalReminder



data class CreatePeriodRequestModel(
    val lastPeriodDate: String,
    val averageCycleLength: Int,
    val periodDuration: String,
    val flowIntensity: String,
)


data class CreatePeriodResponseModel(
    val success: Boolean,
    val msg: String,
    val data: Hormonaldata,
)

data class Hormonaldata(
    val lastPeriodDate: String,
    val averageCycleLength: Long,
    val periodDuration: String,
    val flowIntensity: String,
    val nextPeriodDate: String,
    val month: String,
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
)


data class PeriodGetAllDataResponse(
    val success: Boolean,
    val data: List<DataPeriodDetails>,
    val msg: String,
)

data class DataPeriodDetails(
    val _id: String,
    val labsPeriodDate: String?,
    val averageCycleLength: Long,
    val periodDuration: String,
    val flowIntensity: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Long,
    val lastPeriodDate: String?,
    val nextPeriodDate: String?,
    val month: String?,
)

