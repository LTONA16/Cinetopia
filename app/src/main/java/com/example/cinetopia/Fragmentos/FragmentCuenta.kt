package com.example.cinetopia.Fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cinetopia.EditarPerfil
import com.example.cinetopia.OpcionesLogin
import com.example.cinetopia.R
import com.example.cinetopia.databinding.FragmentCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class FragmentCuenta : Fragment() {

    private var _binding: FragmentCuentaBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context
    private var currentImageResId: Int = R.drawable.pfp1

    // Lista de recursos de imagen disponibles
    private val profileImages = listOf(
        R.drawable.pfp1, R.drawable.pfp2, R.drawable.pfp3, R.drawable.pfp4, R.drawable.pfp5
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
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

        // Establecer imagen aleatoria inicial
        setRandomProfileImage()

        // Mostrar información del usuario
        cargarInformacion()

        // Configurar listeners de las cards
        setupClickListeners()
    }

    private fun setRandomProfileImage() {
        val randomIndex = Random.nextInt(profileImages.size)
        currentImageResId = profileImages[randomIndex]
        binding.ivPerfil.setImageResource(currentImageResId)
    }

    private fun cargarInformacion() {
        val user = firebaseAuth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
            ref.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isAdded) {
                        val nombres = "${snapshot.child("nombres").value}"
                        val email = "${snapshot.child("email").value}"
                        val fechaNac = "${snapshot.child("fecha_nac").value}"
                        val telefono = "${snapshot.child("codigoTelefono").value}"
                        val tiempo = snapshot.child("tiempo").value as? Long ?: 0L

                        val fechaRegistro = if (tiempo != 0L) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            sdf.format(Date(tiempo))
                        } else {
                            "N/A"
                        }

                        binding.tvNombre.text = if (nombres.isNotEmpty() && nombres != "null") nombres else "Usuario"
                        binding.tvEmail.text = if (email != "null") email else ""
                        
                        binding.tvNombresCuenta.text = if (nombres.isNotEmpty() && nombres != "null") nombres else "No especificado"
                        binding.tvEmailCuenta.text = if (email != "null") email else "No especificado"
                        binding.tvFechaNacCuenta.text = if (fechaNac.isNotEmpty() && fechaNac != "null") fechaNac else "No especificada"
                        binding.tvTelefonoCuenta.text = if (telefono.isNotEmpty() && telefono != "null") telefono else "No especificado"
                        binding.tvMiembroDesdeCuenta.text = fechaRegistro

                        if (user.isEmailVerified) {
                            binding.tvEstadoCuenta.text = "Verificado"
                            binding.tvEstadoCuenta.setTextColor(mContext.getColor(android.R.color.holo_green_dark))
                        } else {
                            binding.tvEstadoCuenta.text = "No verificado"
                            binding.tvEstadoCuenta.setTextColor(mContext.getColor(android.R.color.holo_red_dark))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isAdded) {
                        Toast.makeText(mContext, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    private fun setupClickListeners() {
        binding.cardEditarPerfil.setOnClickListener {
            val intent = Intent(mContext, EditarPerfil::class.java)
            intent.putExtra("IMAGEN_RES_ID", currentImageResId)
            startActivity(intent)
        }

        binding.cardMisCompras.setOnClickListener {
            Toast.makeText(mContext, "Mis Compras", Toast.LENGTH_SHORT).show()
        }

        binding.cardNotificaciones.setOnClickListener {
            Toast.makeText(mContext, "Notificaciones", Toast.LENGTH_SHORT).show()
        }

        binding.cardPrivacidad.setOnClickListener {
            Toast.makeText(mContext, "Privacidad y Seguridad", Toast.LENGTH_SHORT).show()
        }

        binding.cardAyuda.setOnClickListener {
            Toast.makeText(mContext, "Ayuda y Soporte", Toast.LENGTH_SHORT).show()
        }

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