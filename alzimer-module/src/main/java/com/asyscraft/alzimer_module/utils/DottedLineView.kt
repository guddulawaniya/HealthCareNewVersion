package com.asyscraft.alzimer_module.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

//class DottedLineView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
//
//    private val paint = Paint().apply {
//        color = Color.BLACK
//        strokeWidth = 5f
//        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted line effect
//    }
//
//    // List to hold start and end points of lines
//    var startPoint: Pair<Float, Float>? = null
//    var endPoint: Pair<Float, Float>? = null
//
//    // Flag to indicate if we are drawing the line
//    private var isDrawing = false
//
//    // Set the points between which we will draw the dotted line
//    fun setPoints(start: Pair<Float, Float>, end: Pair<Float, Float>) {
//        this.startPoint = start
//        this.endPoint = end
//        invalidate() // Redraw the view when points are set
//    }
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                // Set the starting point for the line
//                startPoint = Pair(event.x, event.y)
//                endPoint = startPoint // Initially set the end point to be the start point
//                isDrawing = true
//                invalidate()
//            }
//            MotionEvent.ACTION_MOVE -> {
//                // Update the end point as the user moves their finger
//                endPoint = Pair(event.x, event.y)
//                invalidate() // Redraw the line continuously as the finger moves
//            }
//            MotionEvent.ACTION_UP -> {
//                // Stop drawing and finalize the line position
//                endPoint = Pair(event.x, event.y)
//                isDrawing = false
//                invalidate() // Redraw to finalize the line at the end point
//            }
//        }
//        return true
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        // Draw the dotted line if start and end points are set
//        if (startPoint != null && endPoint != null) {
//            canvas.drawLine(
//                startPoint!!.first,
//                startPoint!!.second,
//                endPoint!!.first,
//                endPoint!!.second,
//                paint
//            )
//        }
//    }
//}








//class DottedLineView1(context: Context, attrs: AttributeSet?) : View(context, attrs) {
//
//    private val paint = Paint().apply {
//        color = Color.BLACK
//        strokeWidth = 5f
//        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted line effect
//    }
//
//    // List to hold all drawn lines
//    private val lines = mutableListOf<Pair<Pair<Float, Float>, Pair<Float, Float>>>()
//
//    // Temporary line while drawing
//    private var currentStartPoint: Pair<Float, Float>? = null
//    private var currentEndPoint: Pair<Float, Float>? = null
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                currentStartPoint = Pair(event.x, event.y)
//                currentEndPoint = currentStartPoint
//                invalidate()
//            }
//            MotionEvent.ACTION_MOVE -> {
//                currentEndPoint = Pair(event.x, event.y)
//                invalidate()
//            }
//            MotionEvent.ACTION_UP -> {
//                currentEndPoint = Pair(event.x, event.y)
//                // Save the drawn line into list
//                if (currentStartPoint != null && currentEndPoint != null) {
//                    lines.add(Pair(currentStartPoint!!, currentEndPoint!!))
//                }
//                // Clear current points
//                currentStartPoint = null
//                currentEndPoint = null
//                invalidate()
//            }
//        }
//        return true
//    }
//
//    // External function to set points (for your TextView dragging)
//    fun setPoints(start: Pair<Float, Float>, end: Pair<Float, Float>, finalize: Boolean = false) {
//        currentStartPoint = start
//        currentEndPoint = end
//        if (finalize) {
//            lines.add(Pair(start, end))
//            currentStartPoint = null
//            currentEndPoint = null
//        }
//        invalidate()
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        // Draw all saved lines
//        for (line in lines) {
//            canvas.drawLine(
//                line.first.first,
//                line.first.second,
//                line.second.first,
//                line.second.second,
//                paint
//            )
//        }
//
//        // Draw current dragging line (if any)
//        if (currentStartPoint != null && currentEndPoint != null) {
//            canvas.drawLine(
//                currentStartPoint!!.first,
//                currentStartPoint!!.second,
//                currentEndPoint!!.first,
//                currentEndPoint!!.second,
//                paint
//            )
//        }
//    }
//}












import android.graphics.DashPathEffect

class DottedLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // List of line points
    private val lines = mutableListOf<Pair<Pair<Float, Float>, Pair<Float, Float>>>()

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 6f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)
        isAntiAlias = true
    }

    fun addPoint(start: Pair<Float, Float>, end: Pair<Float, Float>) {
        lines.add(Pair(start, end))
        invalidate()
    }

    fun clearPoints() {
        lines.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((start, end) in lines) {
            canvas.drawLine(start.first, start.second, end.first, end.second, paint)
        }
    }
}




// map functionality

fun Int.dpToPx(): Int =
    (this * Resources.getSystem().displayMetrics.density).toInt()