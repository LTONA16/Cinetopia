package com.example.cinetopia.Adaptadores

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinetopia.Modelos.ChatMessage
import com.example.cinetopia.R
import com.example.cinetopia.databinding.ItemChatMessageBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val currentUserId: String,
    private val onLongClick: (ChatMessage) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var messages = mutableListOf<ChatMessage>()

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

            if (message.isDeleted) {
                binding.txtMessage.text = "Este mensaje fue eliminado"
                binding.txtMessage.alpha = 0.5f
                binding.imgMessage.visibility = View.GONE
            } else {
                binding.txtMessage.alpha = 1.0f
                if (message.type == "image") {
                    binding.txtMessage.visibility = View.GONE
                    binding.imgMessage.visibility = View.VISIBLE
                    Glide.with(binding.root.context).load(message.imageUrl).into(binding.imgMessage)
                } else {
                    binding.txtMessage.visibility = View.VISIBLE
                    binding.imgMessage.visibility = View.GONE
                    binding.txtMessage.text = message.message
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
    }
}
