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

class SupportDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportDashboardBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: ChatSessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        setupRecyclerView()
        loadChatSessions()

        binding.toolbarDashboard.setNavigationIcon(R.drawable.ic_exit)
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
            onBlockClick = { userId ->
                showBlockDialog(userId)
            }
        )
        binding.rvChatSessions.layoutManager = LinearLayoutManager(this)
        binding.rvChatSessions.adapter = adapter
    }

    private fun loadChatSessions() {
        database.child("SupportChats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sessions = mutableListOf<Pair<UserRole, ChatMessage?>>()
                val totalChats = snapshot.childrenCount
                if (totalChats == 0L) {
                    binding.txtNoChats.visibility = View.VISIBLE
                    adapter.setSessions(emptyList())
                    return
                }
                binding.txtNoChats.visibility = View.GONE

                var processedCount = 0
                for (chatSnapshot in snapshot.children) {
                    val chatId = chatSnapshot.key ?: continue
                    val userId = chatId.replace("support_", "")
                    
                    // Get last message
                    val lastMsg = chatSnapshot.children.lastOrNull()?.getValue(ChatMessage::class.java)

                    // Get User Info
                    database.child("Usuarios").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            val user = userSnapshot.getValue(UserRole::class.java) ?: UserRole(uid = userId, email = "Desconocido")
                            sessions.add(user to lastMsg)
                            processedCount++
                            if (processedCount.toLong() == totalChats) {
                                adapter.setSessions(sessions.sortedByDescending { it.second?.timestamp ?: 0L })
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            processedCount++
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SupportDashboardActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showBlockDialog(userId: String) {
        AlertDialog.Builder(this)
            .setTitle("Bloquear Usuario")
            .setMessage("¿Estás seguro de que quieres bloquear a este usuario del soporte?")
            .setPositiveButton("Bloquear") { _, _ ->
                blockUser(userId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun blockUser(userId: String) {
        database.child("BlockedUsers").child(userId).setValue(true)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario bloqueado", Toast.LENGTH_SHORT).show()
            }
    }
}
