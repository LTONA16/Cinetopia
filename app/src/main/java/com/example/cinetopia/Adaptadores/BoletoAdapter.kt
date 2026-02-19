package com.example.cinetopia.Adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.Modelos.Boleto
import com.example.cinetopia.databinding.ItemBoletoBinding

class BoletoAdapter(
    private val boletos: List<Boleto>,
    private val onQRClick: (Boleto) -> Unit,
    private val onDetallesClick: (Boleto) -> Unit
) : RecyclerView.Adapter<BoletoAdapter.BoletoViewHolder>() {

    inner class BoletoViewHolder(private val binding: ItemBoletoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(boleto: Boleto) {
            binding.apply {
                // Información básica
                tvPeliculaBoleto.text = boleto.pelicula
                tvCineBoleto.text = boleto.cine
                tvFechaBoleto.text = boleto.fecha
                tvHoraBoleto.text = boleto.hora
                tvSalaAsientosBoleto.text = "${boleto.sala} • ${boleto.asientos}"
                tvTotalBoleto.text = "$${String.format("%.2f", boleto.total)} MXN"
                tvCodigoBoleto.text = boleto.codigoQR

                // Cargar imagen del poster
                val context = binding.root.context
                val resourceId = context.resources.getIdentifier(
                    boleto.posterResId,
                    "drawable",
                    context.packageName
                )
                if (resourceId != 0) {
                    ivPosterBoleto.setImageResource(resourceId)
                }

                // Estado del boleto
                val ahora = System.currentTimeMillis()
                val tiempoRestante = boleto.fechaTimestamp - ahora
                val horasRestantes = tiempoRestante / (1000 * 60 * 60)

                when {
                    tiempoRestante < 0 -> {
                        tvEstadoBoleto.text = "USADO"
                        binding.root.alpha = 0.6f
                    }
                    horasRestantes < 24 -> {
                        tvEstadoBoleto.text = "HOY"
                    }
                    horasRestantes < 48 -> {
                        tvEstadoBoleto.text = "MAÑANA"
                    }
                    else -> {
                        tvEstadoBoleto.text = "PRÓXIMO"
                    }
                }

                // Listeners
                btnVerQR.setOnClickListener { onQRClick(boleto) }
                btnDetalles.setOnClickListener { onDetallesClick(boleto) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoletoViewHolder {
        val binding = ItemBoletoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BoletoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoletoViewHolder, position: Int) {
        holder.bind(boletos[position])
    }

    override fun getItemCount() = boletos.size
}