package com.example.cinetopia.Fragmentos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinetopia.Adaptadores.ComboAdapter
import com.example.cinetopia.Adaptadores.ProductoDulceriaAdapter
import com.example.cinetopia.Adaptadores.PromocionAdapter
import com.example.cinetopia.Modelos.CategoriaProducto
import com.example.cinetopia.Modelos.ProductoDulceria
import com.example.cinetopia.Modelos.Promocion
import com.example.cinetopia.databinding.FragmentDulceriaBinding

class FragmentDulceria : Fragment() {

    private var _binding: FragmentDulceriaBinding? = null
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
        _binding = FragmentDulceriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPromociones()
        setupCombos()
        setupPalomitas()
        setupBebidas()
        setupDulces()
    }

    private fun setupPromociones() {
        val promociones = listOf(
            Promocion(
                id = 1,
                titulo = "¡MARTES DE PLAZA!",
                descripcion = "2x1 en combos",
                descuento = "50% OFF",
                imagenResId = "hero_background",
                colorFondo = "#FF6B6B"
            ),
            Promocion(
                id = 2,
                titulo = "COMBO ESTUDIANTE",
                descripcion = "Palomitas + Refresco",
                descuento = "$99",
                imagenResId = "hero_background",
                colorFondo = "#4ECDC4"
            ),
            Promocion(
                id = 3,
                titulo = "HAPPY HOUR",
                descripcion = "De 2pm a 5pm",
                descuento = "30% OFF",
                imagenResId = "hero_background",
                colorFondo = "#FFD93D"
            )
        )

        val adapter = PromocionAdapter(promociones) { promocion ->
            Toast.makeText(mContext, "Promoción: ${promocion.titulo}", Toast.LENGTH_SHORT).show()
        }

        binding.rvPromociones.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    private fun setupCombos() {
        val combos = listOf(
            ProductoDulceria(
                id = 1,
                nombre = "Combo Pareja",
                descripcion = "2 Palomitas grandes + 2 Refrescos grandes",
                precio = 220.0,
                imagenResId = "p1",
                categoria = CategoriaProducto.COMBO
            ),
            ProductoDulceria(
                id = 2,
                nombre = "Combo Familiar",
                descripcion = "3 Palomitas medianas + 3 Refrescos + Nachos",
                precio = 380.0,
                imagenResId = "p2",
                categoria = CategoriaProducto.COMBO
            ),
            ProductoDulceria(
                id = 3,
                nombre = "Combo Individual",
                descripcion = "1 Palomita mediana + 1 Refresco mediano",
                precio = 120.0,
                imagenResId = "p3",
                categoria = CategoriaProducto.COMBO
            )
        )

        val adapter = ComboAdapter(combos) { combo ->
            Toast.makeText(mContext, "Agregado: ${combo.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.rvCombos.apply {
            layoutManager = LinearLayoutManager(mContext)
            this.adapter = adapter
        }
    }

    private fun setupPalomitas() {
        val palomitas = listOf(
            ProductoDulceria(
                id = 10,
                nombre = "Palomitas Grande",
                descripcion = "Con mantequilla",
                precio = 85.0,
                imagenResId = "p4",
                categoria = CategoriaProducto.PALOMITAS
            ),
            ProductoDulceria(
                id = 11,
                nombre = "Palomitas Mediana",
                descripcion = "Con mantequilla",
                precio = 65.0,
                imagenResId = "p5",
                categoria = CategoriaProducto.PALOMITAS
            ),
            ProductoDulceria(
                id = 12,
                nombre = "Palomitas Chica",
                descripcion = "Con mantequilla",
                precio = 45.0,
                imagenResId = "p6",
                categoria = CategoriaProducto.PALOMITAS
            ),
            ProductoDulceria(
                id = 13,
                nombre = "Palomitas Jumbo",
                descripcion = "Extra grande",
                precio = 110.0,
                imagenResId = "p7",
                categoria = CategoriaProducto.PALOMITAS
            )
        )

        val adapter = ProductoDulceriaAdapter(palomitas) { producto ->
            Toast.makeText(mContext, "Agregado: ${producto.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.rvPalomitas.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    private fun setupBebidas() {
        val bebidas = listOf(
            ProductoDulceria(
                id = 20,
                nombre = "Coca-Cola Grande",
                descripcion = "700ml",
                precio = 55.0,
                imagenResId = "p8",
                categoria = CategoriaProducto.BEBIDAS
            ),
            ProductoDulceria(
                id = 21,
                nombre = "Sprite Grande",
                descripcion = "700ml",
                precio = 55.0,
                imagenResId = "p1",
                categoria = CategoriaProducto.BEBIDAS
            ),
            ProductoDulceria(
                id = 22,
                nombre = "Fanta Grande",
                descripcion = "700ml",
                precio = 55.0,
                imagenResId = "p2",
                categoria = CategoriaProducto.BEBIDAS
            ),
            ProductoDulceria(
                id = 23,
                nombre = "Agua Embotellada",
                descripcion = "600ml",
                precio = 30.0,
                imagenResId = "p3",
                categoria = CategoriaProducto.BEBIDAS
            )
        )

        val adapter = ProductoDulceriaAdapter(bebidas) { producto ->
            Toast.makeText(mContext, "Agregado: ${producto.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.rvBebidas.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    private fun setupDulces() {
        val dulces = listOf(
            ProductoDulceria(
                id = 30,
                nombre = "M&M's",
                descripcion = "Chocolate",
                precio = 40.0,
                imagenResId = "p4",
                categoria = CategoriaProducto.DULCES
            ),
            ProductoDulceria(
                id = 31,
                nombre = "Skittles",
                descripcion = "Sabores frutales",
                precio = 40.0,
                imagenResId = "p5",
                categoria = CategoriaProducto.DULCES
            ),
            ProductoDulceria(
                id = 32,
                nombre = "Snickers",
                descripcion = "Barra de chocolate",
                precio = 35.0,
                imagenResId = "p6",
                categoria = CategoriaProducto.DULCES
            ),
            ProductoDulceria(
                id = 33,
                nombre = "Gomitas",
                descripcion = "Surtido",
                precio = 45.0,
                imagenResId = "p7",
                categoria = CategoriaProducto.DULCES
            )
        )

        val adapter = ProductoDulceriaAdapter(dulces) { producto ->
            Toast.makeText(mContext, "Agregado: ${producto.nombre}", Toast.LENGTH_SHORT).show()
        }

        binding.rvDulces.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}