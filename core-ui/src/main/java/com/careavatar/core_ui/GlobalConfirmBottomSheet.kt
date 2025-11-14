package com.careavatar.core_ui

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.careavatar.core_ui.R
import com.google.android.material.bottomsheet.BottomSheetDialog

object GlobalConfirmBottomSheet {

    /**
     * Shows a reusable confirmation bottom sheet with dynamic title, description,
     * cancel and confirm actions.
     *
     * @param context The context to show the dialog
     * @param title Title text (e.g. "Delete Event")
     * @param description Description text (e.g. "This event will be permanently deleted...")
     * @param cancelText Optional text for cancel button (default: "Cancel")
     * @param confirmText Optional text for confirm button (default: "Confirm")
     * @param onCancel Click callback for cancel button
     * @param onConfirm Click callback for confirm button
     */
    fun show(
        context: Context,
        title: String,
        description: String,
        cancelText: String = "Cancel",
        confirmText: String = "Confirm",
        onCancel: (() -> Unit)? = null,
        onConfirm: (() -> Unit)? = null
    ): Dialog {
        val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialog)
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.delete_dialog_layout, null)

        val titleText = view.findViewById<TextView>(R.id.titleText)
        val descriptionText = view.findViewById<TextView>(R.id.descriptionText)
        val cancelButton = view.findViewById<TextView>(R.id.cancelText)
        val confirmButton = view.findViewById<TextView>(R.id.deleteBtn)

        // Assign dynamic text
        titleText.text = title
        descriptionText.text = description
        cancelButton.text = cancelText
        confirmButton.text = confirmText

        // Handle clicks
        cancelButton.setOnClickListener {
            onCancel?.invoke()
            dialog.dismiss()
        }

        confirmButton.setOnClickListener {
            onConfirm?.invoke()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
        return dialog
    }
}
