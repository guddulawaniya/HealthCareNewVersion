package com.careavatar.dashboardmodule.stepCount

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.google.android.gms.fitness.FitnessOptions
import kotlinx.coroutines.launch

class StepViewModel(application: Application) : AndroidViewModel(application) {

    private val _todaySteps = MutableLiveData<Result<Int>>()
    val todaySteps: LiveData<Result<Int>> get() = _todaySteps

    private val _last7Days = MutableLiveData<Result<List<Int>>>()
    val last7Days: LiveData<Result<List<Int>>> get() = _last7Days

    private val _liveSteps = MutableLiveData<Int>()
    val liveSteps: LiveData<Int> get() = _liveSteps

    private var totalLiveSteps = 0
    private var liveStepListener: ((Int) -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadHistoricalSteps(options: FitnessOptions) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext

            if (!FitPermissionManager.hasGoogleFitPermission(context)) {
                _todaySteps.postValue(Result.failure(IllegalStateException("Google Fit permission required")))
                _last7Days.postValue(Result.failure(IllegalStateException("Google Fit permission required")))
                return@launch
            }

            val todayResult = FitStepRepository.getTodaySteps(context, options)
            val weeklyResult = FitStepRepository.getLast7Days(context, options)

            _todaySteps.postValue(todayResult)
            _last7Days.postValue(weeklyResult)
        }
    }

    fun startLiveSteps(options: FitnessOptions) {
        val context = getApplication<Application>().applicationContext
        val account = FitStepRepository.getValidAccount(context, options) ?: return

        totalLiveSteps = 0

        liveStepListener = { deltaSteps ->
            totalLiveSteps += deltaSteps
            _liveSteps.postValue(totalLiveSteps)
        }

        FitStepRepository.startLiveStepListener(context, account, liveStepListener!!)
    }

    fun stopLiveSteps() {
        liveStepListener?.let { FitStepRepository.removeLiveStepListener(it) }
        liveStepListener = null
    }
}
