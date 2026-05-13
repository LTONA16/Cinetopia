package com.example.cinetopia

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinetopia.Adaptadores.ChatAdapter
import com.example.cinetopia.Modelos.ChatMessage
import com.example.cinetopia.databinding.ActivityChatSupportBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ChatSupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatSupportBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var adapter: ChatAdapter
    private var currentUserId: String = ""
    private var chatRoomId: String = ""

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadImage(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""
        
        // El chat de soporte es entre el usuario actual y el "soporte" (admin)
        chatRoomId = "support_$currentUserId"

        setupRecyclerView()
        checkBlockStatus()
        loadMessages()

        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText, "text", null)
                binding.etMessage.setText("")
            }
        }

        binding.btnAttach.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.toolbarChat.setNavigationIcon(R.drawable.ic_exit) // Reusando icono si existe
        binding.toolbarChat.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(currentUserId) { message ->
            showDeleteDialog(message)
        }
        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvMessages.adapter = adapter
    }

    private fun checkBlockStatus() {
        database.child("BlockedUsers").child(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.txtBlocked.visibility = View.VISIBLE
                    binding.layoutInput.visibility = View.GONE
                } else {
                    binding.txtBlocked.visibility = View.GONE
                    binding.layoutInput.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadMessages() {
        database.child("SupportChats").child(chatRoomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = mutableListOf<ChatMessage>()
                for (data in snapshot.children) {
                    val msg = data.getValue(ChatMessage::class.java)
                    if (msg != null) {
                        messageList.add(msg)
                    }
                }
                adapter.setMessages(messageList)
                binding.rvMessages.scrollToPosition(messageList.size - 1)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage(content: String, type: String, imageUrl: String?) {
        val messageId = database.child("SupportChats").child(chatRoomId).push().key ?: return
        val message = ChatMessage(
            id = messageId,
            senderId = currentUserId,
            receiverId = "support",
            message = if (type == "text") content else "",
            timestamp = System.currentTimeMillis(),
            imageUrl = imageUrl,
            type = type
        )
        database.child("SupportChats").child(chatRoomId).child(messageId).setValue(message)
    }

    private fun uploadImage(uri: Uri) {
        val ref = storage.reference.child("chat_images/${UUID.randomUUID()}")
        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { downloadUri ->
                sendMessage("", "image", downloadUri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteDialog(message: ChatMessage) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar mensaje")
            .setMessage("¿Estás seguro de que quieres eliminar este mensaje?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteMessage(message)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteMessage(message: ChatMessage) {
        database.child("SupportChats").child(chatRoomId).child(message.id).child("isDeleted").setValue(true)
        database.child("SupportChats").child(chatRoomId).child(message.id).child("message").setValue("Este mensaje fue eliminado")
        database.child("SupportChats").child(chatRoomId).child(message.id).child("imageUrl").removeValue()
    }
}
