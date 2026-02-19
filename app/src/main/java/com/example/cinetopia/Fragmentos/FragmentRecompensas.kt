package com.example.cinetopia.Fragmentos

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinetopia.Adaptadores.RecompensaAdapter
import com.example.cinetopia.Modelos.Recompensa
import com.example.cinetopia.Modelos.UsuarioRecompensas
import com.example.cinetopia.Utilidades.QRCodeGenerator
import com.example.cinetopia.databinding.FragmentRecompensasBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

class FragmentRecompensas : Fragment() {

    private var _binding: FragmentRecompensasBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecompensasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        setupUsuario()
        setupRecompensas()
        setupQRCode()

        binding.btnCompartirQR.setOnClickListener {
            compartirQR()
        }
    }

    private fun setupUsuario() {
        val user = firebaseAuth.currentUser
        val usuarioRecompensas = obtenerDatosUsuario()

        binding.apply {
            tvNombreUsuarioQR.text = user?.displayName ?: usuarioRecompensas.nombre
            tvCodigoUsuario.text = usuarioRecompensas.codigoQR
            tvPuntosActuales.text = usuarioRecompensas.puntosActuales.toString()
            tvNivelUsuario.text = usuarioRecompensas.nivel
            tvMiembroDesde.text = "Miembro desde: ${usuarioRecompensas.miembroDesde}"

            // Calcular progreso al siguiente nivel
            val puntosParaSiguienteNivel = 2000
            val progreso = (usuarioRecompensas.puntosActuales * 100) / puntosParaSiguienteNivel
            progressBarNivel.progress = progreso
            tvPuntosRestantes.text = "${puntosParaSiguienteNivel - usuarioRecompensas.puntosActuales} pts más"
        }
    }

    private fun setupQRCode() {
        val user = firebaseAuth.currentUser
        val usuarioRecompensas = obtenerDatosUsuario()

        // Contenido del QR (puede incluir ID de usuario, email, código, etc.)
        val contenidoQR = """
            {
                "tipo": "CINETOPIA_MEMBER",
                "codigo": "${usuarioRecompensas.codigoQR}",
                "email": "${user?.email ?: usuarioRecompensas.email}",
                "puntos": ${usuarioRecompensas.puntosActuales},
                "nivel": "${usuarioRecompensas.nivel}"
            }
        """.trimIndent()

        // Generar el código QR
        val qrBitmap = QRCodeGenerator.generateQRCode(contenidoQR, 500, 500)

        if (qrBitmap != null) {
            binding.ivCodigoQR.setImageBitmap(qrBitmap)
        } else {
            Toast.makeText(mContext, "Error al generar código QR", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecompensas() {
        val recompensas = obtenerRecompensasDisponibles()

        val adapter = RecompensaAdapter(recompensas) { recompensa ->
            if (recompensa.desbloqueado) {
                Toast.makeText(mContext, "Ya has canjeado: ${recompensa.titulo}", Toast.LENGTH_SHORT).show()
            } else {
                val usuarioRecompensas = obtenerDatosUsuario()
                if (usuarioRecompensas.puntosActuales >= recompensa.puntos) {
                    Toast.makeText(mContext, "¿Canjear ${recompensa.titulo}?", Toast.LENGTH_SHORT).show()
                    // TODO: Implementar diálogo de confirmación y canje
                } else {
                    val puntosNecesarios = recompensa.puntos - usuarioRecompensas.puntosActuales
                    Toast.makeText(
                        mContext,
                        "Te faltan $puntosNecesarios puntos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.rvRecompensas.apply {
            layoutManager = LinearLayoutManager(mContext)
            this.adapter = adapter
        }
    }

    private fun compartirQR() {
        try {
            // Obtener el bitmap del QR
            binding.ivCodigoQR.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(binding.ivCodigoQR.drawingCache)
            binding.ivCodigoQR.isDrawingCacheEnabled = false

            // Guardar temporalmente
            val cachePath = File(mContext.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "qr_code.png")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()

            // Compartir
            val contentUri = FileProvider.getUriForFile(
                mContext,
                "${mContext.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, "Mi código de miembro Cinetopia")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Compartir QR"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(mContext, "Error al compartir QR", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerDatosUsuario(): UsuarioRecompensas {
        val user = firebaseAuth.currentUser
        return UsuarioRecompensas(
            nombre = user?.displayName ?: "Usuario",
            email = user?.email ?: "usuario@cinetopia.com",
            puntosActuales = 1250,
            nivel = "Nivel Oro",
            codigoQR = "MEM-${user?.uid?.take(6)?.uppercase() ?: "123456"}",
            miembroDesde = "Enero 2024"
        )
    }

    private fun obtenerRecompensasDisponibles(): List<Recompensa> {
        return listOf(
            Recompensa(
                id = 1,
                titulo = "Palomitas Gratis",
                descripcion = "Palomitas medianas en tu próxima compra",
                puntos = 500,
                icono = "popcorn",
                desbloqueado = true
            ),
            Recompensa(
                id = 2,
                titulo = "Refresco Gratis",
                descripcion = "Refresco mediano de tu elección",
                puntos = 400,
                icono = "drink",
                desbloqueado = false
            ),
            Recompensa(
                id = 3,
                titulo = "Combo 2x1",
                descripcion = "Compra un combo y lleva otro gratis",
                puntos = 800,
                icono = "combo",
                desbloqueado = false
            ),
            Recompensa(
                id = 4,
                titulo = "Boleto Gratis",
                descripcion = "Un boleto gratis para cualquier función",
                puntos = 1000,
                icono = "ticket",
                desbloqueado = false
            ),
            Recompensa(
                id = 5,
                titulo = "Upgrade VIP",
                descripcion = "Mejora a sala VIP sin costo",
                puntos = 1500,
                icono = "vip",
                desbloqueado = false
            ),
            Recompensa(
                id = 6,
                titulo = "Mes de Cine",
                descripcion = "4 boletos para usar en 30 días",
                puntos = 3000,
                icono = "calendar",
                desbloqueado = false
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}