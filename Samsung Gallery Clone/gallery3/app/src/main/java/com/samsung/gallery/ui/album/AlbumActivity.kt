package com.samsung.gallery.ui.album

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.samsung.gallery.R
import com.samsung.gallery.adapters.MediaAdapter
import com.samsung.gallery.databinding.ActivityAlbumBinding
import com.samsung.gallery.models.MediaItem
import com.samsung.gallery.ui.viewer.ViewerActivity
import com.samsung.gallery.viewmodel.AlbumViewModel

/**
 * Actividad que muestra el contenido de un álbum específico
 */
class AlbumActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_BUCKET_ID = "extra_bucket_id"
        const val EXTRA_ALBUM_NAME = "extra_album_name"
    }
    
    private lateinit var binding: ActivityAlbumBinding
    private val viewModel: AlbumViewModel by viewModels()
    private lateinit var mediaAdapter: MediaAdapter
    
    private var bucketId: String = ""
    private var albumName: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        getIntentData()
        setupUI()
        setupRecyclerView()
        observeViewModel()
        loadAlbumContent()
    }
    
    /**
     * Obtiene los datos del Intent
     */
    private fun getIntentData() {
        bucketId = intent.getStringExtra(EXTRA_BUCKET_ID) ?: ""
        albumName = intent.getStringExtra(EXTRA_ALBUM_NAME) ?: ""
        
        if (bucketId.isEmpty()) {
            Toast.makeText(this, "Error: ID de álbum no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }
    
    /**
     * Configura la interfaz de usuario
     */
    private fun setupUI() {
        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = albumName
        }
        
        // Configurar botón de refrescar
        binding.buttonRefresh.setOnClickListener {
            viewModel.refreshMediaItems(bucketId, albumName)
        }
    }
    
    /**
     * Configura el RecyclerView para mostrar los archivos multimedia
     */
    private fun setupRecyclerView() {
        mediaAdapter = MediaAdapter { mediaItem, position ->
            openMediaViewer(mediaItem, position)
        }
        
        binding.recyclerViewMedia.apply {
            adapter = mediaAdapter
            layoutManager = GridLayoutManager(this@AlbumActivity, 3)
            setHasFixedSize(true)
        }
    }
    
    /**
     * Observa los cambios en el ViewModel
     */
    private fun observeViewModel() {
        // Observar lista de archivos multimedia
        viewModel.mediaItems.observe(this) { mediaItems ->
            mediaAdapter.submitList(mediaItems)
            updateEmptyState(mediaItems.isEmpty())
        }
        
        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
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
     * Actualiza el estado cuando no hay archivos multimedia
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.layoutEmpty.isVisible = isEmpty && !binding.progressBar.isVisible
    }
    
    /**
     * Carga el contenido del álbum
     */
    private fun loadAlbumContent() {
        viewModel.loadMediaItems(bucketId, albumName)
    }
    
    /**
     * Abre el visor de archivos multimedia
     */
    private fun openMediaViewer(mediaItem: MediaItem, position: Int) {
        val mediaItems = mediaAdapter.currentList
        val intent = Intent(this, ViewerActivity::class.java).apply {
            putExtra(ViewerActivity.EXTRA_MEDIA_ITEMS, ArrayList(mediaItems))
            putExtra(ViewerActivity.EXTRA_CURRENT_POSITION, position)
        }
        startActivity(intent)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}