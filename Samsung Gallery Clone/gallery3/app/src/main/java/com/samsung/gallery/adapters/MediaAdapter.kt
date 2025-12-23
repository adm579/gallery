package com.samsung.gallery.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.samsung.gallery.R
import com.samsung.gallery.databinding.ItemMediaBinding
import com.samsung.gallery.models.MediaItem

/**
 * Adapter para mostrar archivos multimedia en un RecyclerView con grid
 */
class MediaAdapter(
    private val onMediaClick: (MediaItem, Int) -> Unit
) : ListAdapter<MediaItem, MediaAdapter.MediaViewHolder>(MediaDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
    
    inner class MediaViewHolder(
        private val binding: ItemMediaBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(mediaItem: MediaItem, position: Int) {
            binding.apply {
                // Mostrar indicador de video si es necesario
                imageVideoIndicator.isVisible = mediaItem.isVideo
                
                // Mostrar duración del video si es necesario
                if (mediaItem.isVideo && mediaItem.duration > 0) {
                    textVideoDuration.isVisible = true
                    textVideoDuration.text = formatDuration(mediaItem.duration)
                } else {
                    textVideoDuration.isVisible = false
                }
                
                // Cargar imagen/thumbnail con Glide
                Glide.with(imageMedia.context)
                    .load(mediaItem.path)
                    .transform(CenterCrop(), RoundedCorners(12))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(imageMedia)
                
                // Configurar click listener
                root.setOnClickListener {
                    onMediaClick(mediaItem, position)
                }
            }
        }
        
        /**
         * Formatea la duración del video en formato MM:SS
         */
        private fun formatDuration(durationMs: Long): String {
            val seconds = durationMs / 1000
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return String.format("%d:%02d", minutes, remainingSeconds)
        }
    }
}

/**
 * DiffUtil callback para optimizar las actualizaciones del RecyclerView
 */
class MediaDiffCallback : DiffUtil.ItemCallback<MediaItem>() {
    override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
        return oldItem == newItem
    }
}