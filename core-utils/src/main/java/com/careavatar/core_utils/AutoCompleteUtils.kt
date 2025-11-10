package com.careavatar.core_utils

import android.annotation.SuppressLint
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.careavatar.core_ui.R

object AutoCompleteUtils {

    /**
     * Sets up an AutoCompleteTextView with the provided data list.
     * Automatically shows dropdown on first click or focus.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun setupAutoComplete(autoCompleteTextView: AutoCompleteTextView, data: List<String>) {
        val adapter = ArrayAdapter(
            autoCompleteTextView.context,
            R.layout.dropdown_item,
            data
        )
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1

        // ✅ Show dropdown immediately when focused
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) autoCompleteTextView.showDropDown()
        }

        // ✅ Also show on touch
        autoCompleteTextView.setOnTouchListener { _, _ ->
            autoCompleteTextView.showDropDown()
            false
        }
    }
}
