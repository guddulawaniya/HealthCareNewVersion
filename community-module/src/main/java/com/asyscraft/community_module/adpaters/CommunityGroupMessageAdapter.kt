package com.asyscraft.community_module.adpaters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.community_module.R
import com.bumptech.glide.Glide
import com.careavatar.core_model.GroupChatMessageModel
import com.careavatar.core_utils.Constants
import com.google.android.material.imageview.ShapeableImageView
import de.hdodenhof.circleimageview.CircleImageView

class CommunityGroupMessageAdapter(
    private val chatList: MutableList<GroupChatMessageModel>,
    private val onEventJoinClick: (GroupChatMessageModel) -> Unit,
    private val onEventConfirmClick: (GroupChatMessageModel, Boolean) -> Unit // true = confirm, false = decline
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SENDER_TEXT = 1
        private const val TYPE_RECEIVER_TEXT = 2
        private const val TYPE_SENDER_MEDIA = 3
        private const val TYPE_RECEIVER_MEDIA = 4
        private const val TYPE_SENDER_EVENT = 5
        private const val TYPE_RECEIVER_EVENT = 6
    }


    override fun getItemViewType(position: Int): Int {
        val message = chatList[position]
        Log.d("TAG", "getItemViewType: ${message.type}")
        return when (message.type) {
            "text" -> if (message.isSender) TYPE_SENDER_TEXT else TYPE_RECEIVER_TEXT
            "image", "video", "audio", "document" ->
                if (message.isSender) TYPE_SENDER_MEDIA else TYPE_RECEIVER_MEDIA
            "event" ->
                if (message.isSender) TYPE_SENDER_EVENT else TYPE_RECEIVER_EVENT
            else -> TYPE_RECEIVER_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SENDER_TEXT -> SenderTextViewHolder(inflater.inflate(R.layout.item_message_text_sender, parent, false))
            TYPE_RECEIVER_TEXT -> ReceiverTextViewHolder(inflater.inflate(R.layout.item_message_text_receiver, parent, false))
            TYPE_SENDER_MEDIA -> SenderMediaViewHolder(inflater.inflate(R.layout.item_message_media_sender, parent, false))
            TYPE_RECEIVER_MEDIA -> ReceiverMediaViewHolder(inflater.inflate(R.layout.item_message_media_receiver, parent, false))
            TYPE_SENDER_EVENT -> SenderEventViewHolder(inflater.inflate(R.layout.item_message_event_sender, parent, false))
            TYPE_RECEIVER_EVENT -> ReceiverEventViewHolder(inflater.inflate(R.layout.item_message_event_receiver, parent, false))
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = chatList[position]
        when (holder) {
            is SenderTextViewHolder -> holder.bind(message)
            is ReceiverTextViewHolder -> holder.bind(message)
            is SenderMediaViewHolder -> holder.bind(message)
            is ReceiverMediaViewHolder -> holder.bind(message)
            is SenderEventViewHolder -> holder.bind(message)
            is ReceiverEventViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount() = chatList.size

    // ------------------------------------------------------
    //  TEXT VIEW HOLDERS
    // ------------------------------------------------------
    inner class SenderTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val message = itemView.findViewById<TextView>(R.id.tvMessage)
        private val time = itemView.findViewById<TextView>(R.id.tvTime)
        fun bind(model: GroupChatMessageModel) {
            message.text = model.message
            time.text = model.messagetime
        }
    }

    inner class ReceiverTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val message = itemView.findViewById<TextView>(R.id.tvMessage)
        private val time = itemView.findViewById<TextView>(R.id.tvTime)
        private val username = itemView.findViewById<TextView>(R.id.tvUserName)
        private val profile = itemView.findViewById<ImageView>(R.id.ivProfile)

        fun bind(model: GroupChatMessageModel) {
            message.text = model.message
            time.text = model.messagetime
            username.text = model.username
            Glide.with(profile.context)
                .load(Constants.IMAGE_BASEURL + model.profileimage)
                .placeholder(com.careavatar.core_ui.R.drawable.profile_1)
                .into(profile)


        }
    }

    // ------------------------------------------------------
    //  MEDIA VIEW HOLDERS (Image/Video/Audio/Document)
    // ------------------------------------------------------
    inner class SenderMediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val mediaPreview = itemView.findViewById<ImageView>(R.id.ivMedia)
//        private val time = itemView.findViewById<TextView>(R.id.tvTime)
//
        fun bind(model: GroupChatMessageModel) {
//            time.text = model.messagetime
//            Glide.with(mediaPreview.context)
//                .load(model.message)
//                .placeholder(R.drawable.file_placeholder)
//                .into(mediaPreview)
        }
    }

    inner class ReceiverMediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val mediaPreview = itemView.findViewById<ImageView>(R.id.ivMedia)
//        private val username = itemView.findViewById<TextView>(R.id.tvUserName)
//        private val time = itemView.findViewById<TextView>(R.id.tvTime)
//        private val profile = itemView.findViewById<ImageView>(R.id.ivProfile)
//
        fun bind(model: GroupChatMessageModel) {
//            username.text = model.username
//            time.text = model.messagetime
//            Glide.with(profile.context)
//                .load(model.profileimage)
//                .placeholder(R.drawable.profile_placeholder)
//                .into(profile)
//            Glide.with(mediaPreview.context)
//                .load(model.message)
//                .placeholder(R.drawable.file_placeholder)
//                .into(mediaPreview)
        }
    }

    // ------------------------------------------------------
    //  EVENT VIEW HOLDERS
    // ------------------------------------------------------
    inner class SenderEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventMainImage = itemView.findViewById<ShapeableImageView>(R.id.eventMainImage)
        private val title = itemView.findViewById<TextView>(R.id.tvEventTitle)
        private val eventDateTv = itemView.findViewById<TextView>(R.id.eventDateTv)
        private val eventTimeDuration = itemView.findViewById<TextView>(R.id.eventTimeDuration)
        private val modeType = itemView.findViewById<TextView>(R.id.modeType)
        private val eventTypeIcon = itemView.findViewById<ImageView>(R.id.eventTypeIcon)
        private val meetingLink = itemView.findViewById<TextView>(R.id.meetingLink)
        private val confirmedAttendees = itemView.findViewById<TextView>(R.id.confirmedAttendees)

        fun bind(model: GroupChatMessageModel) {
            title.text = model.event ?: "Event"
//            desc.text = model.message
//            time.text = model.messagetime
//            join.setOnClickListener { onEventJoinClick(model) }
        }
    }

    inner class ReceiverEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventMainImage = itemView.findViewById<ShapeableImageView>(R.id.eventMainImage)
        private val title = itemView.findViewById<TextView>(R.id.tvEventTitle)
        private val eventDateTv = itemView.findViewById<TextView>(R.id.eventDateTv)
        private val eventTimeDuration = itemView.findViewById<TextView>(R.id.eventTimeDuration)
        private val modeType = itemView.findViewById<TextView>(R.id.modeType)
        private val eventTypeIcon = itemView.findViewById<ImageView>(R.id.eventTypeIcon)
        private val meetingLink = itemView.findViewById<ImageView>(R.id.meetingLink)
        private val confirmButton = itemView.findViewById<TextView>(R.id.confirm_button)
        private val decline = itemView.findViewById<TextView>(R.id.decline)
        private val senderName = itemView.findViewById<TextView>(R.id.sender_name)
        private val senderImage = itemView.findViewById<CircleImageView>(R.id.sender_image)
        private val tvTime = itemView.findViewById<TextView>(R.id.tvTime)

        fun bind(model: GroupChatMessageModel) {
            title.text = model.event ?: "Event"
//            desc.text = model.message
//            time.text = model.messagetime
//
//            confirm.setOnClickListener { onEventConfirmClick(model, true) }
//            decline.setOnClickListener { onEventConfirmClick(model, false) }
        }
    }
}
