package com.example.gestordearchivos.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gestordearchivos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Permiso concedido, puedes cargar el fragmento inicial
                setupNavigation()
            } else {
                Toast.makeText(this, "El permiso de almacenamiento es necesario para usar la app.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                storagePermissionLauncher.launch(intent)
            } else {
                setupNavigation()
            }
        } else {
            // Para versiones < Android 11, la lógica de permisos en tiempo de ejecución
            // (READ/WRITE_EXTERNAL_STORAGE) se manejaría aquí.
            // Por simplicidad, asumimos que se conceden.
            setupNavigation()
        }
    }

    private fun setupNavigation() {
        // El NavHostFragment ya está en el layout, por lo que la navegación
        // se iniciará automáticamente. Podemos añadir configuraciones aquí si es necesario.
    }
}