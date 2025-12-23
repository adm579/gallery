package com.samsung.gallery.ui.viewer

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.samsung.gallery.databinding.FragmentMediaViewerBinding
import com.samsung.gallery.models.MediaItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem as ExoMediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class MediaViewerFragment : Fragment() {

    companion object {
        private const val ARG_MEDIA_ITEM = "arg_media_item"

        fun newInstance(mediaItem: MediaItem): MediaViewerFragment {
            return MediaViewerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_MEDIA_ITEM, mediaItem)
                }
            }
        }
    }

    private var _binding: FragmentMediaViewerBinding? = null
    private val binding get() = _binding!!

    private var mediaItem: MediaItem? = null
    private var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaItem = arguments?.getParcelable(ARG_MEDIA_ITEM)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaItem?.let { media ->
            if (media.isImage) setupImage(media)
            else if (media.isVideo) setupVideo(media)
        }
    }

    private fun setupImage(media: MediaItem) {
        binding.imageView.isVisible = true
        binding.playerView.isVisible = false

        // Cargar imagen con Glide
        Glide.with(this)
            .load(media.path)
            .into(binding.imageView)
    }

    private fun setupVideo(media: MediaItem) {
        binding.imageView.isVisible = false
        binding.playerView.isVisible = true

        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = exoPlayer

        val dataSourceFactory = DefaultDataSource.Factory(requireContext())
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(ExoMediaItem.fromUri(Uri.parse(media.path)))

        exoPlayer?.apply {
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = false
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.release()
        exoPlayer = null
        _binding = null
    }
}
