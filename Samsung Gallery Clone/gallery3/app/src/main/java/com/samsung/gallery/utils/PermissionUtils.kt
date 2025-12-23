package com.samsung.gallery.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Utilidad para manejar permisos de almacenamiento según la versión de Android
 */
object PermissionUtils {

    /**
     * Obtiene los permisos necesarios según la versión de Android
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            // Android 12 y menor
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    /**
     * Verifica si todos los permisos necesarios están concedidos
     */
    fun hasStoragePermissions(context: Context): Boolean {
        val permissions = getRequiredPermissions()
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Obtiene el mensaje explicativo para los permisos
     */
    fun getPermissionExplanation(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            "Esta aplicación necesita acceso a tus fotos y videos para mostrar tu galería."
        } else {
            "Esta aplicación necesita acceso al almacenamiento para mostrar tus fotos y videos."
        }
    }
}