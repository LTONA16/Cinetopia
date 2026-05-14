package com.example.cinetopia

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cinetopia.databinding.ActivityRegistroEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmail.error = "Email Invalido"
            binding.EtEmail.requestFocus()
        }
        else if(email.isEmpty()){
            binding.EtEmail.error = "Ingrese un email"
            binding.EtEmail.requestFocus()
        }
        else if(password.isEmpty()){
            binding.EtPassword.error = "Ingrese el password"
            binding.EtPassword.requestFocus()
        }
        else if(r_password.isEmpty()){
            binding.EtRPassword.error = "Repita el password"
            binding.EtRPassword.requestFocus()
        }
        else if(password != r_password){
            binding.EtRPassword.error = "No coinciden"
            binding.EtRPassword.requestFocus()
        }
        else{
            registrarUsuario()
        }
    }

    private fun registrarUsuario(){
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                llenarInfoDB()
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "No se registró el usuario debido a ${exception.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun llenarInfoDB() {
        progressDialog.setMessage("Guardando Información")
        val tiempo = Constantes.obtenerTiempoDis()
        val emailUsuario = firebaseAuth.currentUser!!.email
        val uidUsuario = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["nombres"] = ""
        hashMap["codigoTelefono"] = ""
        hashMap["urlImagenPerfil"] = ""
        hashMap["proveedor"] = "Email"
        hashMap["escribiendo"] = ""
        hashMap["tiempo"] = tiempo
        hashMap["online"] = true
        hashMap["email"] = "${emailUsuario}"
        hashMap["uid"] = "${uidUsuario}"
        hashMap["fecha_nac"] = ""
        hashMap["role"] = "cliente"

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidUsuario!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                // Después de guardar en DB, enviar email de bienvenida
                enviarEmailBienvenida()
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "No se registró el usuario debido a ${exception.message}",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun enviarEmailBienvenida() {
        progressDialog.setMessage("Enviando email de bienvenida...")

        // Enviar email en segundo plano usando coroutines
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val emailEnviado = EmailSender.enviarEmailBienvenida(
                    emailDestino = email,
                    nombreUsuario = email,
                    password = password
                )

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()

                    if (emailEnviado) {
                        Toast.makeText(
                            this@Registro_email,
                            "Registro exitoso. Revisa tu correo electrónico 📧",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@Registro_email,
                            "Registro exitoso ✓",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Ir al MainActivity
                    startActivity(Intent(this@Registro_email, MainActivity::class.java))
                    finishAffinity()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@Registro_email,
                        "Registro exitoso ✓",
                        Toast.LENGTH_LONG
                    ).show()

                    // Ir al MainActivity aunque falle el email
                    startActivity(Intent(this@Registro_email, MainActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }
}