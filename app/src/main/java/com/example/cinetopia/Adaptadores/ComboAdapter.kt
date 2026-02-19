package com.example.cinetopia.Adaptadores

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.Modelos.ProductoDulceria
import com.example.cinetopia.databinding.ItemComboDulceriaBinding

class ComboAdapter(
    private val combos: List<ProductoDulceria>,
    private val onAgregarClick: (ProductoDulceria) -> Unit
) : RecyclerView.Adapter<ComboAdapter.ComboViewHolder>() {

    inner class ComboViewHolder(private val binding: ItemComboDulceriaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(combo: ProductoDulceria) {
            binding.apply {
                tvNombreCombo.text = combo.nombre
                tvDescripcionCombo.text = combo.descripcion
                tvPrecioCombo.text = "$${String.format("%.2f", combo.precio)}"

                // Precio anterior tachado
                val precioAnterior = combo.precio * 1.3
                tvPrecioAnteriorCombo.text = "$${String.format("%.2f", precioAnterior)}"
                tvPrecioAnteriorCombo.paintFlags =
                    tvPrecioAnteriorCombo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                // Cargar imagen
                val context = binding.root.context
                val resourceId = context.resources.getIdentifier(
                    combo.imagenResId,
                    "drawable",
                    context.packageName
                )
                if (resourceId != 0) {
                    ivCombo.setImageResource(resourceId)
                }

                btnAgregarCombo.setOnClickListener {
                    onAgregarClick(combo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboViewHolder {
        val binding = ItemComboDulceriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ComboViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComboViewHolder, position: Int) {
        holder.bind(combos[position])
    }

    override fun getItemCount() = combos.size
}