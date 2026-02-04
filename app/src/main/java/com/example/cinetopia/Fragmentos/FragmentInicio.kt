package com.example.cinetopia.Fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.Adaptadores.PeliculaAdapter
import com.example.cinetopia.Modelos.Pelicula
import com.example.cinetopia.R
import com.google.android.material.button.MaterialButton

class FragmentInicio : Fragment() {

    private lateinit var rvCartelera: RecyclerView
    private lateinit var rvPreventa: RecyclerView
    private lateinit var rvCinesCercanos: RecyclerView
    private lateinit var btnExplorar: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        rvCartelera = view.findViewById(R.id.rvCartelera)
        rvPreventa = view.findViewById(R.id.rvPreventa)
        rvCinesCercanos = view.findViewById(R.id.rvCinesCercanos)
        btnExplorar = view.findViewById(R.id.btnExplorar)

        // Configurar RecyclerViews
        setupCartelera()
        setupPreventa()
        setupCinesCercanos()

        // Configurar botón explorar
        btnExplorar.setOnClickListener {
            Toast.makeText(requireContext(), "Explorando cartelera...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCartelera() {
        // Datos de ejemplo para cartelera (7 películas)
        val peliculasCartelera = listOf(
            Pelicula(1, "Película 1", "p1", 8.5, "Acción", 120),
            Pelicula(2, "Película 2", "p2", 7.8, "Drama", 110),
            Pelicula(3, "Película 3", "p3", 9.0, "Ciencia Ficción", 140),
            Pelicula(4, "Película 4", "p4", 8.2, "Thriller", 105),
            Pelicula(5, "Película 5", "p5", 7.5, "Comedia", 95),
            Pelicula(6, "Película 6", "p6", 8.8, "Aventura", 130),
            Pelicula(7, "Película 7", "p7", 7.9, "Terror", 100)
        )

        val adapter = PeliculaAdapter(peliculasCartelera) { pelicula ->
            Toast.makeText(requireContext(), "Seleccionaste: ${pelicula.titulo}", Toast.LENGTH_SHORT).show()
        }

        rvCartelera.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCartelera.adapter = adapter
    }

    private fun setupPreventa() {
        // Datos de ejemplo para preventa
        val peliculasPreventa = listOf(
            Pelicula(9, "Película 9", "p9", 8.7, "Drama", 115),
            Pelicula(8, "Película 8", "p8", 9.2, "Acción", 125),
            Pelicula(1, "Película 1", "p1", 8.5, "Ciencia Ficción", 120),
            Pelicula(2, "Película 2", "p2", 7.8, "Thriller", 110),
            Pelicula(3, "Película 3", "p3", 9.0, "Aventura", 140)
        )

        val adapter = PeliculaAdapter(peliculasPreventa) { pelicula ->
            Toast.makeText(requireContext(), "Preventa: ${pelicula.titulo}", Toast.LENGTH_SHORT).show()
        }

        rvPreventa.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvPreventa.adapter = adapter
    }

    private fun setupCinesCercanos() {
        // Datos de ejemplo para cines cercanos
        val peliculasCercanos = listOf(
            Pelicula(4, "Película 4", "p4", 8.2, "Comedia", 105),
            Pelicula(5, "Película 5", "p5", 7.5, "Terror", 95),
            Pelicula(6, "Película 6", "p6", 8.8, "Drama", 130),
            Pelicula(7, "Película 7", "p7", 7.9, "Acción", 100),
            Pelicula(8, "Película 8", "p8", 9.2, "Ciencia Ficción", 125),
            Pelicula(9, "Película 9", "p9", 8.7, "Aventura", 115)
        )

        val adapter = PeliculaAdapter(peliculasCercanos) { pelicula ->
            Toast.makeText(requireContext(), "Cine cercano: ${pelicula.titulo}", Toast.LENGTH_SHORT).show()
        }

        rvCinesCercanos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCinesCercanos.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentInicio()
    }
}