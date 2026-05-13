package com.example.cinetopia

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cinetopia.databinding.ActivityEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class EditarPerfil : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private var imageUri: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el ID de la imagen enviado desde el fragmento
        val imagenResId = intent.getIntExtra("IMAGEN_RES_ID", R.drawable.pfp1)
        binding.imgPerfil.setImageResource(imagenResId)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por fabor espere")
        progressDialog.setCanceledOnTouchOutside(true)


        // Cargar datos actuales del usuario
        cargarInformacion()

        binding.imgPerfil.setOnClickListener {
            selec_imagen_de()
        }


        // Configurar Date Picker para el campo de fecha
        binding.etFechaNac.setOnClickListener {
            mostrarDatePicker()
        }

        binding.btnCancelar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnGuardar.setOnClickListener {
            validarYGuardar()
        }
    }

    private fun cargarInformacion() {
        val uid = firebaseAuth.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombres = snapshot.child("nombres").value.toString()
                val imagen = "${snapshot.child("urlImagenPerfil").value}"
                val telefono = snapshot.child("telefono").value.toString()
                val codigoPais = snapshot.child("codigoPais").value.toString()
                val fechaNac = snapshot.child("fecha_nac").value.toString()

                binding.etNombres.setText(if (nombres != "null") nombres else "")
                binding.etTelefono.setText(if (telefono != "null") telefono else "")
                binding.etFechaNac.setText(if (fechaNac != "null") fechaNac else "")

                try {
                    Glide.with(applicationContext)
                        .load(imagen)
                        .placeholder(R.drawable.pfp2)
                        .into(binding.imgPerfil)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@EditarPerfil,
                        "Error al cargar imagen: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                try {
                    if (codigoPais != "null" && codigoPais.isNotEmpty()) {
                        val codigo = codigoPais.replace("+", "").toInt()
                        binding.ccp.setCountryForPhoneCode(codigo)
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@EditarPerfil,
                        "Error al cargar código de país:",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditarPerfil, "Error al cargar datos", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val fechaSeleccionada = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etFechaNac.setText(fechaSeleccionada)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun validarYGuardar() {
        val nombres = binding.etNombres.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val fechaNac = binding.etFechaNac.text.toString().trim()
        val codigoPais = binding.ccp.selectedCountryCodeWithPlus

        if (nombres.isEmpty()) {
            binding.etNombres.error = "Ingresa tu nombre"
            return
        }

        actualizarPerfil(nombres, telefono, fechaNac, codigoPais)
    }

    private fun actualizarPerfil(
        nombres: String,
        telefono: String,
        fechaNac: String,
        codigoPais: String
    ) {
        val uid = firebaseAuth.uid ?: return

        val datos = HashMap<String, Any>()
        datos["nombres"] = nombres
        datos["telefono"] = telefono
        datos["codigoPais"] = codigoPais
        datos["codigoTelefono"] = "$codigoPais $telefono"
        datos["fecha_nac"] = fechaNac

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid).updateChildren(datos)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var concederPermisoCamara =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
            var concedidoTodos = true
            for (seConcede in resultado.values) {
                concedidoTodos = concedidoTodos && seConcede
            }

            if (concedidoTodos) {
                imagenCamara()
            } else {
                Toast.makeText(
                    this,
                    "El permiso de la cámara o almacenamiento fue denegado.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private var concederPermisosAlmacenamiento =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { esConcedido ->
            if (esConcedido) {
                imagenGaleria()
            } else {
                Toast.makeText(
                    this,
                    "El permiso de almacenamiento fue denegado.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun selec_imagen_de(){
        val popupMenu = PopupMenu(this, binding.imgPerfil)
        popupMenu.menu.add(Menu.NONE,1,1,"Cámara")
        popupMenu.menu.add(Menu.NONE,2,2,"Galería")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 1){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    concederPermisoCamara.launch(arrayOf(android.Manifest.permission.CAMERA))
                }else{
                    concederPermisoCamara.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
                return@setOnMenuItemClickListener true
            }
            else if (id == 2){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imagenGaleria()
                }else{
                    concederPermisosAlmacenamiento.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private val resultadoCamara_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.pfp2)
                        .into(binding.imgPerfil)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "La captura de la imagen se canceló: ${e.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(this, "La captura de la imagen se canceló", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    private fun imagenCamara() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Titulo_imagen")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion_imagen")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        resultadoCamara_ARL.launch(intent)
    }

    private val resultadoGaleria_ARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val data = resultado.data
                imageUri = data!!.data

                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.pfp2)
                        .into(binding.imgPerfil)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "La selección de la imagen se canceló ${e.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(this, "La selección de la imagen se canceló", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun imagenGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultadoGaleria_ARL.launch(intent)
    }

    private fun subirImagenStorage(){
        progressDialog.setMessage("Subiendo imagen de storage")
        progressDialog.show()

        val rutaImagen = "imagenesPerfil/${firebaseAuth.uid}"
        val storageReference = FirebaseStorage.getInstance().getReference(rutaImagen)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagenCargada = "${uriTask.result}"
                if (uriTask.isSuccessful) {
                    //actualizarImagenBD(urlImagenCargada)
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "No se pudo subir la imagen: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }



}