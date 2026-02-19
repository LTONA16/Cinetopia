package com.example.cinetopia.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.Modelos.Recompensa
import com.example.cinetopia.databinding.ItemRecompensaBinding

class RecompensaAdapter(
    private val recompensas: List<Recompensa>,
    private val onRecompensaClick: (Recompensa) -> Unit
) : RecyclerView.Adapter<RecompensaAdapter.RecompensaViewHolder>() {

    inner class RecompensaViewHolder(private val binding: ItemRecompensaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recompensa: Recompensa) {
            binding.apply {
                tvTituloRecompensa.text = recompensa.titulo
                tvDescripcionRecompensa.text = recompensa.descripcion
                tvPuntosRecompensa.text = recompensa.puntos.toString()

                // Mostrar check si está desbloqueada
                if (recompensa.desbloqueado) {
                    ivCheckRecompensa.visibility = View.VISIBLE
                    root.alpha = 0.7f
                } else {
                    ivCheckRecompensa.visibility = View.GONE
                    root.alpha = 1.0f
                }

                root.setOnClickListener {
                    onRecompensaClick(recompensa)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecompensaViewHolder {
        val binding = ItemRecompensaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecompensaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecompensaViewHolder, position: Int) {
        holder.bind(recompensas[position])
    }

    override fun getItemCount() = recompensas.size
}