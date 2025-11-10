package com.careavatar.core_utils


import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun <T> EditText.setupSearchFilter(
    originalList: MutableList<T>,
    filterCondition: (T, String) -> Boolean,
    onFiltered: (List<T>) -> Unit
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val query = s?.toString()?.trim().orEmpty()

            val filteredList = if (query.isEmpty()) {
                originalList
            } else {
                originalList.filter { item ->
                    filterCondition(item, query)
                }
            }

            onFiltered(filteredList)
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}
