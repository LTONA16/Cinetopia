package com.example.cinetopia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinetopia.Adaptadores.PeliculaAdapter
import com.example.cinetopia.Modelos.Pelicula
import com.example.cinetopia.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        setupCartelera()
        setupPreventa()
        setupCines()

        binding.btnExplorar.setOnClickListener {
            Toast.makeText(this, "Explorando cartelera...", Toast.LENGTH_SHORT).show()
        }

        binding.BottomNV.setOnItemSelectedListener {
            true
        }
    }

    private fun comprobarSesion() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, OpcionesLogin::class.java))
            finishAffinity()
        }
    }
    private fun setupCartelera() {

        val peliculas = listOf(
            Pelicula(1,"Batman","p1",8.5,"Acción",120),
            Pelicula(2,"Spiderman","p2",7.9,"Drama",110),
            Pelicula(3,"Dune","p3",9.0,"Sci-Fi",140),
            Pelicula(4,"Avatar","p4",8.2,"Aventura",130)
        )

        binding.rvCartelera.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvCartelera.adapter =
            PeliculaAdapter(peliculas) {
                Toast.makeText(this,it.titulo,Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupPreventa() {

        val peliculas = listOf(
            Pelicula(5,"Matrix","p5",8.7,"Acción",125),
            Pelicula(6,"Joker","p6",9.2,"Drama",115)
        )

        binding.rvPreventa.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvPreventa.adapter =
            PeliculaAdapter(peliculas) {
                Toast.makeText(this,"Preventa: ${it.titulo}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupCines() {

        val peliculas = listOf(
            Pelicula(7,"Rocky","p7",8.0,"Drama",100),
            Pelicula(8,"Alien","p8",8.8,"Terror",110)
        )

        binding.rvCinesCercanos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvCinesCercanos.adapter =
            PeliculaAdapter(peliculas) {
                Toast.makeText(this,"Cercano: ${it.titulo}",Toast.LENGTH_SHORT).show()
            }
    }
}