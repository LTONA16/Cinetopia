package com.example.cinetopia.Adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.Modelos.ChatMessage
import com.example.cinetopia.Modelos.UserRole
import com.example.cinetopia.databinding.ItemChatSessionBinding

class ChatSessionAdapter(
    private val onChatClick: (String, String) -> Unit, // userId, userName
    private val onBlockClick: (String) -> Unit // userId
) : RecyclerView.Adapter<ChatSessionAdapter.SessionViewHolder>() {

    private var sessions = mutableListOf<Pair<UserRole, ChatMessage?>>()

    fun setSessions(newSessions: List<Pair<UserRole, ChatMessage?>>) {
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
        fun bind(session: Pair<UserRole, ChatMessage?>) {
            val user = session.first
            val lastMsg = session.second

            binding.txtUserName.text = if (user.name.isNotEmpty()) user.name else user.email
            binding.txtLastMessage.text = lastMsg?.message ?: "Sin mensajes"

            binding.root.setOnClickListener {
                onChatClick(user.uid, if (user.name.isNotEmpty()) user.name else user.email)
            }
            
            binding.btnBlock.setOnClickListener {
                onBlockClick(user.uid)
            }
        }
    }
}
