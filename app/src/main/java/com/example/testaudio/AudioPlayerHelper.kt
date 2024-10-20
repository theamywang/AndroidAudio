package com.example.testaudio

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlin.math.max
import kotlin.math.min

class AudioPlayerHelper(private val context: Context) {
//    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
//
//    init {
//        exoPlayer.addListener(
//            object : Player.Listener {
//                override fun onPlaybackStateChanged(playbackState: Int) {
//                    if (playbackState == Player.STATE_ENDED) {
//                        _isPlaying.value = false
//                    }
//                }
//            }
//        )
//    }

    fun setAudio(base64String: String) {
        val sessionToken =
            SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            val mediaItem =
                MediaItem.Builder()
                    // Set a unique media ID for the media item
                    .setMediaId("media-1")
                    // Set the URI (Uniform Resource Identifier) for the media content
                    .setUri("data:audio/mpeg;base64," + base64String)
                    // Set the media metadata, which includes details about the media content
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            // Set the artist's name for the media content
                            .setArtist("David Bowie")
                            // Set the title of the media content
                            .setTitle("Heroes")
                            .build()
                    )
                    .build()

            mediaController?.setMediaItem(mediaItem)
            mediaController?.prepare()
            mediaController?.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        val isPaused = mediaController?.isPlaying != true
                        if (playbackState == Player.STATE_ENDED || isPaused) {
                            _isPlaying.value = false
                        }
                    }
                }
            )
        }, MoreExecutors.directExecutor())

//        exoPlayer.setMediaItem(
//            MediaItem.fromUri(
//                "data:audio/mpeg;base64," + base64String
//            )
//        )
//        exoPlayer.prepare()
    }

    fun calibrateIsPlaying() {
        _isPlaying.value = mediaController?.isPlaying == true
    }

    fun setPlaybackSpeed(rate: Float) {
//        exoPlayer.setPlaybackSpeed(rate)
        mediaController?.setPlaybackSpeed(rate)
    }

    fun play() {
        if (_isPlaying.value == true) { return }

        mediaController?.let {
            if (it.currentPosition >= it.duration) {
                it.seekTo(0)
            }
            it.play()
        }
//        if (exoPlayer.currentPosition >= exoPlayer.duration) {
//            exoPlayer.seekTo(0)
//        }
        _isPlaying.value = true
//        mediaController?.play()
    }

    fun fastForward(seconds: Int) {
//        val currentPosition = exoPlayer.currentPosition
//        val newPosition = currentPosition + seconds * 1000

//        exoPlayer.seekTo(min(newPosition, exoPlayer.duration))
        mediaController?.let {
            val currentPosition = it.currentPosition
            val newPosition = currentPosition + seconds * 1000
            it.seekTo(min(newPosition, it.duration))
        }
    }

    fun rewind(seconds: Int) {
//        val currentPosition = exoPlayer.currentPosition
//        val newPosition = currentPosition - seconds * 1000
//
//        exoPlayer.seekTo(max(0, newPosition))
        mediaController?.let {
            val currentPosition = it.currentPosition
            val newPosition = currentPosition - seconds * 1000

            it.seekTo(max(0, newPosition))
        }
    }

    fun pause() {
        _isPlaying.value = false
//        exoPlayer.pause()
        mediaController?.pause()
    }

    fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController?.release()
//        exoPlayer.release()
        mediaController?.release()
    }
}