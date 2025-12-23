package com.samsung.gallery.utils

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.samsung.gallery.models.Album
import com.samsung.gallery.models.AlbumType
import com.samsung.gallery.models.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utilidad para escanear y obtener archivos multimedia del dispositivo
 */
object MediaScanner {
    private const val TAG = "MediaScanner"

    /**
     * Escanea todos los álbumes del dispositivo
     */
    suspend fun scanAlbums(context: Context): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableListOf<Album>()
        val contentResolver = context.contentResolver

        try {
            // Obtener álbumes combinando imágenes y videos
            val albumMap = mutableMapOf<String, Album>()

            // Obtener álbumes de imágenes
            val imageAlbums = getAlbumsFromImages(contentResolver)
            imageAlbums.forEach { album ->
                albumMap[album.bucketId] = album.copy(
                    mediaCount = albumMap[album.bucketId]?.mediaCount?.plus(album.mediaCount) ?: album.mediaCount
                )
            }

            // Obtener álbumes de videos
            val videoAlbums = getAlbumsFromVideos(contentResolver)
            videoAlbums.forEach { album ->
                val existingAlbum = albumMap[album.bucketId]
                if (existingAlbum != null) {
                    albumMap[album.bucketId] = existingAlbum.copy(
                        mediaCount = existingAlbum.mediaCount + album.mediaCount,
                        coverImagePath = if (existingAlbum.coverImagePath.isBlank()) album.coverImagePath else existingAlbum.coverImagePath
                    )
                } else {
                    albumMap[album.bucketId] = album
                }
            }

            albums.addAll(albumMap.values)

            // Ordenar álbumes: especiales primero, luego por nombre
            albums.sortedWith(compareBy<Album> {
                if (it.type == AlbumType.REGULAR) 1 else 0
            }.thenBy { it.name })

        } catch (e: Exception) {
            Log.e(TAG, "Error scanning albums", e)
            albums.clear()
        }

        Log.d(TAG, "Total albums found: ${albums.size}")
        albums
    }

    /**
     * Obtiene álbumes desde imágenes
     */
    private fun getAlbumsFromImages(contentResolver: ContentResolver): List<Album> {
        val albums = mutableListOf<Album>()

        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val bucketIdColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            val albumMap = mutableMapOf<String, Album>()

            while (it.moveToNext()) {
                try {
                    val bucketId = it.getString(bucketIdColumn)
                    val bucketName = it.getString(bucketNameColumn) ?: "Unknown"
                    val imagePath = it.getString(dataColumn) ?: ""

                    if (bucketId != null && bucketName.isNotBlank()) {
                        val existingAlbum = albumMap[bucketId]
                        if (existingAlbum == null) {
                            // Primer elemento de este bucket
                            albumMap[bucketId] = Album(
                                id = bucketId.hashCode().toLong(),
                                name = bucketName,
                                bucketId = bucketId,
                                coverImagePath = imagePath,
                                mediaCount = 1,
                                type = determineAlbumType(bucketName)
                            )
                        } else {
                            // Incrementar contador
                            albumMap[bucketId] = existingAlbum.copy(
                                mediaCount = existingAlbum.mediaCount + 1
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error processing image album", e)
                }
            }

            albums.addAll(albumMap.values)
        }

        Log.d(TAG, "Image albums found: ${albums.size}")
        return albums
    }

    /**
     * Obtiene álbumes desde videos
     */
    private fun getAlbumsFromVideos(contentResolver: ContentResolver): List<Album> {
        val albums = mutableListOf<Album>()

        val projection = arrayOf(
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATA
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val bucketIdColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketNameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            val albumMap = mutableMapOf<String, Album>()

            while (it.moveToNext()) {
                try {
                    val bucketId = it.getString(bucketIdColumn)
                    val bucketName = it.getString(bucketNameColumn) ?: "Unknown"
                    val videoPath = it.getString(dataColumn) ?: ""

                    if (bucketId != null && bucketName.isNotBlank()) {
                        val existingAlbum = albumMap[bucketId]
                        if (existingAlbum == null) {
                            // Primer elemento de este bucket
                            albumMap[bucketId] = Album(
                                id = bucketId.hashCode().toLong(),
                                name = bucketName,
                                bucketId = bucketId,
                                coverImagePath = videoPath,
                                mediaCount = 1,
                                type = determineAlbumType(bucketName)
                            )
                        } else {
                            // Incrementar contador
                            albumMap[bucketId] = existingAlbum.copy(
                                mediaCount = existingAlbum.mediaCount + 1
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error processing video album", e)
                }
            }

            albums.addAll(albumMap.values)
        }

        Log.d(TAG, "Video albums found: ${albums.size}")
        return albums
    }

    /**
     * Obtiene todos los archivos multimedia de un álbum específico
     */
    suspend fun getMediaItemsForAlbum(context: Context, bucketId: String): List<MediaItem> =
        withContext(Dispatchers.IO) {
            val mediaItems = mutableListOf<MediaItem>()
            val contentResolver = context.contentResolver

            try {
                // Obtener imágenes
                mediaItems.addAll(getImagesForBucket(contentResolver, bucketId))

                // Obtener videos
                mediaItems.addAll(getVideosForBucket(contentResolver, bucketId))

                // Ordenar por fecha (más reciente primero)
                mediaItems.sortedByDescending { it.dateAdded }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting media items for album: $bucketId", e)
            }

            Log.d(TAG, "Media items found for bucket $bucketId: ${mediaItems.size}")
            mediaItems
        }

    /**
     * Obtiene imágenes para un bucket específico
     */
    private fun getImagesForBucket(contentResolver: ContentResolver, bucketId: String): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val mimeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
            val bucketIdColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val widthColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            } else -1
            val heightColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            } else -1

            while (it.moveToNext()) {
                try {
                    val mediaItem = MediaItem(
                        id = it.getLong(idColumn),
                        path = it.getString(dataColumn),
                        name = it.getString(nameColumn),
                        dateAdded = it.getLong(dateColumn),
                        size = it.getLong(sizeColumn),
                        mimeType = it.getString(mimeColumn),
                        bucketId = it.getString(bucketIdColumn),
                        bucketName = it.getString(bucketNameColumn),
                        width = if (widthColumn >= 0) it.getInt(widthColumn) else 0,
                        height = if (heightColumn >= 0) it.getInt(heightColumn) else 0
                    )
                    mediaItems.add(mediaItem)
                } catch (e: Exception) {
                    Log.w(TAG, "Error processing image", e)
                }
            }
        }

        return mediaItems
    }

    /**
     * Obtiene videos para un bucket específico
     */
    private fun getVideosForBucket(contentResolver: ContentResolver, bucketId: String): List<MediaItem> {
        val mediaItems = mutableListOf<MediaItem>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )

        val selection = "${MediaStore.Video.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val mimeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
            val bucketIdColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
            val bucketNameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val durationColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            } else -1
            val widthColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            } else -1
            val heightColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            } else -1

            while (it.moveToNext()) {
                try {
                    val mediaItem = MediaItem(
                        id = it.getLong(idColumn),
                        path = it.getString(dataColumn),
                        name = it.getString(nameColumn),
                        dateAdded = it.getLong(dateColumn),
                        size = it.getLong(sizeColumn),
                        mimeType = it.getString(mimeColumn),
                        bucketId = it.getString(bucketIdColumn),
                        bucketName = it.getString(bucketNameColumn),
                        duration = if (durationColumn >= 0) it.getLong(durationColumn) else 0L,
                        width = if (widthColumn >= 0) it.getInt(widthColumn) else 0,
                        height = if (heightColumn >= 0) it.getInt(heightColumn) else 0
                    )
                    mediaItems.add(mediaItem)
                } catch (e: Exception) {
                    Log.w(TAG, "Error processing video", e)
                }
            }
        }

        return mediaItems
    }

    /**
     * Determina el tipo de álbum basado en el nombre
     */
    private fun determineAlbumType(bucketName: String): AlbumType {
        return when {
            bucketName.equals("Camera", ignoreCase = true) ||
                    bucketName.equals("DCIM", ignoreCase = true) -> AlbumType.CAMERA

            bucketName.contains("Screenshot", ignoreCase = true) -> AlbumType.SCREENSHOTS

            bucketName.contains("WhatsApp") && bucketName.contains("Images", ignoreCase = true) ->
                AlbumType.WHATSAPP_IMAGES

            bucketName.contains("WhatsApp") && bucketName.contains("Video", ignoreCase = true) ->
                AlbumType.WHATSAPP_VIDEO

            bucketName.equals("Download", ignoreCase = true) ||
                    bucketName.equals("Downloads", ignoreCase = true) -> AlbumType.DOWNLOADS

            bucketName.equals("Movies", ignoreCase = true) ||
                    bucketName.equals("Videos", ignoreCase = true) -> AlbumType.VIDEOS

            else -> AlbumType.REGULAR
        }
    }
}