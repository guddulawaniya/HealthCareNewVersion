package com.asyscraft.alzimer_module.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asyscraft.alzimer_module.R
import com.careavatar.core_model.alzimer.VoiceItem
import com.careavatar.core_utils.Constants
import com.squareup.picasso.Picasso

class AdapterVoice(
    private val context: Context,
    private val datalist: List<VoiceItem>
) : RecyclerView.Adapter<AdapterVoice.ViewHolderClass>()  {


    private var mediaPlayer: MediaPlayer? = null
    private var playingPosition = -1
    private var isPreparing = false  // NEW FLAG

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.voice_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, @SuppressLint("RecyclerView") position: Int) {
        val item = datalist[position]

        val musicUrl = Constants.IMAGE_BASEURL + item.music

        if (musicUrl.isNullOrEmpty() || !musicUrl.startsWith("http")) {
            Log.e("AdapterVoiceurl", "Invalid music URL: $musicUrl")
            return
        }

        Log.e("AdapterVoiceurl", "correct music URL: $musicUrl")

        Picasso.get()
            .load(Constants.IMAGE_BASEURL  + item.sender.image)
            .placeholder(R.drawable.user_dummy_image) // shown while loading
            .error(R.drawable.user_dummy_image)       // shown if loading fails
            .into(holder.image)




        holder.playcontrol.setOnClickListener {


            if (playingPosition == position && mediaPlayer?.isPlaying == true) {
                // Pause the currently playing
                mediaPlayer?.pause()
                holder.playcontrol.setImageResource(R.drawable.voice_play)
                holder.image.background = null
            } else {
                // Stop any existing media player
                if (mediaPlayer != null) {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
//                    notifyItemChanged(playingPosition) // reset previous playing item



                    val prev = playingPosition
                    playingPosition = -1
                    isPreparing = false
                    notifyItemChanged(prev)
                }

                // Start new playback

                // Set new playing state
                playingPosition = position
                isPreparing = true


                val uri = Uri.parse(musicUrl)

                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, uri)
                    setOnPreparedListener {
                        isPreparing = false
                        it.start()

                        holder.playcontrol.setImageResource(R.drawable.voice__pause)
                        holder.image.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.voice_play_bg)
                    }

                    setOnCompletionListener {
                        playingPosition = -1
                        isPreparing = false
                        holder.playcontrol.setImageResource(R.drawable.voice_play)
                        holder.image.background = null
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }


                    setOnErrorListener { mp, what, extra ->
                        Log.e("MediaPlayer", "Error occurred: what=$what extra=$extra")
                        true
                    }
                    prepareAsync() // Non-blocking
                }

//                playingPosition = position
//                holder.playcontrol.setImageResource(R.drawable.voice__pause)
//                holder.image.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.voice_play_bg)
//
//                mediaPlayer?.setOnCompletionListener {
//                    holder.playcontrol.setImageResource(R.drawable.voice_play)
//                    holder.image.background = null
//                    playingPosition = -1
//                    mediaPlayer?.release()
//                    mediaPlayer = null
//                }
            }



        }




        if (position == 0) {
            holder.recently.visibility = View.VISIBLE
        } else {
            holder.recently.visibility = View.INVISIBLE
        }

        // Maintain state after recycling
        if (playingPosition == position && (mediaPlayer?.isPlaying == true || isPreparing)) {
            holder.playcontrol.setImageResource(R.drawable.voice__pause)
            holder.image.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.voice_play_bg)
        } else {
            holder.playcontrol.setImageResource(R.drawable.voice_play)
            holder.image.background = null
        }
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val recently: TextView = itemView.findViewById(R.id.recently)
        val playcontrol: ImageButton = itemView.findViewById(R.id.play_pause)
    }
}

