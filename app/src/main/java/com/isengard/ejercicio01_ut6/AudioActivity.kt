package com.isengard.ejercicio01_ut6

import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class AudioActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        val btnPlay = findViewById<Button>(R.id.btnPlayAudio)
        val btnPause = findViewById<Button>(R.id.btnPauseAudio)

        // Inicialización estática: Pasa automáticamente por los estados Idle -> Initialized -> Prepared
        mediaPlayer = MediaPlayer.create(this, R.raw.laud_finrod)

        btnPlay.setOnClickListener {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start() // Estado Started
                Toast.makeText(this, "Sonando: El cantar de Beleriand", Toast.LENGTH_SHORT).show()
            }
        }

        btnPause.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause() // Estado Paused
            }
        }
    }
}