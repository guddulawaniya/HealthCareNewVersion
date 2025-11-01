package com.asyscraft.medical_reminder.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DefaultBackgroundDecorator(
    private val colorInt: Int
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean = true

    override fun decorate(view: DayViewFacade) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(colorInt) // ✅ direct int
        }
        val inset = InsetDrawable(drawable, 5, 5, 5, 5)
        view.setBackgroundDrawable(inset)
        view.addSpan(StyleSpan(Typeface.BOLD))
    }
}

