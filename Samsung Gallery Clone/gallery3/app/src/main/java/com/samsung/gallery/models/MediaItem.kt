package com.example.samsunggalleryclone.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de datos para representar un archivo multimedia (imagen o video)
 */
@Parcelize
data class MediaItem(
    val id: Long,
    val path: String,
    val name: String,
    val dateAdded: Long,
    val size: Long,
    val mimeType: String,
    val bucketId: String,
    val bucketName: String,
    val duration: Long = 0, // Para videos
    val width: Int = 0,
    val height: Int = 0
) : Parcelable {
    /**
     * Determina si el archivo es un video
     */
    val isVideo: Boolean
        get() = mimeType.startsWith("video/")

    /**
     * Determina si el archivo es una imagen
     */
    val isImage: Boolean
        get() = mimeType.startsWith("image/")
}
