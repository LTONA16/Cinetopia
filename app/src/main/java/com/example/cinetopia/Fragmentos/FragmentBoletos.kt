package com.example.cinetopia.Fragmentos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinetopia.Adaptadores.BoletoAdapter
import com.example.cinetopia.Modelos.Boleto
import com.example.cinetopia.databinding.FragmentBoletosBinding
import java.util.Calendar

class FragmentBoletos : Fragment() {

    private var _binding: FragmentBoletosBinding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentBoletosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBoletos()
    }

    private fun setupBoletos() {
        // Datos hardcodeados (simulación)
        val boletos = obtenerBoletosSimulados()

        if (boletos.isEmpty()) {
            // Mostrar vista vacía
            binding.layoutVacio.visibility = View.VISIBLE
            binding.rvBoletos.visibility = View.GONE
            binding.tvCantidadBoletos.text = "No tienes boletos"

            binding.btnIrCartelera.setOnClickListener {
                // TODO: Navegar a cartelera
                Toast.makeText(mContext, "Ir a cartelera", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Mostrar lista de boletos
            binding.layoutVacio.visibility = View.GONE
            binding.rvBoletos.visibility = View.VISIBLE
            binding.tvCantidadBoletos.text = "Tienes ${boletos.size} boleto${if (boletos.size > 1) "s" else ""}"

            val adapter = BoletoAdapter(
                boletos = boletos,
                onQRClick = { boleto ->
                    Toast.makeText(mContext, "Ver QR: ${boleto.codigoQR}", Toast.LENGTH_SHORT).show()
                    // TODO: Mostrar dialog con código QR
                },
                onDetallesClick = { boleto ->
                    Toast.makeText(mContext, "Detalles de ${boleto.pelicula}", Toast.LENGTH_SHORT).show()
                    // TODO: Abrir pantalla de detalles del boleto
                }
            )

            binding.rvBoletos.apply {
                layoutManager = LinearLayoutManager(mContext)
                this.adapter = adapter
            }
        }
    }

    private fun obtenerBoletosSimulados(): List<Boleto> {
        val calendar = Calendar.getInstance()

        // Boleto 1 - Hoy en la noche
        val hoy = calendar.clone() as Calendar
        hoy.set(Calendar.HOUR_OF_DAY, 19)
        hoy.set(Calendar.MINUTE, 30)

        // Boleto 2 - Mañana
        val manana = calendar.clone() as Calendar
        manana.add(Calendar.DAY_OF_MONTH, 1)
        manana.set(Calendar.HOUR_OF_DAY, 15)
        manana.set(Calendar.MINUTE, 0)

        // Boleto 3 - Próximo fin de semana
        val finDeSemana = calendar.clone() as Calendar
        finDeSemana.add(Calendar.DAY_OF_MONTH, 5)
        finDeSemana.set(Calendar.HOUR_OF_DAY, 21)
        finDeSemana.set(Calendar.MINUTE, 0)

        return listOf(
            Boleto(
                id = 1,
                pelicula = "Dune",
                posterResId = "p3",
                cine = "Cinépolis VIP Plaza Sendero",
                sala = "Sala 3",
                fecha = "18 Feb 2026",
                hora = "19:30",
                asientos = "F5, F6",
                cantidad = 2,
                total = 350.00,
                codigoQR = "BOL-001234",
                fechaTimestamp = hoy.timeInMillis
            ),
            Boleto(
                id = 2,
                pelicula = "Avatar",
                posterResId = "p4",
                cine = "Cinépolis IMAX Galerías",
                sala = "Sala 1",
                fecha = "19 Feb 2026",
                hora = "15:00",
                asientos = "G8",
                cantidad = 1,
                total = 180.00,
                codigoQR = "BOL-001235",
                fechaTimestamp = manana.timeInMillis
            ),
            Boleto(
                id = 3,
                pelicula = "Batman",
                posterResId = "p1",
                cine = "Cinépolis Centro",
                sala = "Sala 5",
                fecha = "23 Feb 2026",
                hora = "21:00",
                asientos = "H10, H11, H12",
                cantidad = 3,
                total = 480.00,
                codigoQR = "BOL-001236",
                fechaTimestamp = finDeSemana.timeInMillis
            )
        ).sortedBy { it.fechaTimestamp } // Ordenar por fecha más próxima
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}