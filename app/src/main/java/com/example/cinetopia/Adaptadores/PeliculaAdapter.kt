package com.example.cinetopia.Adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.R
import com.example.cinetopia.Modelos.Pelicula

class PeliculaAdapter(
    private val peliculas: List<Pelicula>,
    private val onPeliculaClick: (Pelicula) -> Unit
) : RecyclerView.Adapter<PeliculaAdapter.PeliculaViewHolder>() {

    inner class PeliculaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPelicula: ImageView = itemView.findViewById(R.id.ivPelicula)
        val tvTituloPelicula: TextView = itemView.findViewById(R.id.tvTituloPelicula)
        val tvCalificacion: TextView = itemView.findViewById(R.id.tvCalificacion)

        fun bind(pelicula: Pelicula) {
            // Cargar imagen desde recursos drawable
            val context = itemView.context
            val imageResId = context.resources.getIdentifier(
                pelicula.imagenNombre,
                "drawable",
                context.packageName
            )

            if (imageResId != 0) {
                ivPelicula.setImageResource(imageResId)
            } else {
                ivPelicula.setImageResource(R.drawable.ic_launcher_background)
            }

            tvTituloPelicula.text = pelicula.titulo

            // Mostrar calificación si existe
            if (pelicula.calificacion > 0) {
                tvCalificacion.visibility = View.VISIBLE
                tvCalificacion.text = "⭐ ${pelicula.calificacion}"
            } else {
                tvCalificacion.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onPeliculaClick(pelicula)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeliculaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_pelicula, parent, false)
        return PeliculaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeliculaViewHolder, position: Int) {
        holder.bind(peliculas[position])
    }

    override fun getItemCount(): Int = peliculas.size
}