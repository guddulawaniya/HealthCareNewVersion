package com.asyscraft.alzimer_module.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.Patientlist

class AdapterPatientList(
    private val context: Context,
    private val datalist: List<Patientlist>,
    private val listener: OnPatientClickListener
) : RecyclerView.Adapter<AdapterPatientList.ViewHolderClass>() {

    interface OnPatientClickListener {
        fun onPatientClick(position: Int, patient: Patientlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.patientlistlayout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int = datalist.size

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val item = datalist[position]
        holder.recently.text = item.fullName ?: "No name"

        holder.itemView.setOnClickListener {
            listener.onPatientClick(position, item)
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recently: TextView = itemView.findViewById(R.id.text)
    }
}
