package com.example.cinetopia.Utilidades

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.cinetopia.ChatSupportActivity
import com.example.cinetopia.Modelos.ChatMessage
import com.example.cinetopia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatNotificationService : Service() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var userRole: String = ""
    private val processedMessages = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        
        createNotificationChannels()
        startMyForeground()
        
        obtenerRolYEscuchar()
    }

    private fun startMyForeground() {
        val channelId = "chat_service_channel"
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Soporte Cinetopia")
            .setContentText("Buscando nuevos mensajes...")
            .setSmallIcon(R.drawable.ic_support_chat)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, notification)
        }
    }

    private fun obtenerRolYEscuchar() {
        val uid = auth.currentUser?.uid ?: return
        database.child("Usuarios").child(uid).child("role")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userRole = snapshot.getValue(String::class.java) ?: "cliente"
                    iniciarEscuchaMensajes(uid)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun iniciarEscuchaMensajes(uid: String) {
        if (userRole == "soporte" || userRole == "admin") {
            // Escuchar TODOS los chats para el soporte
            database.child("SupportChats").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    escucharMensajesEnChat(snapshot.key ?: "")
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        } else {
            // Escuchar solo el chat del cliente
            escucharMensajesEnChat("support_$uid")
        }
    }

    private fun escucharMensajesEnChat(chatId: String) {
        val currentUid = auth.currentUser?.uid ?: return
        database.child("SupportChats").child(chatId).limitToLast(1)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(ChatMessage::class.java)
                    if (message != null && message.senderId != currentUid && !message.isDeleted) {
                        // Evitar duplicados y mensajes antiguos
                        if (!processedMessages.contains(message.id)) {
                            processedMessages.add(message.id)
                            if (System.currentTimeMillis() - message.timestamp < 30000) {
                                showLocalNotification(message)
                            }
                        }
                    }
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun showLocalNotification(message: ChatMessage) {
        val intent = Intent(this, ChatSupportActivity::class.java)
        
        // Si el receptor es "support", significa que el cliente mandó el mensaje.
        // El chatRoomId que debe abrir el soporte es "support_" + senderId
        if (userRole == "soporte" || userRole == "admin") {
            intent.putExtra("targetUserId", message.senderId)
            intent.putExtra("targetUserName", "Cliente") // Podríamos buscar el nombre real, pero "Cliente" es un buen fallback
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "support_chat_channel")
            .setSmallIcon(R.drawable.ic_support_chat)
            .setContentTitle(if (userRole == "soporte" || userRole == "admin") "Nuevo mensaje de cliente" else "Respuesta de soporte")
            .setContentText(if (message.type == "image") "📷 Foto" else message.message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(message.senderId.hashCode(), notification)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "chat_service_channel",
                "Servicio de Chat",
                NotificationManager.IMPORTANCE_LOW
            )
            val chatChannel = NotificationChannel(
                "support_chat_channel",
                "Mensajes de Soporte",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
            manager?.createNotificationChannel(chatChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
