package com.asyscraft.community_module.adpaters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.databinding.CallHistoryRowLayoutBinding
import com.bumptech.glide.Glide
import com.careavatar.core_model.CallHistoryModel
import com.careavatar.core_utils.Constants
import com.careavatar.core_ui.R as CoreUiR

class CallHistoryListAdapter(
    private val context: Context,
    private val callList: MutableList<CallHistoryModel>,
) : RecyclerView.Adapter<CallHistoryListAdapter.CallViewHolder>() {

    inner class CallViewHolder(val binding: CallHistoryRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        val binding = CallHistoryRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CallViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        val item = callList[position]
        with(holder.binding) {

            tvuserName.text = item.name
            callTime.text = item.time
            statusCall.text = item.status

            // Load profile image safely
            Glide.with(context)
                .load(Constants.IMAGE_BASEURL + item.imageUrl)
                .placeholder(CoreUiR.drawable.add_user_icon)
                .error(CoreUiR.drawable.add_user_icon)
                .into(ivProfile)

            // Determine call type (audio/video)
            if (item.type.equals("Audio", ignoreCase = true)) {
                callTypeIcon.setImageResource(CoreUiR.drawable.call_icon)

                if (item.status.equals("missed", ignoreCase = true)) {
                    statusCall.setTextColor(
                        ContextCompat.getColor(
                            context,
                            CoreUiR.color.hintColor
                        )
                    )
                    statusCall.setCompoundDrawablesWithIntrinsicBounds(
                        CoreUiR.drawable.call_missed_24px, 0, 0, 0
                    )
                } else if (item.status.equals("Incoming", ignoreCase = true)) {
                    statusCall.setTextColor(
                        ContextCompat.getColor(
                            context,
                            CoreUiR.color.hintColor
                        )
                    )
                    statusCall.setCompoundDrawablesWithIntrinsicBounds(
                        CoreUiR.drawable.call_received_24px, 0, 0, 0
                    )
                } else if (item.status.equals("Outgoing", ignoreCase = true)) {
                    statusCall.setTextColor(
                        ContextCompat.getColor(
                            context,
                            CoreUiR.color.hintColor
                        )
                    )
                    statusCall.setCompoundDrawablesWithIntrinsicBounds(
                        CoreUiR.drawable.call_made_24px, 0, 0, 0
                    )
                } else {
                    statusCall.setTextColor(
                        ContextCompat.getColor(
                            context,
                            CoreUiR.color.hintColor
                        )
                    )
                    statusCall.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }

            } else {
                callTypeIcon.setImageResource(CoreUiR.drawable.video_call_icon)

                if (item.status.equals("missed", ignoreCase = true)) {
                    statusCall.setTextColor(
                        ContextCompat.getColor(
                            context,
                            CoreUiR.color.hintColor
                        )
                    )
                    statusCall.setCompoundDrawablesWithIntrinsicBounds(
                        CoreUiR.drawable.missed_video_call_24px, 0, 0, 0
                    )
                } else if (item.status.equals("Missed Video", ignoreCase = true)) {
                    statusCall.setTextColor(
                        ContextCompat.getColor(
                            context,
                            CoreUiR.color.hintColor
                        )
                    )
                    statusCall.setCompoundDrawablesWithIntrinsicBounds(
                        CoreUiR.drawable.call_received_24px, 0, 0, 0
                    )
                } else if (item.status.equals("Outgoing", ignoreCase = true)) {
                    statusCall.setTextColor(
                        ContextCompat.getColor(
                            context,
                            CoreUiR.color.hintColor
                        )
                    )
                    statusCall.setCompoundDrawablesWithIntrinsicBounds(
                        CoreUiR.drawable.call_made_24px, 0, 0, 0
                    )
                } else {
                    statusCall.setTextColor(
                        ContextCompat.getColor(
                            context,
                            CoreUiR.color.hintColor
                        )
                    )
                    statusCall.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }
    }

    override fun getItemCount(): Int = callList.size
}
