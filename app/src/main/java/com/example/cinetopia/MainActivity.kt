package com.example.cinetopia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cinetopia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BottomNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Item_inicio -> {
                    true
                }
                R.id.Item_cuenta -> {
                    true
                }
                R.id.Item_boletos -> {
                    true
                }
                R.id.Item_dulceria -> {
                    true
                }
                R.id.Item_recompensas -> {
                    true
                }
                else -> { false }
            }
        }
    }
}