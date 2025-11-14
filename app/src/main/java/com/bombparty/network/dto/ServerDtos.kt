package com.bombparty.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: String,
    val name: String,
    val lives: Int,
    val isAlive: Boolean = true,
    val isCurrentTurn: Boolean = false,
    val usedWords: Set<String> = emptySet(),
    val bonusLettersUsed: Map<String, Int> = emptyMap(),
    val avatar: String = "😀"
)

@Serializable
data class GameConfigDto(
    val language: String = "SPANISH",
    val syllableDifficulty: String = "BEGINNER",
    val customMinWords: Int? = null,
    val customMaxWords: Int? = null,
    val minTurnDuration: Int = 5,
    val maxSyllableLifespan: Int = 2,
    val initialLives: Int = 2,
    val maxLives: Int = 3,
    val maxPlayers: Int = 16,
    val bonusAlphabet: Map<String, Int> = emptyMap()
)

@Serializable
data class GameRoomDto(
    val id: String,
    val hostId: String,
    val config: GameConfigDto,
    val players: List<PlayerDto>,
    val isStarted: Boolean = false
)

@Serializable
data class BombStateDto(
    val currentSyllable: String,
    val timeRemaining: Float,
    val maxTime: Float,
    val syllableTurnsRemaining: Int
)

@Serializable
data class GameStateDto(
    val roomId: String,
    val players: List<PlayerDto>,
    val config: GameConfigDto,
    val status: String = "PLAYING",
    val currentPlayerIndex: Int = 0,
    val bombState: BombStateDto? = null,
    val usedWordsInRound: Set<String> = emptySet(),
    val winnerId: String? = null,
    val currentBonusLetters: Set<Char> = emptySet()
)
