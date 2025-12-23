package com.example.samsunggalleryclone.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.samsunggalleryclone.models.MediaItem
import com.example.samsunggalleryclone.utils.MediaScanner
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de álbum que muestra los archivos multimedia
 */
class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _mediaItems = MutableLiveData<List<MediaItem>>()
    val mediaItems: LiveData<List<MediaItem>> = _mediaItems
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _albumName = MutableLiveData<String>()
    val albumName: LiveData<String> = _albumName
    
    /**
     * Carga los archivos multimedia de un álbum específico
     */
    fun loadMediaItems(bucketId: String, albumName: String) {
        _albumName.value = albumName
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val mediaList = MediaScanner.getMediaItemsForAlbum(getApplication(), bucketId)
                _mediaItems.value = mediaList
                
            } catch (e: Exception) {
                _error.value = "Error al cargar los archivos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refresca la lista de archivos multimedia
     */
    fun refreshMediaItems(bucketId: String, albumName: String) {
        loadMediaItems(bucketId, albumName)
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _error.value = null
    }
}