package com.example.cinetopia.Adaptadores

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.Modelos.Promocion
import com.example.cinetopia.databinding.ItemPromocionBinding

class PromocionAdapter(
    private val promociones: List<Promocion>,
    private val onPromocionClick: (Promocion) -> Unit
) : RecyclerView.Adapter<PromocionAdapter.PromocionViewHolder>() {

    inner class PromocionViewHolder(private val binding: ItemPromocionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(promocion: Promocion) {
            binding.apply {
                tvTituloPromocion.text = promocion.titulo
                tvDescripcionPromocion.text = promocion.descripcion
                tvDescuentoPromocion.text = promocion.descuento

                // Aplicar color de fondo
                try {
                    layoutPromocion.setBackgroundColor(Color.parseColor(promocion.colorFondo))
                } catch (e: Exception) {
                    layoutPromocion.setBackgroundColor(Color.parseColor("#FF6B6B"))
                }

                // Cargar imagen de fondo
                val context = binding.root.context
                val resourceId = context.resources.getIdentifier(
                    promocion.imagenResId,
                    "drawable",
                    context.packageName
                )
                if (resourceId != 0) {
                    ivPromocionFondo.setImageResource(resourceId)
                }

                fabVerPromocion.setOnClickListener {
                    onPromocionClick(promocion)
                }

                binding.root.setOnClickListener {
                    onPromocionClick(promocion)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromocionViewHolder {
        val binding = ItemPromocionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PromocionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromocionViewHolder, position: Int) {
        holder.bind(promociones[position])
    }

    override fun getItemCount() = promociones.size
}