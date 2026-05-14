package com.example.cinetopia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cinetopia.Fragmentos.FragmentBoletos
import com.example.cinetopia.Fragmentos.FragmentCuenta
import com.example.cinetopia.Fragmentos.FragmentDulceria
import com.example.cinetopia.Fragmentos.FragmentRecompensas
import com.example.cinetopia.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var currentFragment: Fragment? = null

    private lateinit var firebaseStorage: FirebaseStorage
    private var userRole: String = "cliente"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()
        obtenerRolUsuario()
        solicitarTokenFCM()
        pedirPermisosNotificaciones()
        iniciarServicioNotificaciones()

        // Cargar fragment inicial solo si no hay estado guardado
        if (savedInstanceState == null) {
            loadFragment(FragmentInicio())
        }

        // Bottom Navigation
        binding.BottomNV.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Item_inicio -> {
                    loadFragment(FragmentInicio())
                    true
                }
                R.id.Item_cuenta -> {
                    loadFragment(FragmentCuenta())
                    true
                }
                R.id.Item_boletos -> {
                    loadFragment(FragmentBoletos())
                    true
                }
                R.id.Item_dulceria -> {
                    loadFragment(FragmentDulceria())
                    true
                }
                R.id.Item_recompensas -> {
                    loadFragment(FragmentRecompensas())
                    true
                }
                else -> false
            }
        }

        // FAB - Botón de Recompensas (NO abre FragmentCuenta)
        // En el listener del FAB
        binding.FAB.setOnClickListener {
            loadFragment(FragmentRecompensas())
        }

        binding.fabChat.setOnClickListener {
            if (userRole == "soporte" || userRole == "admin") {
                startActivity(Intent(this, SupportDashboardActivity::class.java))
            } else {
                startActivity(Intent(this, ChatSupportActivity::class.java))
            }
        }
    }

    private fun obtenerRolUsuario() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        com.google.firebase.database.FirebaseDatabase.getInstance().reference
            .child("Usuarios").child(uid).child("role")
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    userRole = snapshot.getValue(String::class.java) ?: "cliente"
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
            })
    }

    private fun solicitarTokenFCM() {
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val uid = firebaseAuth.currentUser?.uid
                if (uid != null && token != null) {
                    com.google.firebase.database.FirebaseDatabase.getInstance().reference
                        .child("Usuarios").child(uid).child("fcmToken").setValue(token)
                }
            }
        }
    }

    private fun pedirPermisosNotificaciones() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, permission) != 
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, arrayOf(permission), 101)
            }
        }
    }

    private fun iniciarServicioNotificaciones() {
        val intent = Intent(this, com.example.cinetopia.Utilidades.ChatNotificationService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // Verificar si el fragment actual es el mismo que se quiere cargar
        if (currentFragment?.javaClass == fragment.javaClass) {
            return // No hacer nada si es el mismo fragment
        }

        currentFragment = fragment

        supportFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, fragment)
            .commit()
    }

    private fun comprobarSesion() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, OpcionesLogin::class.java))
            finishAffinity()
        }
    }
}