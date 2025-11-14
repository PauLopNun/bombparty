package com.bombparty.server.dto

import kotlinx.serialization.SerialName
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

@Serializable
sealed class ServerMessage {
    @Serializable
    @SerialName("RoomCreated")
    data class RoomCreated(val room: GameRoomDto, val playerId: String) : ServerMessage()

    @Serializable
    @SerialName("RoomJoined")
    data class RoomJoined(val room: GameRoomDto, val playerId: String) : ServerMessage()

    @Serializable
    @SerialName("PlayerJoined")
    data class PlayerJoined(val player: PlayerDto) : ServerMessage()

    @Serializable
    @SerialName("PlayerLeft")
    data class PlayerLeft(val playerId: String) : ServerMessage()

    @Serializable
    @SerialName("GameStarted")
    data class GameStarted(val gameState: GameStateDto) : ServerMessage()

    @Serializable
    @SerialName("GameStateUpdate")
    data class GameStateUpdate(val gameState: GameStateDto) : ServerMessage()

    @Serializable
    @SerialName("WordAccepted")
    data class WordAccepted(val word: String, val playerId: String, val gainedLife: Boolean) : ServerMessage()

    @Serializable
    @SerialName("WordRejected")
    data class WordRejected(val word: String, val reason: String) : ServerMessage()

    @Serializable
    @SerialName("BombExplodedEvent")
    data class BombExplodedEvent(val playerId: String, val playerName: String, val livesRemaining: Int) : ServerMessage()

    @Serializable
    @SerialName("PlayerEliminated")
    data class PlayerEliminated(val playerId: String, val playerName: String) : ServerMessage()

    @Serializable
    @SerialName("GameFinished")
    data class GameFinished(val winnerId: String, val winnerName: String) : ServerMessage()

    @Serializable
    @SerialName("NewSyllable")
    data class NewSyllable(val syllable: String, val bombTime: Float) : ServerMessage()

    @Serializable
    @SerialName("BombTimerUpdate")
    data class BombTimerUpdate(val timeRemaining: Float) : ServerMessage()

    @Serializable
    @SerialName("ConfigUpdated")
    data class ConfigUpdated(val config: GameConfigDto) : ServerMessage()

    @Serializable
    @SerialName("Error")
    data class Error(val message: String) : ServerMessage()
}
