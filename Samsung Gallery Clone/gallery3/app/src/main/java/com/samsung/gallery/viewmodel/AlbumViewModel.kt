package com.samsung.gallery.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.samsung.gallery.models.MediaItem
import com.samsung.gallery.utils.MediaScanner
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de álbum que muestra los archivos multimedia
 */
class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "AlbumViewModel"
    }

    private val _mediaItems = MutableLiveData<List<MediaItem>>()
    val mediaItems: LiveData<List<MediaItem>> = _mediaItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Carga los archivos multimedia de un álbum específico
     */
    fun loadMediaItems(bucketId: String, albumName: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                Log.d(TAG, "Loading media items for album: $albumName (bucketId: $bucketId)")

                val mediaList = MediaScanner.getMediaItemsForAlbum(getApplication(), bucketId)
                Log.d(TAG, "Media items loaded successfully: ${mediaList.size} items found")

                _mediaItems.value = mediaList

            } catch (e: Exception) {
                Log.e(TAG, "Error loading media items for album: $albumName", e)
                _error.value = "Error al cargar los archivos multimedia: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresca los archivos multimedia del álbum
     */
    fun refreshMediaItems(bucketId: String, albumName: String) {
        Log.d(TAG, "Refreshing media items for album: $albumName")
        loadMediaItems(bucketId, albumName)
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _error.value = null
    }
}