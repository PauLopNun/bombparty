package com.bombparty.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.bombparty.R

/**
 * SoundManager - Gestiona la reproducción de efectos de sonido del juego
 *
 * Archivos de sonido necesarios en app/src/main/res/raw/:
 * - bomb_tick.mp3 o .ogg - Sonido de tick-tock de la bomba (repetitivo)
 * - bomb_explode.mp3 o .ogg - Explosión de la bomba
 * - word_correct.mp3 o .ogg - Palabra aceptada
 * - word_incorrect.mp3 o .ogg - Palabra rechazada
 * - victory.mp3 o .ogg - Música/sonido de victoria
 * - game_start.mp3 o .ogg - Inicio del juego
 */
class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var soundIds = mutableMapOf<SoundType, Int>()
    private var streamIds = mutableMapOf<SoundType, Int>()

    private var isMuted = false

    enum class SoundType {
        BOMB_TICK,
        BOMB_EXPLODE,
        WORD_CORRECT,
        WORD_INCORRECT,
        VICTORY,
        GAME_START
    }

    init {
        initializeSoundPool()
        loadSounds()
    }

    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    private fun loadSounds() {
        try {
            // Intentar cargar cada sonido, si no existe continuará sin error
            soundPool?.let { pool ->
                // Nota: Los IDs de recursos se generan automáticamente cuando se añaden los archivos
                // Por ahora usamos try-catch para que no crashee si no existen los archivos

                try {
                    val bombTickId = context.resources.getIdentifier("bomb_tick", "raw", context.packageName)
                    if (bombTickId != 0) {
                        soundIds[SoundType.BOMB_TICK] = pool.load(context, bombTickId, 1)
                    }
                } catch (e: Exception) {
                    println("SoundManager: bomb_tick not found")
                }

                try {
                    val bombExplodeId = context.resources.getIdentifier("bomb_explode", "raw", context.packageName)
                    if (bombExplodeId != 0) {
                        soundIds[SoundType.BOMB_EXPLODE] = pool.load(context, bombExplodeId, 1)
                    }
                } catch (e: Exception) {
                    println("SoundManager: bomb_explode not found")
                }

                try {
                    val wordCorrectId = context.resources.getIdentifier("word_correct", "raw", context.packageName)
                    if (wordCorrectId != 0) {
                        soundIds[SoundType.WORD_CORRECT] = pool.load(context, wordCorrectId, 1)
                    }
                } catch (e: Exception) {
                    println("SoundManager: word_correct not found")
                }

                try {
                    val wordIncorrectId = context.resources.getIdentifier("word_incorrect", "raw", context.packageName)
                    if (wordIncorrectId != 0) {
                        soundIds[SoundType.WORD_INCORRECT] = pool.load(context, wordIncorrectId, 1)
                    }
                } catch (e: Exception) {
                    println("SoundManager: word_incorrect not found")
                }

                try {
                    val victoryId = context.resources.getIdentifier("victory", "raw", context.packageName)
                    if (victoryId != 0) {
                        soundIds[SoundType.VICTORY] = pool.load(context, victoryId, 1)
                    }
                } catch (e: Exception) {
                    println("SoundManager: victory not found")
                }

                try {
                    val gameStartId = context.resources.getIdentifier("game_start", "raw", context.packageName)
                    if (gameStartId != 0) {
                        soundIds[SoundType.GAME_START] = pool.load(context, gameStartId, 1)
                    }
                } catch (e: Exception) {
                    println("SoundManager: game_start not found")
                }
            }
        } catch (e: Exception) {
            println("SoundManager: Error loading sounds - ${e.message}")
        }
    }

    /**
     * Reproduce un sonido una vez
     */
    fun playSound(soundType: SoundType, volume: Float = 1.0f) {
        if (isMuted) return

        soundPool?.let { pool ->
            soundIds[soundType]?.let { soundId ->
                try {
                    pool.play(soundId, volume, volume, 1, 0, 1.0f)
                } catch (e: Exception) {
                    println("SoundManager: Error playing $soundType - ${e.message}")
                }
            }
        }
    }

    /**
     * Reproduce un sonido en bucle (útil para bomb_tick)
     */
    fun playSoundLoop(soundType: SoundType, volume: Float = 1.0f) {
        if (isMuted) return

        // Detener el loop anterior si existe
        stopSound(soundType)

        soundPool?.let { pool ->
            soundIds[soundType]?.let { soundId ->
                try {
                    val streamId = pool.play(soundId, volume, volume, 1, -1, 1.0f) // -1 = loop infinito
                    streamIds[soundType] = streamId
                } catch (e: Exception) {
                    println("SoundManager: Error playing loop $soundType - ${e.message}")
                }
            }
        }
    }

    /**
     * Detiene un sonido que está en loop
     */
    fun stopSound(soundType: SoundType) {
        soundPool?.let { pool ->
            streamIds[soundType]?.let { streamId ->
                try {
                    pool.stop(streamId)
                    streamIds.remove(soundType)
                } catch (e: Exception) {
                    println("SoundManager: Error stopping $soundType - ${e.message}")
                }
            }
        }
    }

    /**
     * Detiene todos los sonidos
     */
    fun stopAllSounds() {
        soundPool?.let { pool ->
            streamIds.values.forEach { streamId ->
                try {
                    pool.stop(streamId)
                } catch (e: Exception) {
                    // Ignorar errores al detener
                }
            }
            streamIds.clear()
        }
    }

    /**
     * Mutea/desmutea todos los sonidos
     */
    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (muted) {
            stopAllSounds()
        }
    }

    /**
     * Libera los recursos del SoundPool
     */
    fun release() {
        stopAllSounds()
        soundPool?.release()
        soundPool = null
        soundIds.clear()
    }
}
