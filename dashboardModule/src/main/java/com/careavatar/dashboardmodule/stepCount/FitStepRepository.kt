package com.careavatar.dashboardmodule.stepCount

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.SensorRequest
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object FitStepRepository {

    private const val TAG = "FitStepRepository"
    private val liveListeners = mutableMapOf<(Int) -> Unit, Any>()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodaySteps(context: Context, options: com.google.android.gms.fitness.FitnessOptions): Result<Int> {
        return try {
            val account = getValidAccount(context, options) ?: return Result.failure(IllegalStateException("User not signed in"))

            val end = ZonedDateTime.now()
            val start = end.toLocalDate().atStartOfDay(end.zone)

            val request = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(start.toInstant().toEpochMilli(), end.toInstant().toEpochMilli(), TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build()

            val response = Fitness.getHistoryClient(context, account).readData(request).await()

            var total = 0
            response.buckets.forEach { bucket ->
                bucket.dataSets.forEach { ds ->
                    ds.dataPoints.forEach { dp ->
                        total += dp.getValue(Field.FIELD_STEPS).asInt()
                    }
                }
            }

            Result.success(total)
        } catch (e: ApiException) {
            Log.e(TAG, "Google Fit API error: ${e.statusCode} - ${e.statusMessage}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting steps: ${e.message}")
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLast7Days(context: Context, options: com.google.android.gms.fitness.FitnessOptions): Result<List<Int>> {
        return try {
            val account = getValidAccount(context, options) ?: return Result.failure(IllegalStateException("User not signed in"))

            val end = ZonedDateTime.now()
            val start = end.minusDays(7)

            val request = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(start.toInstant().toEpochMilli(), end.toInstant().toEpochMilli(), TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build()

            val response = Fitness.getHistoryClient(context, account).readData(request).await()

            val list = mutableListOf<Int>()
            response.buckets.forEach { bucket ->
                var steps = 0
                bucket.dataSets.forEach { ds ->
                    ds.dataPoints.forEach { dp ->
                        steps += dp.getValue(Field.FIELD_STEPS).asInt()
                    }
                }
                list.add(steps)
            }

            Result.success(list)
        } catch (e: ApiException) {
            Log.e(TAG, "Google Fit API error: ${e.statusCode} - ${e.statusMessage}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting weekly steps: ${e.message}")
            Result.failure(e)
        }
    }

    fun getValidAccount(context: Context, options: com.google.android.gms.fitness.FitnessOptions) =
        GoogleSignIn.getAccountForExtension(context, options).takeIf { GoogleSignIn.hasPermissions(it, options) }

    // ---------------- Live Steps ----------------
    fun startLiveStepListener(context: Context, account: com.google.android.gms.auth.api.signin.GoogleSignInAccount, listener: (Int) -> Unit) {
        val sensorRequest = SensorRequest.Builder()
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setSamplingRate(1, TimeUnit.SECONDS)
            .build()

        val registration = Fitness.getSensorsClient(context, account)
            .add(sensorRequest) { dataPoint ->
                for (field in dataPoint.dataType.fields) {
                    listener(dataPoint.getValue(field).asInt())
                }
            }

        liveListeners[listener] = registration
    }

    fun removeLiveStepListener(listener: (Int) -> Unit) {
        val registration = liveListeners.remove(listener) ?: return
        // Optionally: call Fitness.getSensorsClient(context, account).remove(registration)
    }
}
