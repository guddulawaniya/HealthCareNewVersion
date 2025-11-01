package com.asyscraft.alzimer_module.utils

import android.app.AlertDialog
import android.content.Context
import com.asyscraft.alzimer_module.R

object androidExtension {

    fun alertBox(message: String, context: Context) {
        var alertDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        builder.setIcon(com.careavatar.core_ui.R.drawable.logo)
        builder.setTitle("HealthCare")
        builder.setMessage(message)
        alertDialog?.window?.attributes?.windowAnimations = R.style.Fade

        builder.setPositiveButton("ok") { dialogInterface, which ->
            alertDialog!!.dismiss()
        }
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.show()
    }

}