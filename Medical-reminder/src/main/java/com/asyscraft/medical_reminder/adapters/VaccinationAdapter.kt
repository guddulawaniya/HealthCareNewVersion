package com.asyscraft.medical_reminder.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.medical_reminder.databinding.UpcomingVaccinationRowItemBinding
import com.careavatar.core_model.medicalReminder.VaccinationDetailsResponse
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable1
import com.careavatar.core_utils.DateTimePickerUtil.formatDateToReadable2

class VaccinationAdapter(
    var dataList: MutableList<VaccinationDetailsResponse.Data>,
) : RecyclerView.Adapter<VaccinationAdapter.ViewHolder>() {

    class ViewHolder(val binding: UpcomingVaccinationRowItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UpcomingVaccinationRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.binding.tvLevel.text = data.label

        val takenformattedDate = formatDateToReadable2(data.taken_date.toString())
        val nextdateformattedDate = formatDateToReadable2(data.next_date.toString())

        holder.binding.takenDate.text = takenformattedDate
        holder.binding.tvNextDate.text = nextdateformattedDate


//        holder.binding.editbtn.setOnClickListener {
//            onItemClick(position,true)
//        }

//        holder.binding.editbtn.setOnClickListener {
//
//            val popupMenu = PopupMenu(holder.itemView.context, it)
//            popupMenu.menuInflater.inflate(R.menu.deleteupdatemenu, popupMenu.menu)
//
//            popupMenu.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.deletebtn -> {
//                        onItemClick(position,false)
//                        true
//                    }
//
//                    R.id.updateBtn -> {
//                        onItemClick(position,true)
//
//                        true
//                    }
//
//                    else -> false
//                }
//            }
//
//            popupMenu.show()
//        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}
