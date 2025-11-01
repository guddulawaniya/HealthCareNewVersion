package com.asyscraft.alzimer_module.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class DottedLineView1(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted effect
    }

    // List to hold all finalized lines
    private val lines = mutableListOf<Pair<Pair<Float, Float>, Pair<Float, Float>>>()

    // Current line points (only used during draw, not for manual touch)
    private var currentStartPoint: Pair<Float, Float>? = null
    private var currentEndPoint: Pair<Float, Float>? = null

    /**
     * Programmatically set points and draw a line.
     */
    fun setPoints(start: Pair<Float, Float>, end: Pair<Float, Float>, finalize: Boolean = false) {
        currentStartPoint = start
        currentEndPoint = end
        if (finalize) {
            lines.add(Pair(start, end))
            currentStartPoint = null
            currentEndPoint = null
        }
        invalidate()
    }

    /**
     * Disable all touch drawing from user.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Do nothing on touch; line drawing only allowed programmatically
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw finalized lines
        for (line in lines) {
            canvas.drawLine(
                line.first.first, line.first.second,
                line.second.first, line.second.second,
                paint
            )
        }

        // Optionally draw the current temporary line (if needed)
        if (currentStartPoint != null && currentEndPoint != null) {
            canvas.drawLine(
                currentStartPoint!!.first, currentStartPoint!!.second,
                currentEndPoint!!.first, currentEndPoint!!.second,
                paint
            )
        }
    }
}