package com.asyscraft.alzimer_module.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.asyscraft.alzimer_module.R
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin

class CustomVisualizerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var barColor: Int = Color.BLACK
    private var barWidth: Float = 4f.dpToPx()
    private var spacing: Float = 2f.dpToPx()
    private var barHeights = FloatArray(64)
    private var targetHeights = FloatArray(64)
    private val smoothingSpeed = 0.2f


    private val paint = Paint().apply {
        color = barColor
        strokeWidth = barWidth
        isAntiAlias = true
    }

    private var showIdleAnimation = true
    private var idleFrame = 0f

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomVisualizerView, 0, 0).apply {
            try {
                barColor = getColor(R.styleable.CustomVisualizerView_bar_color, Color.BLACK)
                barWidth = getDimension(R.styleable.CustomVisualizerView_bar_width, 4f.dpToPx())
                spacing = 2f.dpToPx()
                paint.color = barColor
                paint.strokeWidth = barWidth
            } finally {
                recycle()
            }
        }
    }

    fun updateVisualizer(bytes: ByteArray) {
        showIdleAnimation = false
        val maxBars = min(barHeights.size, bytes.size)
        for (i in 0 until maxBars) {
            val amplitude = abs(bytes[i].toInt()).toFloat() / 128f
            targetHeights[i] = amplitude * height
        }
    }

    fun pauseVisualizer() {
        showIdleAnimation = true
    }

    fun resumeVisualizer() {
        showIdleAnimation = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val totalBars = barHeights.size.coerceAtMost((width / (barWidth + spacing)).toInt())

        for (i in 0 until totalBars) {
            val animatedHeight = barHeights[i]
            val x = i * (barWidth + spacing)
            canvas.drawLine(
                x,
                centerY - animatedHeight / 2f,
                x,
                centerY + animatedHeight / 2f,
                paint
            )
        }
    }

    private val animationRunnable = object : Runnable {
        override fun run() {
            val totalBars = barHeights.size
            if (showIdleAnimation) {
                idleFrame += 0.05f
                for (i in 0 until totalBars) {
//                    val wave = sin(i * 0.3f + idleFrame)
                    val wave = sin(i * 0.3f)
                    targetHeights[i] = ((wave + 1f) / 2f) * height * 0.4f
                }
            }

            var needsInvalidate = false
            for (i in 0 until totalBars) {
                val diff = targetHeights[i] - barHeights[i]
                if (abs(diff) > 1f) {
                    barHeights[i] += diff * smoothingSpeed
                    needsInvalidate = true
                } else {
                    barHeights[i] = targetHeights[i]
                }
            }

            if (needsInvalidate) invalidate()
            postDelayed(this, 16)
        }
    }

    private fun Float.dpToPx(): Float = this * context.resources.displayMetrics.density

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post(animationRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(animationRunnable)
    }
}
