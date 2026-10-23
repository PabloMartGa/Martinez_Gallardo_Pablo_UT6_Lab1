package com.isengard.ejercicio01_ut6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnIrAudio = findViewById<Button>(R.id.btnIrAudio)
        val btnIrCampanas = findViewById<Button>(R.id.btnIrCampanas)
        val btnIrVideo = findViewById<Button>(R.id.btnIrVideo);
        val btnIrWeb = findViewById<Button>(R.id.btnIrWeb);

        btnIrAudio.setOnClickListener {
            val intent = Intent(this, AudioActivity::class.java)
            startActivity(intent)
        }

        btnIrCampanas.setOnClickListener {
            val intent = Intent(this, CampanasActivity::class.java)
            startActivity(intent)
        }

        btnIrVideo.setOnClickListener {
            val intent = Intent(this, VideoActivity ::class.java)
            startActivity(intent)
        }

        btnIrWeb.setOnClickListener {
            val intent = Intent(this, WebActivity ::class.java)
            startActivity(intent)
        }
    }
}