package com.example.samsunggalleryclone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.samsunggalleryclone.models.MediaItem
import com.example.samsunggalleryclone.ui.viewer.MediaViewerFragment

/**
 * Adapter para ViewPager2 que muestra archivos multimedia en pantalla completa
 */
class MediaViewerAdapter(
    fragmentActivity: FragmentActivity,
    private val mediaItems: List<MediaItem>
) : FragmentStateAdapter(fragmentActivity) {
    
    override fun getItemCount(): Int = mediaItems.size
    
    override fun createFragment(position: Int): Fragment {
        return MediaViewerFragment.newInstance(mediaItems[position])
    }
    
    /**
     * Obtiene el MediaItem en la posición especificada
     */
    fun getMediaItem(position: Int): MediaItem? {
        return if (position in 0 until mediaItems.size) {
            mediaItems[position]
        } else {
            null
        }
    }
}