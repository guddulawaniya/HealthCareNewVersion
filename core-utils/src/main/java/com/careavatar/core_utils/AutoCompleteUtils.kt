package com.careavatar.core_utils

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
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

        // Make sure suggestions start showing immediately
        autoCompleteTextView.threshold = 0

        // ✅ Force show dropdown when focused
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                autoCompleteTextView.post {
                    autoCompleteTextView.showDropDown()
                }
            }
        }

        // ✅ Also show dropdown when touched
        autoCompleteTextView.setOnTouchListener { _, _ ->
            if (!autoCompleteTextView.isPopupShowing) {
                autoCompleteTextView.showDropDown()
            }
            false
        }

        // ✅ Ensure filter shows full list when text is empty
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    adapter.filter.filter(null)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}
