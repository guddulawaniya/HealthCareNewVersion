package com.asyscraft.dietition_module.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.graphics.toColorInt
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class TodayDecorator(
    private val backgroundColor: Int = "#FFFFFF".toColorInt(), // pink fill color
    private val borderColor: Int = "#33000000".toColorInt(),     // darker pink border color
    private val borderWidth: Int = 1                            // border thickness (in px)
) : DayViewDecorator {

    private val today = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay): Boolean = day == today

    override fun decorate(view: DayViewFacade) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(backgroundColor)
            setStroke(borderWidth, borderColor)
        }

        // Add some spacing around the circle
        val inset = InsetDrawable(drawable, 5, 5, 5, 5)

        view.setBackgroundDrawable(inset)
        view.addSpan(ForegroundColorSpan(Color.BLACK))
        view.addSpan(StyleSpan(Typeface.BOLD))
    }
}
