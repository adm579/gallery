package com.samsung.gallery.models

/**
 * Modelo de datos para representar un álbum de fotos/videos
 */
data class Album(
    val id: Long,
    val name: String,
    val bucketId: String,
    val coverImagePath: String,
    val mediaCount: Int,
    val type: AlbumType = AlbumType.REGULAR
)

/**
 * Tipos de álbumes especiales
 */
enum class AlbumType {
    CAMERA,
    SCREENSHOTS,
    WHATSAPP_IMAGES,
    WHATSAPP_VIDEO,
    DOWNLOADS,
    VIDEOS,
    REGULAR
}