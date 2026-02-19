package com.example.cinetopia.Adaptadores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.Modelos.ProductoDulceria
import com.example.cinetopia.databinding.ItemProductoDulceriaBinding

class ProductoDulceriaAdapter(
    private val productos: List<ProductoDulceria>,
    private val onAgregarClick: (ProductoDulceria) -> Unit
) : RecyclerView.Adapter<ProductoDulceriaAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(private val binding: ItemProductoDulceriaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: ProductoDulceria) {
            binding.apply {
                tvNombreProducto.text = producto.nombre
                tvDescripcionProducto.text = producto.descripcion
                tvPrecioProducto.text = "$${String.format("%.2f", producto.precio)}"

                // Cargar imagen
                val context = binding.root.context
                val resourceId = context.resources.getIdentifier(
                    producto.imagenResId,
                    "drawable",
                    context.packageName
                )
                if (resourceId != 0) {
                    ivProducto.setImageResource(resourceId)
                }

                btnAgregarProducto.setOnClickListener {
                    onAgregarClick(producto)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoDulceriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount() = productos.size
}