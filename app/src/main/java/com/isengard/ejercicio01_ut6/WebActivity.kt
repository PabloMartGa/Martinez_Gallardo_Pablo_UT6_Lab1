package com.isengard.ejercicio01_ut6

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        webView = findViewById(R.id.webViewPalantir)

        // 1. Configuraciones críticas para evitar que el vídeo salte fuera de la App
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // 2. Habilitación de JavaScript (Imprescindible para el reproductor de YouTube)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true // Permite almacenar configuraciones locales del reproductor
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true

        // 3. ID extraído de la URL proporcionada (R4qfx3ZMSB8)
        val videoId = "R4qfx3ZMSB8"

        // 4. Código HTML con el Iframe oficial de YouTube encapsulado y adaptado al 100% de la vista
        val iframeHtml = """
            <html>
            <head>
                <style>
                    body { margin: 0; padding: 0; background-color: #000000; }
                    .video-container { position: relative; width: 100%; height: 100%; }
                    iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }
                </style>
            </head>
            <body>
                <div class="video-container">
                    <iframe src="https://youtube.com" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
                </div>
            </body>
            </html>
        """.trimIndent()

        // 5. Carga de los datos multimedia en el motor del WebView
        webView.loadDataWithBaseURL("https://www.youtube.com", iframeHtml, "text/html", "utf-8", null)
    }

    // Liberamos memoria al salir para frenar procesos en segundo plano
    override fun onDestroy() {
        webView.stopLoading()
        webView.destroy()
        super.onDestroy()
    }
}