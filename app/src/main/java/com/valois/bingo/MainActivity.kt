package com.valois.bingo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import android.widget.ImageButton

import android.media.MediaPlayer
class MainActivity : AppCompatActivity() {

    private lateinit var numberCircle: TextView
    private lateinit var buttonGirar: Button
    private lateinit var buttonRecomecar: Button
    private lateinit var buttonMusicToggle: ImageButton // Botão para música
    private val calledNumbers = mutableSetOf<Int>()
    private val numberCells = mutableMapOf<Int, TextView>()
    private var mediaPlayerBackground: MediaPlayer? = null // Música de fundo
    private var isMusicPlaying = true // Controle do estado da música

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializando componentes
        numberCircle = findViewById(R.id.numberCircle)
        buttonGirar = findViewById(R.id.buttonGirar)
        buttonRecomecar = findViewById(R.id.buttonRecomecar)
        buttonMusicToggle = findViewById(R.id.buttonMusicToggle) // Inicializar o botão
        val tableLayout: TableLayout = findViewById(R.id.numberTable)

        // Iniciando música de fundo
        startBackgroundMusic()

        // Configurando botão de alternar música
        buttonMusicToggle.setOnClickListener {
            toggleBackgroundMusic()
        }

        // Criando tabela de números
        for (i in 1..75) {
            val row = (i - 1) / 5
            val col = (i - 1) % 5
            if (col == 0) tableLayout.addView(TableRow(this)) // Adiciona uma nova linha

            val tableRow = tableLayout.getChildAt(row) as TableRow
            val cell = TextView(this).apply {
                text = String.format("%02d", i) // Adiciona zero à frente
                setPadding(16, 16, 16, 16)
                setTextColor(resources.getColor(android.R.color.darker_gray, theme))
                setBackgroundResource(R.drawable.cell_border)
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            }
            numberCells[i] = cell
            tableRow.addView(cell)
        }

        // Botão "Girar"
        buttonGirar.setOnClickListener {
            if (calledNumbers.size == 75) {
                Toast.makeText(this, "Todos os números já foram sorteados!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            animateCircle {
                val newNumber = generateRandomNumber()
                numberCircle.text = String.format("%02d", newNumber) // Adiciona zero à frente
                numberCells[newNumber]?.apply {
                    setBackgroundColor(resources.getColor(android.R.color.holo_green_light, theme))
                    setTextColor(resources.getColor(android.R.color.black, theme))
                }
            }
        }

        // Botão "Recomeçar"
        buttonRecomecar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Novo jogo")
                .setMessage("Tem certeza de que deseja começar um novo jogo?")
                .setPositiveButton("Sim") { _, _ ->
                    resetGame()
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }

    private fun generateRandomNumber(): Int {
        var number: Int
        do {
            number = Random.nextInt(1, 76) // Número entre 1 e 75
        } while (calledNumbers.contains(number))
        calledNumbers.add(number)
        return number
    }

    private fun animateCircle(onComplete: () -> Unit) {
        numberCircle.text = "??" // Mostra "??" durante o giro
        numberCircle.animate().apply {
            duration = 2000 // 3 segundos
            rotationBy(720f) // 2 voltas completas
            withEndAction {
                numberCircle.rotation = 0f // Reseta a rotação
                onComplete()
            }
            start()
        }
    }



    private fun resetGame() {
        calledNumbers.clear()
        numberCells.values.forEach { cell ->
            cell.setBackgroundResource(R.drawable.cell_border) // Reaplica a borda
            cell.setTextColor(resources.getColor(android.R.color.darker_gray, theme)) // Volta para a cor original
        }
        numberCircle.text = "--" // Reseta o círculo
    }

    private fun startBackgroundMusic() {
        mediaPlayerBackground = MediaPlayer.create(this, R.raw.bingo).apply {
            isLooping = true // Configura o som para repetir continuamente
            start() // Inicia a reprodução
        }
        updateMusicButtonIcon(true)
    }

    private fun stopBackgroundMusic() {
        mediaPlayerBackground?.stop() // Para a música
        mediaPlayerBackground?.release() // Libera o recurso
        mediaPlayerBackground = null
        updateMusicButtonIcon(false)
    }

    private fun toggleBackgroundMusic() {
        if (isMusicPlaying) {
            stopBackgroundMusic()
        } else {
            startBackgroundMusic()
        }
        isMusicPlaying = !isMusicPlaying
    }

    private fun updateMusicButtonIcon(isPlaying: Boolean) {
        val iconRes = if (isPlaying) R.drawable.music_on else R.drawable.music_off
        buttonMusicToggle.setImageResource(iconRes)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopBackgroundMusic() // Para a música de fundo ao destruir a atividade
    }
}