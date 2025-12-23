package com.example.samsunggalleryclone.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.samsunggalleryclone.models.Album
import com.example.samsunggalleryclone.utils.MediaScanner
import com.example.samsunggalleryclone.utils.PermissionUtils
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal que muestra los álbumes
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> = _albums

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasPermissions = MutableLiveData<Boolean>()
    val hasPermissions: LiveData<Boolean> = _hasPermissions

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        Log.d(TAG, "ViewModel initialized")
        checkPermissions()
    }

    /**
     * Verifica si los permisos están concedidos
     */
    fun checkPermissions() {
        val hasPermissions = PermissionUtils.hasStoragePermissions(getApplication())
        Log.d(TAG, "Has permissions: $hasPermissions")
        _hasPermissions.value = hasPermissions

        if (hasPermissions) {
            Log.d(TAG, "Permissions granted, loading albums...")
            loadAlbums()
        } else {
            Log.d(TAG, "Permissions not granted")
        }
    }

    /**
     * Carga todos los álbumes del dispositivo
     */
    fun loadAlbums() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                Log.d(TAG, "Starting to load albums...")

                val albumList = MediaScanner.scanAlbums(getApplication())
                Log.d(TAG, "Albums loaded successfully: ${albumList.size} albums found")

                // Log para debugging
                albumList.forEachIndexed { index, album ->
                    Log.d(TAG, "Album $index: ${album.name}, Count: ${album.mediaCount}, Type: ${album.type}")
                }

                _albums.value = albumList

            } catch (e: Exception) {
                Log.e(TAG, "Error loading albums", e)
                _error.value = "Error al cargar los álbumes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresca la lista de álbumes
     */
    fun refreshAlbums() {
        Log.d(TAG, "Refreshing albums...")
        loadAlbums()
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _error.value = null
    }
}