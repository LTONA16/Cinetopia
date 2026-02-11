package com.example.cinetopia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinetopia.Adaptadores.PeliculaAdapter
import com.example.cinetopia.Modelos.Pelicula
import com.example.cinetopia.databinding.FragmentInicioBinding

class FragmentInicio : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartelera()
        setupPreventa()
        setupCines()

        binding.btnExplorar.setOnClickListener {
            Toast.makeText(requireContext(), "Explorando cartelera...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCartelera() {
        val peliculas = listOf(
            Pelicula(1, "Batman", "p1", 8.5, "Acción", 120),
            Pelicula(2, "Spiderman", "p2", 7.9, "Drama", 110),
            Pelicula(3, "Dune", "p3", 9.0, "Sci-Fi", 140),
            Pelicula(4, "Avatar", "p4", 8.2, "Aventura", 130)
        )

        binding.rvCartelera.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = PeliculaAdapter(peliculas) { pelicula ->
                Toast.makeText(requireContext(), pelicula.titulo, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPreventa() {
        val peliculas = listOf(
            Pelicula(5, "Matrix", "p5", 8.7, "Acción", 125),
            Pelicula(6, "Joker", "p6", 9.2, "Drama", 115)
        )

        binding.rvPreventa.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = PeliculaAdapter(peliculas) { pelicula ->
                Toast.makeText(requireContext(), "Preventa: ${pelicula.titulo}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCines() {
        val peliculas = listOf(
            Pelicula(7, "Rocky", "p7", 8.0, "Drama", 100),
            Pelicula(8, "Alien", "p8", 8.8, "Terror", 110)
        )

        binding.rvCinesCercanos.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = PeliculaAdapter(peliculas) { pelicula ->
                Toast.makeText(requireContext(), "Cercano: ${pelicula.titulo}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}