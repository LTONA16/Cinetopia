package com.example.cinetopia

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cinetopia.databinding.ActivityRegistroEmailBinding
import com.google.firebase.auth.FirebaseAuth

class Registro_email : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroEmailBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.BtnRegistrar.setOnClickListener {
            validarInfo()
        }
    }

    private var email = ""
    private var password = ""
    private var r_password = ""

    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()
        r_password = binding.EtRPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.EtEmail.error = "Ingrese su email"
        }
        if (password.isEmpty()) {
            binding.EtPassword.error = "Ingrese su contraseña"
        }
        if (r_password.isEmpty()) {
            binding.EtRPassword.error = "Repita su contraseña"
        }
        if (password != r_password) {
            binding.EtRPassword.error = "Las contraseñas no coinciden"
        }
        if (password.length < 6) {
            binding.EtPassword.error = "La contraseña debe tener al menos 6 caracteres"
        }
        if (password == r_password) {
            firebaseRegistrar()
        }
    }

    private fun firebaseRegistrar() {

    }

}