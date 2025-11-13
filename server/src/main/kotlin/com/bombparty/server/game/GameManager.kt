package com.bombparty.server.game

import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.util.*
import java.util.concurrent.ConcurrentHashMap

data class ServerPlayer(
    val id: String,
    val name: String,
    val session: WebSocketSession,
    var lives: Int,
    var isAlive: Boolean = true
)

data class ServerGameRoom(
    val id: String,
    val hostId: String,
    val players: MutableList<ServerPlayer> = mutableListOf(),
    var currentPlayerIndex: Int = 0,
    var currentSyllable: String? = null,
    var bombTimerJob: Job? = null,
    var bombTimeRemaining: Float = 0f,
    val usedWords: MutableSet<String> = mutableSetOf()
)

class GameManager {
    private val rooms = ConcurrentHashMap<String, ServerGameRoom>()
    private val playerToRoom = ConcurrentHashMap<String, String>()
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun createRoom(
        hostSession: WebSocketSession,
        playerName: String
    ): String = mutex.withLock {
        val roomId = generateRoomCode()
        val playerId = UUID.randomUUID().toString()

        val host = ServerPlayer(
            id = playerId,
            name = playerName,
            session = hostSession,
            lives = 2
        )

        val room = ServerGameRoom(
            id = roomId,
            hostId = playerId
        )
        room.players.add(host)

        rooms[roomId] = room
        playerToRoom[playerId] = roomId

        // Send RoomCreated message with full room details
        sendToPlayer(host, mapOf(
            "type" to "RoomCreated",
            "room" to mapOf(
                "id" to roomId,
                "hostId" to playerId,
                "players" to listOf(
                    mapOf(
                        "id" to playerId,
                        "name" to playerName,
                        "lives" to 2,
                        "isAlive" to true
                    )
                ),
                "status" to "WAITING",
                "maxPlayers" to 8
            ),
            "playerId" to playerId
        ))

        return roomId
    }

    suspend fun joinRoom(
        roomId: String,
        playerName: String,
        session: WebSocketSession
    ): String? = mutex.withLock {
        val room = rooms[roomId] ?: return null

        if (room.players.size >= 16) {
            sendToSession(session, mapOf("type" to "error", "message" to "Room is full"))
            return null
        }

        val playerId = UUID.randomUUID().toString()
        val player = ServerPlayer(
            id = playerId,
            name = playerName,
            session = session,
            lives = 2
        )

        room.players.add(player)
        playerToRoom[playerId] = roomId

        // Notify all players
        broadcastToRoom(room, mapOf(
            "type" to "player_joined",
            "playerId" to playerId,
            "playerName" to playerName
        ))

        sendToPlayer(player, mapOf(
            "type" to "room_joined",
            "roomId" to roomId,
            "playerId" to playerId,
            "players" to room.players.map { mapOf(
                "id" to it.id,
                "name" to it.name,
                "lives" to it.lives,
                "isAlive" to it.isAlive
            )}
        ))

        return playerId
    }

    suspend fun startGame(roomId: String) {
        val room = rooms[roomId] ?: return

        if (room.players.size < 2) {
            broadcastToRoom(room, mapOf(
                "type" to "error",
                "message" to "Need at least 2 players"
            ))
            return
        }

        broadcastToRoom(room, mapOf(
            "type" to "game_started",
            "players" to room.players.map { mapOf(
                "id" to it.id,
                "name" to it.name,
                "lives" to it.lives,
                "isAlive" to it.isAlive
            )}
        ))

        startNewRound(room)
    }

    private suspend fun startNewRound(room: ServerGameRoom) {
        val syllables = listOf("ar", "er", "or", "an", "en", "on", "al", "el", "as", "es", "os",
            "ra", "re", "ro", "na", "ne", "no", "la", "le", "lo", "sa", "se", "so",
            "ta", "te", "to", "ca", "co", "ma", "me", "mo", "pa", "pe", "po")

        room.currentSyllable = syllables.random()
        room.bombTimeRemaining = (10..20).random().toFloat()

        broadcastToRoom(room, mapOf(
            "type" to "new_syllable",
            "syllable" to room.currentSyllable!!,
            "bombTime" to room.bombTimeRemaining,
            "currentPlayerId" to room.players[room.currentPlayerIndex].id
        ))

        startBombTimer(room)
    }

    private fun startBombTimer(room: ServerGameRoom) {
        room.bombTimerJob?.cancel()
        room.bombTimerJob = CoroutineScope(Dispatchers.Default).launch {
            while (room.bombTimeRemaining > 0) {
                delay(100)
                room.bombTimeRemaining -= 0.1f

                if (room.bombTimeRemaining <= 0) {
                    handleBombExploded(room)
                    break
                }
            }
        }
    }

    private suspend fun handleBombExploded(room: ServerGameRoom) {
        val currentPlayer = room.players.getOrNull(room.currentPlayerIndex) ?: return

        currentPlayer.lives--
        if (currentPlayer.lives <= 0) {
            currentPlayer.isAlive = false
            broadcastToRoom(room, mapOf(
                "type" to "player_eliminated",
                "playerId" to currentPlayer.id,
                "playerName" to currentPlayer.name
            ))
        } else {
            broadcastToRoom(room, mapOf(
                "type" to "bomb_exploded",
                "playerId" to currentPlayer.id,
                "playerName" to currentPlayer.name,
                "livesRemaining" to currentPlayer.lives
            ))
        }

        val alivePlayers = room.players.filter { it.isAlive }
        if (alivePlayers.size == 1) {
            broadcastToRoom(room, mapOf(
                "type" to "game_finished",
                "winnerId" to alivePlayers.first().id,
                "winnerName" to alivePlayers.first().name
            ))
            return
        }

        nextTurn(room)
        startNewRound(room)
    }

    suspend fun submitWord(roomId: String, playerId: String, word: String) {
        val room = rooms[roomId] ?: return
        val currentPlayer = room.players.getOrNull(room.currentPlayerIndex) ?: return

        if (currentPlayer.id != playerId) {
            sendToPlayer(currentPlayer, mapOf(
                "type" to "error",
                "message" to "Not your turn"
            ))
            return
        }

        val syllable = room.currentSyllable ?: return
        val normalizedWord = word.lowercase().trim()

        // Basic validation
        if (!normalizedWord.contains(syllable.lowercase())) {
            sendToPlayer(currentPlayer, mapOf(
                "type" to "word_rejected",
                "word" to word,
                "reason" to "Word doesn't contain syllable"
            ))
            return
        }

        if (normalizedWord in room.usedWords) {
            sendToPlayer(currentPlayer, mapOf(
                "type" to "word_rejected",
                "word" to word,
                "reason" to "Word already used"
            ))
            return
        }

        // Word accepted
        room.usedWords.add(normalizedWord)
        room.bombTimerJob?.cancel()

        broadcastToRoom(room, mapOf(
            "type" to "word_accepted",
            "word" to word,
            "playerId" to playerId
        ))

        nextTurn(room)
        startNewRound(room)
    }

    private fun nextTurn(room: ServerGameRoom) {
        val aliveIndices = room.players.indices.filter { room.players[it].isAlive }
        if (aliveIndices.isEmpty()) return

        val currentAliveIndex = aliveIndices.indexOf(room.currentPlayerIndex)
        val nextAliveIndex = (currentAliveIndex + 1) % aliveIndices.size
        room.currentPlayerIndex = aliveIndices[nextAliveIndex]
    }

    suspend fun handleDisconnect(session: WebSocketSession) {
        val playerId = playerToRoom.entries.find { entry ->
            val room = rooms[entry.value]
            room?.players?.any { it.session == session } == true
        }?.key ?: return

        val roomId = playerToRoom.remove(playerId) ?: return
        val room = rooms[roomId] ?: return

        room.players.removeIf { it.id == playerId }

        if (room.players.isEmpty()) {
            rooms.remove(roomId)
        } else {
            broadcastToRoom(room, mapOf(
                "type" to "player_left",
                "playerId" to playerId
            ))
        }
    }

    private suspend fun broadcastToRoom(room: ServerGameRoom, message: Map<String, Any>) {
        room.players.forEach { player ->
            sendToPlayer(player, message)
        }
    }

    private suspend fun sendToPlayer(player: ServerPlayer, message: Map<String, Any>) {
        sendToSession(player.session, message)
    }

    private suspend fun sendToSession(session: WebSocketSession, message: Map<String, Any>) {
        try {
            val jsonString = json.encodeToString(
                kotlinx.serialization.serializer(),
                message
            )
            session.send(Frame.Text(jsonString))
        } catch (e: Exception) {
            println("Error sending message: ${e.message}")
        }
    }

    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }
}
