package com.samsung.gallery.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.samsung.gallery.R
import com.samsung.gallery.adapters.AlbumAdapter
import com.samsung.gallery.databinding.ActivityMainBinding
import com.samsung.gallery.models.Album
import com.samsung.gallery.ui.album.AlbumActivity
import com.samsung.gallery.utils.PermissionUtils
import com.samsung.gallery.viewmodel.MainViewModel

/**
 * Actividad principal que muestra la lista de álbumes
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var albumAdapter: AlbumAdapter

    // Launcher para solicitar permisos
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.checkPermissions()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        observeViewModel()
    }

    /**
     * Configura la interfaz de usuario
     */
    private fun setupUI() {
        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        // Configurar botón de refrescar
        binding.buttonRefresh.setOnClickListener {
            viewModel.refreshAlbums()
        }

        // Configurar botón de solicitar permisos
        binding.buttonRequestPermissions.setOnClickListener {
            requestPermissions()
        }
    }

    /**
     * Configura el RecyclerView para mostrar los álbumes
     */
    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter { album ->
            openAlbum(album)
        }

        binding.recyclerViewAlbums.apply {
            adapter = albumAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            setHasFixedSize(true)
        }
    }

    /**
     * Observa los cambios en el ViewModel
     */
    private fun observeViewModel() {
        // Observar lista de álbumes
        viewModel.albums.observe(this) { albums ->
            albumAdapter.submitList(albums)
            updateEmptyState(albums.isEmpty())
        }

        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        // Observar permisos
        viewModel.hasPermissions.observe(this) { hasPermissions ->
            binding.layoutPermissions.isVisible = !hasPermissions
            binding.layoutContent.isVisible = hasPermissions
        }

        // Observar errores
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    /**
     * Actualiza el estado cuando no hay álbumes
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.layoutEmpty.isVisible = isEmpty && !binding.progressBar.isVisible
    }

    /**
     * Solicita los permisos necesarios
     */
    private fun requestPermissions() {
        val permissions = PermissionUtils.getRequiredPermissions()
        permissionLauncher.launch(permissions)
    }

    /**
     * Muestra un diálogo cuando los permisos son denegados
     */
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permisos necesarios")
            .setMessage(PermissionUtils.getPermissionExplanation())
            .setPositiveButton("Conceder") { _, _ ->
                requestPermissions()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Abre un álbum específico
     */
    private fun openAlbum(album: Album) {
        val intent = Intent(this, AlbumActivity::class.java).apply {
            putExtra(AlbumActivity.EXTRA_BUCKET_ID, album.bucketId)
            putExtra(AlbumActivity.EXTRA_ALBUM_NAME, album.name)
        }
        startActivity(intent)
    }
}