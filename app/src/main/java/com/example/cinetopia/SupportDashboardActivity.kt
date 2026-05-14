package com.example.cinetopia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinetopia.Adaptadores.ChatSessionAdapter
import com.example.cinetopia.Modelos.ChatMessage
import com.example.cinetopia.Modelos.UserRole
import com.example.cinetopia.databinding.ActivitySupportDashboardBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class SupportDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportDashboardBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: ChatSessionAdapter
    private val sessionsMap = mutableMapOf<String, Triple<UserRole, ChatMessage?, Boolean>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        setupRecyclerView()
        loadChatSessions()

        binding.toolbarDashboard.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarDashboard.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ChatSessionAdapter(
            onChatClick = { userId, userName ->
                val intent = Intent(this, ChatSupportActivity::class.java)
                intent.putExtra("targetUserId", userId)
                intent.putExtra("targetUserName", userName)
                startActivity(intent)
            },
            onLongClick = { user, isBlocked ->
                showContextMenu(user, isBlocked)
            }
        )
        binding.rvChatSessions.layoutManager = LinearLayoutManager(this)
        binding.rvChatSessions.adapter = adapter
    }

    private fun showContextMenu(user: UserRole, isBlocked: Boolean) {
        val options = if (isBlocked) {
            arrayOf("Desbloquear Usuario", "Eliminar Chat Permanentemente")
        } else {
            arrayOf("Bloquear Usuario", "Eliminar Chat Permanentemente")
        }

        AlertDialog.Builder(this)
            .setTitle("Opciones: ${user.name.ifEmpty { user.email }}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (isBlocked) unblockUser(user.uid) else blockUser(user.uid)
                    }
                    1 -> {
                        showDeleteConfirmation(user.uid)
                    }
                }
            }
            .show()
    }

    private fun loadChatSessions() {
        // Escuchar cambios en SupportChats
        database.child("SupportChats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    binding.txtNoChats.visibility = View.VISIBLE
                    adapter.setSessions(emptyList())
                    sessionsMap.clear()
                    return
                }
                binding.txtNoChats.visibility = View.GONE

                val currentChatIds = snapshot.children.mapNotNull { it.key }
                
                // Limpiar sesiones que ya no existen
                sessionsMap.keys.retainAll { chatId -> currentChatIds.contains(chatId) }

                for (chatSnapshot in snapshot.children) {
                    val chatId = chatSnapshot.key ?: continue
                    val userId = chatId.replace("support_", "")
                    val lastMsg = chatSnapshot.children.lastOrNull()?.getValue(ChatMessage::class.java)

                    // Solo agregar listeners si no los tenemos o si queremos actualizar datos
                    fetchUserDetails(userId, chatId, lastMsg)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SupportDashboardActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserDetails(userId: String, chatId: String, lastMsg: ChatMessage?) {
        // Obtener datos del usuario
        database.child("Usuarios").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                val user = userSnapshot.getValue(UserRole::class.java)?.apply {
                    if (this.uid.isEmpty()) {
                        this.uid = userId
                    }
                } ?: UserRole(uid = userId, email = "Desconocido")
                
                // Escuchar estado de bloqueo reactivamente
                database.child("BlockedUsers").child(userId).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(blockSnapshot: DataSnapshot) {
                        val isBlocked = blockSnapshot.exists()
                        sessionsMap[chatId] = Triple(user, lastMsg, isBlocked)
                        updateAdapter()
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateAdapter() {
        val sortedList = sessionsMap.values.sortedByDescending { it.second?.timestamp ?: 0L }
        adapter.setSessions(sortedList)
    }

    private fun blockUser(userId: String) {
        database.child("BlockedUsers").child(userId).setValue(true)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario bloqueado", Toast.LENGTH_SHORT).show()
            }
    }

    private fun unblockUser(userId: String) {
        database.child("BlockedUsers").child(userId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario desbloqueado", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmation(userId: String) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Chat")
            .setMessage("¿Estás seguro de que quieres eliminar este chat permanentemente? Se borrarán todos los mensajes e imágenes.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteChatPermanently(userId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteChatPermanently(userId: String) {
        val chatRoomId = "support_$userId"
        val chatRef = database.child("SupportChats").child(chatRoomId)
        
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val storage = FirebaseStorage.getInstance()
                
                for (msgSnapshot in snapshot.children) {
                    val msg = msgSnapshot.getValue(ChatMessage::class.java)
                    // Borrar imágenes
                    if (msg?.type == "image" && !msg.imageUrl.isNullOrEmpty()) {
                        try {
                            val fileRef = storage.getReferenceFromUrl(msg.imageUrl!!)
                            fileRef.delete()
                        } catch (e: Exception) {}
                    }
                    // Borrar audios
                    if (msg?.type == "audio" && !msg.audioUrl.isNullOrEmpty()) {
                        try {
                            val fileRef = storage.getReferenceFromUrl(msg.audioUrl!!)
                            fileRef.delete()
                        } catch (e: Exception) {}
                    }
                }
                
                chatRef.removeValue().addOnSuccessListener {
                    Toast.makeText(this@SupportDashboardActivity, "Chat eliminado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
