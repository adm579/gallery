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
import com.samsung.gallery.databinding.ItemAlbumBinding
import com.samsung.gallery.models.Album
import com.samsung.gallery.models.AlbumType

/**
 * Adapter para mostrar álbumes en un RecyclerView con grid
 */
class AlbumAdapter(
    private val onAlbumClick: (Album) -> Unit
) : ListAdapter<Album, AlbumAdapter.AlbumViewHolder>(AlbumDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AlbumViewHolder(
        private val binding: ItemAlbumBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.apply {
                // Configurar nombre del álbum
                textAlbumName.text = album.name

                // Configurar contador de archivos multimedia
                val countText = when {
                    album.mediaCount == 1 -> "1 elemento"
                    else -> "${album.mediaCount} elementos"
                }
                textMediaCount.text = countText

                // Configurar icono según el tipo de álbum
                val iconRes = when (album.type) {
                    AlbumType.CAMERA -> R.drawable.ic_camera
                    AlbumType.SCREENSHOTS -> R.drawable.ic_screenshot
                    AlbumType.WHATSAPP_IMAGES, AlbumType.WHATSAPP_VIDEO -> R.drawable.ic_whatsapp
                    AlbumType.DOWNLOADS -> R.drawable.ic_download
                    AlbumType.VIDEOS -> R.drawable.ic_video
                    AlbumType.REGULAR -> R.drawable.ic_folder
                }
                imageAlbumIcon.setImageResource(iconRes)

                // Cargar imagen de portada con Glide
                if (album.coverImagePath.isNotBlank()) {
                    Glide.with(imageAlbumCover.context)
                        .load(album.coverImagePath)
                        .transform(CenterCrop(), RoundedCorners(16))
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(imageAlbumCover)
                } else {
                    imageAlbumCover.setImageResource(R.drawable.ic_image_placeholder)
                }

                // Configurar click listener
                root.setOnClickListener {
                    onAlbumClick(album)
                }
            }
        }
    }
}

/**
 * DiffUtil callback para optimizar las actualizaciones del RecyclerView
 */
class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
    override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem == newItem
    }
}