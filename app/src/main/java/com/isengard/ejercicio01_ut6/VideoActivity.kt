package com.isengard.ejercicio01_ut6

import android.net.Uri
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class VideoActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    // URL de ejemplo en streaming (puedes sustituirla por tu archivo de pruebas o streaming HTTP/HTTPS)
    private val videoStreamingUrl = "https://www.youtube.com/watch?v=QDia3e12czc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        playerView = findViewById(R.id.playerViewPalantir)
    }

    private fun initializePlayer() {
        // 1. Instanciamos el motor del reproductor ExoPlayer
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer

            // 2. Creamos el item multimedia a partir de la URL de Red
            val mediaItem = MediaItem.fromUri(Uri.parse(videoStreamingUrl))
            exoPlayer.setMediaItem(mediaItem)

            // 3. Preparamos el búfer y configuramos el auto-play
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    private fun releasePlayer() {
        // Liberación segura de recursos para evitar fugas de memoria
        player?.let { exoPlayer ->
            exoPlayer.release()
        }
        player = null
    }

    // --- Control del Ciclo de Vida de Android ---
    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (player == null) {
            initializePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}