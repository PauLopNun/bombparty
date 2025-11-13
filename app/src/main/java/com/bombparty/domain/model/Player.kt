package com.bombparty.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String,
    val name: String,
    val lives: Int,
    val isAlive: Boolean = true,
    val isCurrentTurn: Boolean = false,
    val usedWords: Set<String> = emptySet(),
    val bonusLettersUsed: Map<String, Int> = emptyMap()
) {
    fun loseLife(): Player = copy(
        lives = (lives - 1).coerceAtLeast(0),
        isAlive = lives > 1
    )

    fun gainLife(maxLives: Int): Player = copy(
        lives = (lives + 1).coerceAtMost(maxLives)
    )

    fun addUsedWord(word: String): Player = copy(
        usedWords = usedWords + word.lowercase()
    )

    fun updateBonusLetters(letter: Char): Player {
        val letterKey = letter.toString()
        val currentCount = bonusLettersUsed[letterKey] ?: 0
        return copy(
            bonusLettersUsed = bonusLettersUsed + (letterKey to currentCount + 1)
        )
    }

    fun resetBonusLetters(): Player = copy(
        bonusLettersUsed = emptyMap()
    )
}
