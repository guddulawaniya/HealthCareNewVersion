package com.asyscraft.medical_reminder.utils

import android.graphics.drawable.GradientDrawable
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class RangeColorDecorator(
    private val dates: List<CalendarDay>,
    private val color: Int
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val contains = dates.contains(day)
        if (contains) {
            Log.d("RangeColorDecorator", "Decorating: ${day.date}")
        }
        return contains
    }

    override fun decorate(view: DayViewFacade) {
        // 🟣 Create rounded background with fill color
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = 50f
        drawable.setColor(color)

        // Apply to calendar cell
        view.setBackgroundDrawable(drawable)
        view.addSpan(ForegroundColorSpan(0xFF000000.toInt())) // make text black
    }
}
