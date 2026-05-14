package com.example.cinetopia

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinetopia.Adaptadores.ChatAdapter
import com.example.cinetopia.Modelos.ChatMessage
import com.example.cinetopia.databinding.ActivityChatSupportBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*

class ChatSupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatSupportBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var adapter: ChatAdapter
    private var currentUserId: String = ""
    private var targetUserId: String = ""
    private var chatRoomId: String = ""
    private var isSupportMode: Boolean = false

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var isRecording = false
    private var initialX = 0f
    private val SLIDE_CANCEL_THRESHOLD = 200f // Pixels to slide left to cancel

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadImage(it) }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Permiso de audio denegado", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""
        
        targetUserId = intent.getStringExtra("targetUserId") ?: ""
        val targetUserName = intent.getStringExtra("targetUserName") ?: "Soporte"
        
        if (targetUserId.isNotEmpty()) {
            isSupportMode = true
            chatRoomId = "support_$targetUserId"
            binding.toolbarChat.title = "Chat: $targetUserName"
        } else {
            isSupportMode = false
            chatRoomId = "support_$currentUserId"
            binding.toolbarChat.title = "Chat de Soporte"
        }

        setupRecyclerView()
        checkBlockStatus()
        loadMessages()

        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText, "text", null, null, 0)
                binding.etMessage.setText("")
            }
        }

        binding.btnAttach.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        setupVoiceRecording()

        binding.toolbarChat.setNavigationIcon(R.drawable.ic_back) 
        binding.toolbarChat.setNavigationOnClickListener { finish() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupVoiceRecording() {
        binding.btnRecord.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        startRecording()
                        initialX = event.rawX
                        binding.txtSlideToCancel.visibility = View.VISIBLE
                    }
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isRecording) {
                        val diffX = initialX - event.rawX
                        if (diffX > SLIDE_CANCEL_THRESHOLD) {
                            cancelRecording()
                            Toast.makeText(this, "Grabación cancelada", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isRecording) {
                        stopAndSendRecording()
                    }
                    binding.txtSlideToCancel.visibility = View.GONE
                    true
                }
                else -> false
            }
        }
    }

    private fun startRecording() {
        isRecording = true
        audioFile = File(externalCacheDir, "support_voice_${System.currentTimeMillis()}.m4a")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)
            try {
                prepare()
                start()
            } catch (e: Exception) {
                isRecording = false
                Toast.makeText(this@ChatSupportActivity, "Error al iniciar grabación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelRecording() {
        if (!isRecording) return
        isRecording = false
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) {}
        mediaRecorder = null
        audioFile?.delete()
    }

    private fun stopAndSendRecording() {
        if (!isRecording) return
        isRecording = false
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            audioFile?.let { uploadAudio(it) }
        } catch (e: Exception) {
            mediaRecorder = null
        }
    }

    private fun uploadAudio(file: File) {
        val fileName = "chat_audios/${UUID.randomUUID()}.m4a"
        val ref = storage.reference.child(fileName)
        ref.putFile(Uri.fromFile(file)).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { downloadUri ->
                sendMessage("", "audio", null, downloadUri.toString(), 0)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al subir audio: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(currentUserId, { message ->
            showDeleteDialog(message)
        }, { imageUrl ->
            showImageZoomDialog(imageUrl)
        })
        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvMessages.adapter = adapter
    }

    private fun checkBlockStatus() {
        database.child("BlockedUsers").child(if (isSupportMode) targetUserId else currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.txtBlocked.visibility = View.VISIBLE
                        if (isSupportMode) {
                            binding.txtBlocked.text = "Este usuario está bloqueado"
                            binding.layoutInput.visibility = View.VISIBLE
                        } else {
                            binding.txtBlocked.text = "Has sido bloqueado del soporte"
                            binding.layoutInput.visibility = View.GONE
                        }
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

    private fun sendMessage(content: String, type: String, imageUrl: String?, audioUrl: String?, audioDuration: Long) {
        val messageId = database.child("SupportChats").child(chatRoomId).push().key ?: return
        val message = ChatMessage(
            id = messageId,
            senderId = currentUserId,
            receiverId = if (isSupportMode) targetUserId else "support",
            message = if (type == "text") content else "",
            timestamp = System.currentTimeMillis(),
            imageUrl = imageUrl,
            audioUrl = audioUrl,
            audioDuration = audioDuration,
            type = type
        )
        database.child("SupportChats").child(chatRoomId).child(messageId).setValue(message)
    }

    private fun uploadImage(uri: Uri) {
        val ref = storage.reference.child("chat_images/${UUID.randomUUID()}")
        ref.putFile(uri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { downloadUri ->
                sendMessage("", "image", downloadUri.toString(), null, 0)
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
        val updates = HashMap<String, Any?>()
        updates["isDeleted"] = true
        updates["message"] = "Este mensaje fue eliminado"
        updates["imageUrl"] = null
        updates["audioUrl"] = null
        
        database.child("SupportChats").child(chatRoomId).child(message.id).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Mensaje eliminado", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showImageZoomDialog(imageUrl: String) {
        val dialog = android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_image_viewer)

        val photoView = dialog.findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.photoView)
        val btnClose = dialog.findViewById<android.widget.ImageButton>(R.id.btnClose)

        com.bumptech.glide.Glide.with(this)
            .load(imageUrl)
            .into(photoView)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
