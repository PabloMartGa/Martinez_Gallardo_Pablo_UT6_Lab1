package com.isengard.ejercicio01_ut6

import com.isengard.ejercicio01_ut6.R
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Laboratorio 3 - La Forja del Servidor Central Multimedia.
 *
 * La idea, contada en cristiano: el operador elige un puesto de transmisión
 * en un desplegable, la app lo reproduce con ExoPlayer y un texto de abajo
 * le va diciendo cómo va la cosa (cargando, en vivo, terminada). Si la señal
 * se acaba, suena además una alarma de campanas con un MediaPlayer, a la vez
 * que el vídeo sigue en pantalla, para que el operador se entere sin mirar.
 */
class MainActivity : AppCompatActivity() {

    // Las vistas del layout. Las dejamos "lateinit" porque no existen hasta
    // que se infla el XML en onCreate, pero nunca se usan antes de eso.
    private lateinit var spinnerCanales: Spinner
    private lateinit var playerView: PlayerView
    private lateinit var textoEstado: TextView

    // Motor de vídeo (ExoPlayer) y motor de la alarma (MediaPlayer).
    // Los dos son nullable a propósito: el de vídeo lo creamos al arrancar y
    // lo ponemos a null al liberarlo; el de la alarma solo existe mientras suena.
    private var exoPlayer: ExoPlayer? = null
    private var alarma: MediaPlayer? = null

    /**
     * Mapa de canales: nombre que ve el operador -> URL del vídeo .mp4.
     * Usamos LinkedHashMap para que el desplegable salga siempre en el mismo
     * orden en que los escribimos (un HashMap normal no lo garantiza).
     *
     * OJO: estas URLs son de ejemplo (vídeos públicos de prueba). Hay que
     * sustituirlas por las que facilite la cátedra.
     */
    private val canales: LinkedHashMap<String, String> = linkedMapOf(
        "Minas Tirith" to "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "Isengard" to "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "Rivendell" to "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enlazamos las vistas con su id del XML.
        spinnerCanales = findViewById(R.id.spinnerCanales)
        playerView = findViewById(R.id.playerView)
        textoEstado = findViewById(R.id.textoEstado)

        prepararReproductor()
        prepararSelector()
    }

    /**
     * Crea el ExoPlayer, lo conecta a la PlayerView y le cuelga el "oyente"
     * que se entera de todo lo que le pasa al buffer y al estado de la señal.
     */
    private fun prepararReproductor() {
        val player = ExoPlayer.Builder(this).build()
        playerView.player = player
        player.addListener(oyenteDeEstado)
        exoPlayer = player
    }

    /**
     * Rellena el Spinner con los nombres de los canales y reacciona cuando
     * el operador elige otro.
     */
    private fun prepararSelector() {
        // Solo nos interesan los nombres para el desplegable.
        val nombres = canales.keys.toList()
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCanales.adapter = adaptador

        spinnerCanales.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val nombre = nombres[position]
                canales[nombre]?.let { url -> sintonizar(url) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Con un desplegable que siempre tiene algo marcado esto no ocurre.
            }
        }
    }

    /**
     * Cambia de canal: carga la URL nueva en el reproductor y le da al play.
     */
    private fun sintonizar(url: String) {
        // Si el canal anterior había terminado y la alarma seguía sonando,
        // la cortamos: ya estamos en otra señal y no tiene sentido que siga.
        liberarAlarma()

        exoPlayer?.let { player ->
            player.setMediaItem(MediaItem.fromUri(url)) // fuente de datos nueva
            player.prepare()                            // empieza a pedir y bufferizar
            player.playWhenReady = true                 // y que arranque en cuanto pueda
        }
    }

    /**
     * El "chivato" del reproductor. ExoPlayer llama a estos métodos solo
     * cuando algo cambia, así que no hace falta ir preguntándole nada.
     */
    private val oyenteDeEstado = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                // Está descargando datos y todavía no puede reproducir.
                Player.STATE_BUFFERING ->
                    textoEstado.setText(R.string.estado_cargando)

                // Ya tiene suficiente búfer: se ve el vídeo.
                Player.STATE_READY ->
                    textoEstado.setText(R.string.estado_en_vivo)

                // El vídeo se acabó: avisamos en texto Y con sonido a la vez.
                Player.STATE_ENDED -> {
                    textoEstado.setText(R.string.estado_terminada)
                    dispararAlarma()
                }
            }
        }

        // Extra: si la señal se "corta" de verdad ExoPlayer
        // no llega a STATE_ENDED, sino que da error.
        override fun onPlayerError(error: PlaybackException) {
            textoEstado.setText(R.string.estado_terminada)
            dispararAlarma()
        }
    }

    /**
     * Hace sonar las campanas. Usa un MediaPlayer propio, independiente de
     * ExoPlayer, y por eso suena en paralelo sin molestar al vídeo.
     */
    private fun dispararAlarma() {
        // Por si llegan dos avisos seguidos evitamos que
        // suenen dos alarmas pisándose y que se nos escape un MediaPlayer.
        liberarAlarma()

        // create() ya deja el audio preparado, así que se puede lanzar al momento.
        alarma = MediaPlayer.create(this, R.raw.campanas_alarma)?.apply {
            // Cuando termina de sonar, soltamos sus recursos nosotros mismos.
            setOnCompletionListener { liberarAlarma() }
            start()
        }
    }

    /**
     * Para y libera el MediaPlayer de la alarma si existe. Se puede llamar
     * las veces que haga falta: si no hay alarma, no hace nada.
     */
    private fun liberarAlarma() {
        alarma?.let { reproductor ->
            // isPlaying puede lanzar excepción si el player está en un estado raro,
            // por eso lo envolvemos: al liberar queremos ir a lo seguro.
            try {
                if (reproductor.isPlaying) reproductor.stop()
            } catch (_: IllegalStateException) {
                // Ya estaba parado o liberado; seguimos con el release.
            }
            reproductor.release()
        }
        alarma = null
    }

    override fun onStop() {
        super.onStop()
        // Si la app pasa a segundo plano, pausamos el vídeo para no gastar
        // datos ni batería viendo algo que nadie mira.
        exoPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cierre limpio: devolvemos al sistema la memoria y los códecs de
        // hardware de los DOS motores. Si no, quedarían reservados.
        exoPlayer?.removeListener(oyenteDeEstado)
        exoPlayer?.release()
        exoPlayer = null
        playerView.player = null

        liberarAlarma()
    }
}