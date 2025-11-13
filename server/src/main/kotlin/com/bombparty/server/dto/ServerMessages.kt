package com.bombparty.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: String,
    val name: String,
    val lives: Int,
    val isAlive: Boolean = true,
    val isCurrentTurn: Boolean = false,
    val usedWords: Set<String> = emptySet(),
    val bonusLettersUsed: Map<String, Int> = emptyMap()
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
    val bonusAlphabet: Map<String, Int> = ('a'..'z').associate { it.toString() to if (it in listOf('x', 'z')) 0 else 1 }
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
sealed class ServerMessage {
    @Serializable
    data class RoomCreated(val room: GameRoomDto, val playerId: String) : ServerMessage()

    @Serializable
    data class RoomJoined(val room: GameRoomDto, val playerId: String) : ServerMessage()

    @Serializable
    data class PlayerJoined(val player: PlayerDto) : ServerMessage()

    @Serializable
    data class PlayerLeft(val playerId: String) : ServerMessage()

    @Serializable
    data class Error(val message: String) : ServerMessage()
}
