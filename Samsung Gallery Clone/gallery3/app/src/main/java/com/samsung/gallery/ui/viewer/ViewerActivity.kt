package com.example.samsunggalleryclone.ui.viewer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.samsunggalleryclone.adapters.MediaViewerAdapter
import com.example.samsunggalleryclone.databinding.ActivityViewerBinding
import com.example.samsunggalleryclone.models.MediaItem

/**
 * Actividad para visualizar archivos multimedia en pantalla completa
 */
class ViewerActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_MEDIA_ITEMS = "extra_media_items"
        const val EXTRA_CURRENT_POSITION = "extra_current_position"
    }
    
    private lateinit var binding: ActivityViewerBinding
    private lateinit var mediaViewerAdapter: MediaViewerAdapter
    private var mediaItems: List<MediaItem> = emptyList()
    private var currentPosition: Int = 0
    private var isUIVisible = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        getIntentData()
        setupUI()
        setupViewPager()
        setupClickListeners()
    }
    
    /**
     * Obtiene los datos del Intent
     */
    private fun getIntentData() {
        mediaItems = intent.getParcelableArrayListExtra<MediaItem>(EXTRA_MEDIA_ITEMS) ?: emptyList()
        currentPosition = intent.getIntExtra(EXTRA_CURRENT_POSITION, 0)
        
        if (mediaItems.isEmpty()) {
            finish()
            return
        }
    }
    
    /**
     * Configura la interfaz de usuario
     */
    private fun setupUI() {
        // Configurar pantalla completa
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.systemBarsBehavior = 
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        
        updateUI()
    }
    
    /**
     * Configura el ViewPager2
     */
    private fun setupViewPager() {
        mediaViewerAdapter = MediaViewerAdapter(this, mediaItems)
        
        binding.viewPager.apply {
            adapter = mediaViewerAdapter
            currentItem = currentPosition
            
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentPosition = position
                    updateUI()
                }
            })
        }
    }
    
    /**
     * Configura los listeners de click
     */
    private fun setupClickListeners() {
        // Click en el ViewPager para mostrar/ocultar UI
        binding.viewPager.setOnClickListener {
            toggleUI()
        }
        
        // Botón de información
        binding.buttonInfo.setOnClickListener {
            showMediaInfo()
        }
        
        // Botón de compartir
        binding.buttonShare.setOnClickListener {
            shareCurrentMedia()
        }
        
        // Botón de eliminar
        binding.buttonDelete.setOnClickListener {
            deleteCurrentMedia()
        }
    }
    
    /**
     * Actualiza la interfaz de usuario
     */
    private fun updateUI() {
        val currentMedia = mediaItems.getOrNull(currentPosition)
        currentMedia?.let { media ->
            binding.textMediaInfo.text = "${currentPosition + 1} de ${mediaItems.size}"
        }
    }
    
    /**
     * Alterna la visibilidad de la UI
     */
    private fun toggleUI() {
        isUIVisible = !isUIVisible
        
        val visibility = if (isUIVisible) View.VISIBLE else View.GONE
        binding.toolbar.visibility = visibility
        binding.bottomBar.visibility = visibility
        
        // Controlar barras del sistema
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        if (isUIVisible) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
    
    /**
     * Muestra información del archivo multimedia actual
     */
    private fun showMediaInfo() {
        val currentMedia = mediaItems.getOrNull(currentPosition) ?: return
        
        val info = buildString {
            append("Nombre: ${currentMedia.name}\n")
            append("Tamaño: ${formatFileSize(currentMedia.size)}\n")
            append("Tipo: ${currentMedia.mimeType}\n")
            if (currentMedia.isVideo && currentMedia.duration > 0) {
                append("Duración: ${formatDuration(currentMedia.duration)}\n")
            }
            if (currentMedia.width > 0 && currentMedia.height > 0) {
                append("Resolución: ${currentMedia.width} x ${currentMedia.height}")
            }
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Información del archivo")
            .setMessage(info)
            .setPositiveButton("OK", null)
            .show()
    }
    
    /**
     * Comparte el archivo multimedia actual
     */
    private fun shareCurrentMedia() {
        val currentMedia = mediaItems.getOrNull(currentPosition) ?: return
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = currentMedia.mimeType
            putExtra(Intent.EXTRA_STREAM, android.net.Uri.parse(currentMedia.path))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        startActivity(Intent.createChooser(shareIntent, "Compartir"))
    }
    
    /**
     * Elimina el archivo multimedia actual
     */
    private fun deleteCurrentMedia() {
        val currentMedia = mediaItems.getOrNull(currentPosition) ?: return
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar archivo")
            .setMessage("¿Estás seguro de que quieres eliminar este archivo?")
            .setPositiveButton("Eliminar") { _, _ ->
                // Aquí implementarías la lógica de eliminación
                // Por simplicidad, solo mostramos un mensaje
                android.widget.Toast.makeText(this, "Función de eliminación no implementada", android.widget.Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    /**
     * Formatea el tamaño del archivo
     */
    private fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1 -> String.format("%.1f GB", gb)
            mb >= 1 -> String.format("%.1f MB", mb)
            kb >= 1 -> String.format("%.1f KB", kb)
            else -> "$bytes bytes"
        }
    }
    
    /**
     * Formatea la duración del video
     */
    private fun formatDuration(durationMs: Long): String {
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        val remainingSeconds = seconds % 60
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, remainingMinutes, remainingSeconds)
        } else {
            String.format("%d:%02d", remainingMinutes, remainingSeconds)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}