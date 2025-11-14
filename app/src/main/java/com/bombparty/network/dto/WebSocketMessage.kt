package com.bombparty.network.dto

import com.bombparty.domain.model.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class WebSocketMessage {
    @Serializable
    @SerialName("create_room")
    data class CreateRoom(val config: GameConfig, val playerName: String) : WebSocketMessage()

    @Serializable
    @SerialName("join_room")
    data class JoinRoom(val roomId: String, val playerName: String) : WebSocketMessage()

    @Serializable
    @SerialName("leave_room")
    data class LeaveRoom(val roomId: String, val playerId: String) : WebSocketMessage()

    @Serializable
    @SerialName("start_game")
    data class StartGame(val roomId: String) : WebSocketMessage()

    @Serializable
    @SerialName("submit_word")
    data class SubmitWord(
        val roomId: String,
        val playerId: String,
        val word: String
    ) : WebSocketMessage()

    @Serializable
    @SerialName("bomb_exploded")
    data class BombExploded(
        val roomId: String,
        val playerId: String
    ) : WebSocketMessage()

    @Serializable
    @SerialName("update_config")
    data class UpdateConfig(
        val roomId: String,
        val config: GameConfig
    ) : WebSocketMessage()
}

@Serializable
sealed class ServerMessage {
    @Serializable
    @SerialName("RoomCreated")
    data class RoomCreated(val room: GameRoom, val playerId: String) : ServerMessage()

    @Serializable
    @SerialName("RoomJoined")
    data class RoomJoined(val room: GameRoom, val playerId: String) : ServerMessage()

    @Serializable
    @SerialName("PlayerJoined")
    data class PlayerJoined(val player: Player) : ServerMessage()

    @Serializable
    @SerialName("PlayerLeft")
    data class PlayerLeft(val playerId: String) : ServerMessage()

    @Serializable
    @SerialName("GameStarted")
    data class GameStarted(val gameState: GameState) : ServerMessage()

    @Serializable
    @SerialName("GameStateUpdate")
    data class GameStateUpdate(val gameState: GameState) : ServerMessage()

    @Serializable
    @SerialName("WordAccepted")
    data class WordAccepted(
        val word: String,
        val playerId: String,
        val gainedLife: Boolean
    ) : ServerMessage()

    @Serializable
    @SerialName("WordRejected")
    data class WordRejected(val word: String, val reason: String) : ServerMessage()

    @Serializable
    @SerialName("BombExplodedEvent")
    data class BombExplodedEvent(
        val playerId: String,
        val playerName: String,
        val livesRemaining: Int
    ) : ServerMessage()

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
    @SerialName("Error")
    data class Error(val message: String) : ServerMessage()

    @Serializable
    @SerialName("ConfigUpdated")
    data class ConfigUpdated(val config: GameConfig) : ServerMessage()
}
