package com.asyscraft.alzimer_module.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val amplitudes = mutableListOf<Int>()
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 8f
        isAntiAlias = true
    }

    fun addAmplitude(amplitude: Int) {
        amplitudes.add(amplitude / 100)
        if (amplitudes.size > 100) amplitudes.removeAt(0)
        invalidate()
    }

    fun clear() {
        amplitudes.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val middle = height / 2f
        val max = amplitudes.maxOrNull() ?: 1
        amplitudes.forEachIndexed { i, amp ->
            val scaled = (amp / max.toFloat()) * middle
            canvas.drawLine(i * 10f, middle - scaled, i * 10f, middle + scaled, paint)
        }
    }
}
