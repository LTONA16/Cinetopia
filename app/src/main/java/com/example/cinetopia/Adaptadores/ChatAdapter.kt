package com.example.cinetopia.Adaptadores

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinetopia.Modelos.ChatMessage
import com.example.cinetopia.R
import com.example.cinetopia.databinding.ItemChatMessageBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val currentUserId: String,
    private val onLongClick: (ChatMessage) -> Unit,
    private val onImageClick: (String) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var messages = mutableListOf<ChatMessage>()
    private var mediaPlayer: MediaPlayer? = null
    private var activeAudioId: String? = null
    private var updateHandler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null

    fun setMessages(newMessages: List<ChatMessage>) {
        messages = newMessages.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            val params = binding.layoutMessage.layoutParams as LinearLayout.LayoutParams
            
            if (message.senderId == currentUserId) {
                params.gravity = Gravity.END
                binding.layoutMessage.setBackgroundResource(R.drawable.bg_message_sent)
            } else {
                params.gravity = Gravity.START
                binding.layoutMessage.setBackgroundResource(R.drawable.bg_message_received)
            }
            binding.layoutMessage.layoutParams = params

            // 1. RESET COMPLETELY
            binding.txtMessage.visibility = View.GONE
            binding.imgMessage.visibility = View.GONE
            binding.layoutAudioPlayer.visibility = View.GONE
            
            val imgParams = binding.imgMessage.layoutParams as LinearLayout.LayoutParams
            imgParams.height = 0
            imgParams.width = 0
            imgParams.topMargin = 0
            binding.imgMessage.layoutParams = imgParams

            // 2. CHECK DELETION FIRST
            if (message.isDeleted) {
                binding.txtMessage.visibility = View.VISIBLE
                binding.txtMessage.text = "Este mensaje fue eliminado"
                binding.txtMessage.alpha = 0.5f
                binding.txtMessage.setTextColor(Color.BLACK)
                // Stop audio if this specific message was being played
                if (activeAudioId == message.id) {
                    stopAudio()
                }
            } else {
                binding.txtMessage.alpha = 1.0f
                binding.txtMessage.setTextColor(Color.BLACK)
                
                when (message.type) {
                    "image" -> setupImageUI(message)
                    "audio" -> setupAudioUI(message)
                    else -> setupTextUI(message)
                }
            }

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.txtTimestamp.text = sdf.format(Date(message.timestamp))

            binding.root.setOnLongClickListener {
                if (message.senderId == currentUserId && !message.isDeleted) {
                    onLongClick(message)
                }
                true
            }
        }

        private fun setupTextUI(message: ChatMessage) {
            binding.txtMessage.visibility = View.VISIBLE
            binding.txtMessage.text = message.message
        }

        private fun setupImageUI(message: ChatMessage) {
            binding.imgMessage.visibility = View.VISIBLE
            val density = binding.root.context.resources.displayMetrics.density
            val imgParams = binding.imgMessage.layoutParams as LinearLayout.LayoutParams
            imgParams.height = (200 * density).toInt()
            imgParams.width = (200 * density).toInt()
            imgParams.topMargin = (4 * density).toInt()
            binding.imgMessage.layoutParams = imgParams
            
            Glide.with(binding.root.context)
                .load(message.imageUrl)
                .placeholder(android.R.drawable.progress_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .into(binding.imgMessage)
                
            binding.imgMessage.setOnClickListener {
                if (!message.isDeleted && !message.imageUrl.isNullOrEmpty()) {
                    onImageClick(message.imageUrl!!)
                }
            }
        }

        private fun setupAudioUI(message: ChatMessage) {
            binding.layoutAudioPlayer.visibility = View.VISIBLE
            binding.txtAudioDuration.text = "Play"
            
            if (activeAudioId == message.id && mediaPlayer?.isPlaying == true) {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                startUpdatingSeekBar(message.id)
            } else {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                binding.audioSeekBar.progress = 0
            }

            binding.btnPlayPause.setOnClickListener {
                if (message.isDeleted || message.audioUrl.isNullOrEmpty()) {
                    return@setOnClickListener
                }

                if (activeAudioId == message.id) {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                        stopUpdatingSeekBar()
                    } else {
                        mediaPlayer?.start()
                        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                        startUpdatingSeekBar(message.id)
                    }
                } else {
                    startAudio(message)
                }
            }

            binding.audioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser && activeAudioId == message.id) {
                        mediaPlayer?.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        private fun startAudio(message: ChatMessage) {
            if (message.isDeleted || message.audioUrl.isNullOrEmpty()) return
            
            stopAudio()
            activeAudioId = message.id
            mediaPlayer = MediaPlayer().apply {
                try {
                    setDataSource(message.audioUrl)
                    prepareAsync()
                    setOnPreparedListener {
                        binding.audioSeekBar.max = it.duration
                        it.start()
                        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                        startUpdatingSeekBar(message.id)
                    }
                    setOnCompletionListener {
                        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                        binding.audioSeekBar.progress = 0
                        stopUpdatingSeekBar()
                        activeAudioId = null
                    }
                    setOnErrorListener { _, _, _ ->
                        stopAudio()
                        false
                    }
                } catch (e: Exception) {
                    stopAudio()
                }
            }
            notifyDataSetChanged() // To update other audio items icons
        }

        private fun stopAudio() {
            stopUpdatingSeekBar()
            mediaPlayer?.release()
            mediaPlayer = null
            activeAudioId = null
        }

        private fun startUpdatingSeekBar(id: String) {
            stopUpdatingSeekBar()
            updateRunnable = object : Runnable {
                override fun run() {
                    if (activeAudioId == id && mediaPlayer != null) {
                        try {
                            binding.audioSeekBar.progress = mediaPlayer!!.currentPosition
                            val current = mediaPlayer!!.currentPosition / 1000
                            binding.txtAudioDuration.text = String.format(Locale.getDefault(), "%d:%02d", current / 60, current % 60)
                            updateHandler.postDelayed(this, 1000)
                        } catch (e: Exception) {
                            stopUpdatingSeekBar()
                        }
                    }
                }
            }
            updateHandler.post(updateRunnable!!)
        }

        private fun stopUpdatingSeekBar() {
            updateRunnable?.let { updateHandler.removeCallbacks(it) }
        }
    }
}
