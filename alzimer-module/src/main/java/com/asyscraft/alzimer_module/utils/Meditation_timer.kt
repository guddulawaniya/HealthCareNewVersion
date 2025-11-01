package com.asyscraft.alzimer_module.utils

import android.content.Context
import android.os.CountDownTimer
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.asyscraft.alzimer_module.R

class Meditation_timer(
    private val context: Context,
    private val textView: TextView,
    private val totalTimeInMillis: Long = 60_000L,
    private val onFinish: (() -> Unit)? = null
) {
    private var countDownTimer: CountDownTimer? = null
    private var remainingTimeInMillis: Long = totalTimeInMillis
    private var isRunning = false

    fun start() {
        if (isRunning) return // Prevent multiple timers

        isRunning = true

        countDownTimer = object : CountDownTimer(remainingTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInMillis = millisUntilFinished
                val secondsLeft = millisUntilFinished / 1000
                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                textView.text = String.format("%02d:%02d", minutes, seconds)

                if (secondsLeft <= 10) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.red))
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }

            override fun onFinish() {
                isRunning = false
                remainingTimeInMillis = 0L
                textView.text = "00:00"
                textView.setTextColor(ContextCompat.getColor(context, R.color.red))
                onFinish?.invoke()
            }
        }.start()
    }

    fun pause() {
        countDownTimer?.cancel()
        isRunning = false
    }

    fun cancel() {
        countDownTimer?.cancel()
        remainingTimeInMillis = totalTimeInMillis
        isRunning = false
    }

    fun reset() {
        cancel()
        textView.text = formatTime(totalTimeInMillis)
        textView.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun formatTime(timeInMillis: Long): String {
        val secondsLeft = timeInMillis / 1000
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun isRunning(): Boolean = isRunning
}
