package com.example.cinetopia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cinetopia.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        // Cargar fragment inicial
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, FragmentInicio())
                .commit()
        }

        // Bottom Navigation
        binding.BottomNV.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.BottomNV -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FragmentContainer, FragmentInicio())
                        .commit()
                    true
                }
                // Agrega más casos según tu menú
                else -> false
            }
        }

        binding.FAB.setOnClickListener {
            // Acción del botón flotante
        }
    }

    private fun comprobarSesion() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, OpcionesLogin::class.java))
            finishAffinity()
        }
    }
}