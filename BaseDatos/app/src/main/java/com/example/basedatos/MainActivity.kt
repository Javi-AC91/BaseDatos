package com.example.basedatos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.basedatos.databinding.ActivityMainBinding
import com.example.basedatos.model.Usuario
import com.example.basedatos.repositories.UsuarioRepository
import com.example.basedatos.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: UsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = MyApplication.getDatabase(this)
        val repository = UsuarioRepository(db.usuarioDao())
        viewModel = UsuarioViewModel(repository)

        // Observar la lista de usuarios
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.usuarios.collect { listaDeUsuarios ->
                    if (listaDeUsuarios.isEmpty()) {
                        binding.tvwList.text = "La base de datos está vacía."
                    } else {
                        val stringBuilder = StringBuilder()
                        listaDeUsuarios.forEach { usuario ->
                            stringBuilder.append("ID: ${usuario.id} | Nombre: ${usuario.nombre} | Edad: ${usuario.edad}\n")
                        }
                        binding.tvwList.text = stringBuilder.toString()
                    }
                }
            }
        }

        // Botón Registrar
        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val edadStr = binding.etEdad.text.toString().trim()

            if (nombre.isEmpty() || edadStr.isEmpty()) {
                binding.tvwList.text = "Por favor llena todos los campos."
                return@setOnClickListener
            }

            val edad = edadStr.toIntOrNull() ?: 0
            viewModel.agregarUsuario(Usuario(nombre = nombre, edad = edad))
            binding.etNombre.text.clear()
            binding.etEdad.text.clear()
        }

        // Botón Mostrar
        binding.btnMostrar.setOnClickListener {
            viewModel.cargarUsuarios()
        }
    }
}
