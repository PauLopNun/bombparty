package com.bombparty.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class GameLanguage {
    SPANISH,
    ENGLISH,
    BOTH
}

@Serializable
enum class SyllableDifficulty(val minWords: Int) {
    BEGINNER(500),
    INTERMEDIATE(300),
    ADVANCED(100),
    CUSTOM(0)
}

@Serializable
data class GameConfig(
    val language: GameLanguage = GameLanguage.SPANISH,
    val syllableDifficulty: SyllableDifficulty = SyllableDifficulty.BEGINNER,
    val customMinWords: Int? = null,
    val customMaxWords: Int? = null,
    val minTurnDuration: Int = 5, // seconds
    val maxSyllableLifespan: Int = 2, // turns
    val initialLives: Int = 2,
    val maxLives: Int = 3,
    val maxPlayers: Int = 16,
    val bonusAlphabet: Map<String, Int> = defaultBonusAlphabet()
) {
    companion object {
        fun defaultBonusAlphabet(): Map<String, Int> {
            return ('a'..'z').associateWith { if (it in listOf('x', 'z')) 0 else 1 }
                .mapKeys { it.key.toString() }
        }
    }

    fun getMinWordsForSyllable(): Int {
        return when (syllableDifficulty) {
            SyllableDifficulty.CUSTOM -> customMinWords ?: 100
            else -> syllableDifficulty.minWords
        }
    }

    fun getMaxWordsForSyllable(): Int? {
        return if (syllableDifficulty == SyllableDifficulty.CUSTOM) customMaxWords else null
    }
}
