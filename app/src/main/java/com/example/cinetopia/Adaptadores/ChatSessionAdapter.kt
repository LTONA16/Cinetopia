package com.example.cinetopia.Adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.Modelos.ChatMessage
import com.example.cinetopia.Modelos.UserRole
import com.example.cinetopia.R
import com.example.cinetopia.databinding.ItemChatSessionBinding

class ChatSessionAdapter(
    private val onChatClick: (String, String) -> Unit, // userId, userName
    private val onLongClick: (UserRole, Boolean) -> Unit // user, isBlocked
) : RecyclerView.Adapter<ChatSessionAdapter.SessionViewHolder>() {

    private var sessions = mutableListOf<Triple<UserRole, ChatMessage?, Boolean>>()

    fun setSessions(newSessions: List<Triple<UserRole, ChatMessage?, Boolean>>) {
        sessions = newSessions.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemChatSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    override fun getItemCount(): Int = sessions.size

    inner class SessionViewHolder(private val binding: ItemChatSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(session: Triple<UserRole, ChatMessage?, Boolean>) {
            val user = session.first
            val lastMsg = session.second
            val isBlocked = session.third

            binding.txtUserName.text = if (user.name.isNotEmpty()) user.name else user.email
            
            val lastMessageText = when {
                lastMsg == null -> "Sin mensajes"
                lastMsg.isDeleted -> "Mensaje eliminado"
                lastMsg.type == "image" -> "📷 Foto"
                else -> lastMsg.message
            }
            binding.txtLastMessage.text = lastMessageText

            if (isBlocked) {
                binding.root.alpha = 0.5f
                binding.txtUserName.append(" (Bloqueado)")
            } else {
                binding.root.alpha = 1.0f
            }

            binding.root.setOnClickListener {
                onChatClick(user.uid, if (user.name.isNotEmpty()) user.name else user.email)
            }
            
            binding.root.setOnLongClickListener {
                onLongClick(user, isBlocked)
                true
            }
        }
    }
}
