package com.example.cinetopia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cinetopia.OpcionesLogin
import com.example.cinetopia.Fragmentos.FragmentCuenta
import com.example.cinetopia.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

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
                // Agrega más casos según tu menú
                R.id.Item_boletos -> {
                    // TODO: Fragmento de boletos
                    true
                }
                R.id.Item_dulceria -> {
                    // TODO: Fragmento de dulcería
                    true
                }
                else -> false
            }
        }

        // FAB - Botón de Recompensas (NO abre FragmentCuenta)
        binding.FAB.setOnClickListener {
            // TODO: Abrir pantalla de recompensas o mostrar dialog
            // Por ahora puedes dejar vacío o mostrar un Toast
            android.widget.Toast.makeText(
                this,
                "Recompensas próximamente",
                android.widget.Toast.LENGTH_SHORT
            ).show()
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