package com.example.cinetopia.Fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cinetopia.OpcionesLogin
import com.example.cinetopia.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth

class FragmentCuenta : Fragment() {

    private var _binding: FragmentCuentaBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        // Mostrar información del usuario
        setupUserInfo()

        // Configurar listeners de las cards
        setupClickListeners()
    }

    private fun setupUserInfo() {
        val user = firebaseAuth.currentUser
        binding.tvNombre.text = user?.displayName ?: "Usuario"
        binding.tvEmail.text = user?.email ?: "correo@ejemplo.com"
    }

    private fun setupClickListeners() {
        // Editar Perfil
        binding.cardEditarPerfil.setOnClickListener {
            Toast.makeText(mContext, "Editar Perfil", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a pantalla de editar perfil
        }

        // Mis Compras
        binding.cardMisCompras.setOnClickListener {
            Toast.makeText(mContext, "Mis Compras", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a pantalla de mis compras
        }

        // Notificaciones
        binding.cardNotificaciones.setOnClickListener {
            Toast.makeText(mContext, "Notificaciones", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a pantalla de notificaciones
        }

        // Privacidad
        binding.cardPrivacidad.setOnClickListener {
            Toast.makeText(mContext, "Privacidad y Seguridad", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a pantalla de privacidad
        }

        // Ayuda
        binding.cardAyuda.setOnClickListener {
            Toast.makeText(mContext, "Ayuda y Soporte", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a pantalla de ayuda
        }

        // Cerrar Sesión
        binding.BtnCerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(mContext, OpcionesLogin::class.java))
            activity?.finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}