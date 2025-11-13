package com.bombparty.network.dto

import com.bombparty.domain.model.*
import kotlinx.serialization.Serializable

@Serializable
sealed class WebSocketMessage {
    @Serializable
    data class CreateRoom(val config: GameConfig, val playerName: String) : WebSocketMessage()

    @Serializable
    data class JoinRoom(val roomId: String, val playerName: String) : WebSocketMessage()

    @Serializable
    data class LeaveRoom(val roomId: String, val playerId: String) : WebSocketMessage()

    @Serializable
    data class StartGame(val roomId: String) : WebSocketMessage()

    @Serializable
    data class SubmitWord(
        val roomId: String,
        val playerId: String,
        val word: String
    ) : WebSocketMessage()

    @Serializable
    data class BombExploded(
        val roomId: String,
        val playerId: String
    ) : WebSocketMessage()

    @Serializable
    data class UpdateConfig(
        val roomId: String,
        val config: GameConfig
    ) : WebSocketMessage()
}

@Serializable
sealed class ServerMessage {
    @Serializable
    data class RoomCreated(val room: GameRoom, val playerId: String) : ServerMessage()

    @Serializable
    data class RoomJoined(val room: GameRoom, val playerId: String) : ServerMessage()

    @Serializable
    data class PlayerJoined(val player: Player) : ServerMessage()

    @Serializable
    data class PlayerLeft(val playerId: String) : ServerMessage()

    @Serializable
    data class GameStarted(val gameState: GameState) : ServerMessage()

    @Serializable
    data class GameStateUpdate(val gameState: GameState) : ServerMessage()

    @Serializable
    data class WordAccepted(
        val word: String,
        val playerId: String,
        val gainedLife: Boolean
    ) : ServerMessage()

    @Serializable
    data class WordRejected(val word: String, val reason: String) : ServerMessage()

    @Serializable
    data class BombExplodedEvent(
        val playerId: String,
        val playerName: String,
        val livesRemaining: Int
    ) : ServerMessage()

    @Serializable
    data class PlayerEliminated(val playerId: String, val playerName: String) : ServerMessage()

    @Serializable
    data class GameFinished(val winnerId: String, val winnerName: String) : ServerMessage()

    @Serializable
    data class NewSyllable(val syllable: String, val bombTime: Float) : ServerMessage()

    @Serializable
    data class BombTimerUpdate(val timeRemaining: Float) : ServerMessage()

    @Serializable
    data class Error(val message: String) : ServerMessage()

    @Serializable
    data class ConfigUpdated(val config: GameConfig) : ServerMessage()
}
