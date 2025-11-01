package com.asyscraft.alzimer_module.utils

import android.content.Context
import android.os.CountDownTimer
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.asyscraft.alzimer_module.R

class Timer(
    private val context: Context,
    private val textView: TextView,
    private val totalTimeInMillis: Long = 60_000L,
    private val onFinishAction: (() -> Unit)? = null,
    private val onFinish: (() -> Unit)? = null // rename this to 'onFinish'
)
{
    private var countDownTimer: CountDownTimer? = null
    private var startTimeMillis: Long = 0L  // <-- Add this line

    fun start() {
        startTimeMillis = System.currentTimeMillis()

        countDownTimer = object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                textView.text = String.format("%02d:%02d", minutes, seconds)

                if (secondsLeft <= 10) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.red))
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            }

            override fun onFinish() {
                textView.text = "00:00"
                textView.setTextColor(ContextCompat.getColor(context, R.color.red))
                onFinish?.invoke()

            }
        }.start()
    }

    fun cancel() {
        countDownTimer?.cancel()
    }

    fun reset() {
        cancel() // Stop any ongoing timer
        textView.text = "01:00" // Reset display to initial time
        textView.setTextColor(ContextCompat.getColor(context, R.color.black)) // Default color
    }

    fun getElapsedTimeInMillis(): Long {
        return System.currentTimeMillis() - startTimeMillis
    }
}