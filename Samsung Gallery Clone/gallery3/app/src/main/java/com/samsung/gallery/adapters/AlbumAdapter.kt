package com.example.samsunggalleryclone.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.samsunggalleryclone.R
import com.example.samsunggalleryclone.databinding.ItemAlbumBinding
import com.example.samsunggalleryclone.models.Album
import com.example.samsunggalleryclone.models.AlbumType
import java.io.File

/**
 * Adapter para mostrar la lista de álbumes en un RecyclerView
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
                textAlbumName.text = album.name
                textMediaCount.text = album.mediaCount.toString()
                imageAlbumIcon.setImageResource(getAlbumIcon(album.type))
                loadAlbumCover(album.coverImagePath)

                root.setOnClickListener {
                    onAlbumClick(album)
                }
            }
        }

        private fun loadAlbumCover(imagePath: String) {
            val context = binding.imageAlbumCover.context

            val imageSource = when {
                imagePath.startsWith("content://") -> Uri.parse(imagePath)
                File(imagePath).exists() -> File(imagePath).toUri()
                else -> null
            }

            Glide.with(context)
                .load(imageSource ?: R.drawable.ic_image_placeholder)
                .transform(CenterCrop(), RoundedCorners(24))
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(binding.imageAlbumCover)
        }

        private fun getAlbumIcon(type: AlbumType): Int {
            return when (type) {
                AlbumType.CAMERA -> R.drawable.ic_camera
                AlbumType.SCREENSHOTS -> R.drawable.ic_screenshot
                AlbumType.WHATSAPP_IMAGES -> R.drawable.ic_whatsapp
                AlbumType.WHATSAPP_VIDEO -> R.drawable.ic_whatsapp
                AlbumType.DOWNLOADS -> R.drawable.ic_download
                AlbumType.VIDEOS -> R.drawable.ic_video
                AlbumType.REGULAR -> R.drawable.ic_folder
            }
        }
    }
}

/**
 * DiffUtil para optimizar cambios en RecyclerView
 */
class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
    override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem == newItem
    }
}
