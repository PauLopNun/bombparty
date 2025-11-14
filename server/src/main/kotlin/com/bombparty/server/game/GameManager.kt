package com.bombparty.server.game

import com.bombparty.server.dto.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
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
    val usedWords: MutableSet<String> = mutableSetOf(),
    var config: Map<String, Any> = mapOf(
        "language" to "SPANISH",
        "syllableDifficulty" to "BEGINNER",
        "minTurnDuration" to 5,
        "maxSyllableLifespan" to 2,
        "initialLives" to 2,
        "maxLives" to 3,
        "maxPlayers" to 16
    )
)

class GameManager {
    private val rooms = ConcurrentHashMap<String, ServerGameRoom>()
    private val playerToRoom = ConcurrentHashMap<String, String>()
    private val mutex = Mutex()
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        classDiscriminator = "type"
        encodeDefaults = true  // Incluir campos con valores por defecto
    }

    suspend fun createRoom(
        hostSession: WebSocketSession,
        playerName: String,
        configJson: kotlinx.serialization.json.JsonObject? = null
    ): String = mutex.withLock {
        val roomId = generateRoomCode()
        val playerId = UUID.randomUUID().toString()

        // Parse config from JSON if provided
        val config = if (configJson != null) {
            try {
                mutableMapOf<String, Any>().apply {
                    configJson["language"]?.let { put("language", it.toString().replace("\"", "")) }
                    configJson["syllableDifficulty"]?.let { put("syllableDifficulty", it.toString().replace("\"", "")) }
                    configJson["minTurnDuration"]?.let {
                        val value = it.toString().toIntOrNull() ?: 5
                        put("minTurnDuration", value)
                    }
                    configJson["maxSyllableLifespan"]?.let {
                        val value = it.toString().toIntOrNull() ?: 2
                        put("maxSyllableLifespan", value)
                    }
                    configJson["initialLives"]?.let {
                        val value = it.toString().toIntOrNull() ?: 2
                        put("initialLives", value)
                    }
                    configJson["maxLives"]?.let {
                        val value = it.toString().toIntOrNull() ?: 3
                        put("maxLives", value)
                    }
                    configJson["maxPlayers"]?.let {
                        val value = it.toString().toIntOrNull() ?: 16
                        put("maxPlayers", value)
                    }
                }
            } catch (e: Exception) {
                println("Error parsing config: ${e.message}")
                mapOf(
                    "language" to "SPANISH",
                    "syllableDifficulty" to "BEGINNER",
                    "minTurnDuration" to 5,
                    "maxSyllableLifespan" to 2,
                    "initialLives" to 2,
                    "maxLives" to 3,
                    "maxPlayers" to 16
                )
            }
        } else {
            mapOf(
                "language" to "SPANISH",
                "syllableDifficulty" to "BEGINNER",
                "minTurnDuration" to 5,
                "maxSyllableLifespan" to 2,
                "initialLives" to 2,
                "maxLives" to 3,
                "maxPlayers" to 16
            )
        }

        val initialLives = (config["initialLives"] as? Int) ?: 2

        val host = ServerPlayer(
            id = playerId,
            name = playerName,
            session = hostSession,
            lives = initialLives
        )

        val room = ServerGameRoom(
            id = roomId,
            hostId = playerId,
            config = config
        )
        room.players.add(host)

        rooms[roomId] = room
        playerToRoom[playerId] = roomId

        // Send RoomCreated message using DTOs
        val configDto = GameConfigDto(
            language = room.config["language"] as? String ?: "SPANISH",
            syllableDifficulty = room.config["syllableDifficulty"] as? String ?: "BEGINNER",
            minTurnDuration = room.config["minTurnDuration"] as? Int ?: 5,
            maxSyllableLifespan = room.config["maxSyllableLifespan"] as? Int ?: 2,
            initialLives = initialLives,
            maxLives = room.config["maxLives"] as? Int ?: 3,
            maxPlayers = room.config["maxPlayers"] as? Int ?: 16
        )

        val playerDto = PlayerDto(
            id = playerId,
            name = playerName,
            lives = initialLives,
            isAlive = true,
            isCurrentTurn = false
        )

        val roomDto = GameRoomDto(
            id = roomId,
            hostId = playerId,
            config = configDto,
            players = listOf(playerDto),
            isStarted = false
        )

        val message = ServerMessage.RoomCreated(roomDto, playerId)
        sendMessage(host.session, message)

        return roomId
    }

    suspend fun joinRoom(
        roomId: String,
        playerName: String,
        session: WebSocketSession
    ): String? = mutex.withLock {
        val room = rooms[roomId] ?: return null

        if (room.players.size >= 16) {
            sendMessage(session, ServerMessage.Error("Room is full"))
            return null
        }

        val playerId = UUID.randomUUID().toString()
        val initialLives = room.config["initialLives"] as? Int ?: 2
        val player = ServerPlayer(
            id = playerId,
            name = playerName,
            session = session,
            lives = initialLives
        )

        room.players.add(player)
        playerToRoom[playerId] = roomId

        // Notify all OTHER players that a new player joined
        val newPlayerDto = PlayerDto(
            id = playerId,
            name = playerName,
            lives = initialLives
        )
        room.players.filter { it.id != playerId }.forEach { otherPlayer ->
            sendMessage(otherPlayer.session, ServerMessage.PlayerJoined(newPlayerDto))
        }

        // Send RoomJoined to the new player with full room state
        val configDto = GameConfigDto(
            language = room.config["language"] as? String ?: "SPANISH",
            syllableDifficulty = room.config["syllableDifficulty"] as? String ?: "BEGINNER",
            minTurnDuration = room.config["minTurnDuration"] as? Int ?: 5,
            maxSyllableLifespan = room.config["maxSyllableLifespan"] as? Int ?: 2,
            initialLives = initialLives,
            maxLives = room.config["maxLives"] as? Int ?: 3,
            maxPlayers = room.config["maxPlayers"] as? Int ?: 16
        )

        val playersDto = room.players.map { p ->
            PlayerDto(
                id = p.id,
                name = p.name,
                lives = p.lives,
                isAlive = p.isAlive,
                isCurrentTurn = false
            )
        }

        val roomDto = GameRoomDto(
            id = roomId,
            hostId = room.hostId,
            config = configDto,
            players = playersDto,
            isStarted = false
        )

        sendMessage(session, ServerMessage.RoomJoined(roomDto, playerId))

        return playerId
    }

    suspend fun startGame(roomId: String) {
        val room = rooms[roomId] ?: return

        if (room.players.size < 2) {
            room.players.forEach { player ->
                sendMessage(player.session, ServerMessage.Error("Need at least 2 players"))
            }
            return
        }

        startNewRound(room)
    }

    private suspend fun startNewRound(room: ServerGameRoom) {
        val syllables = listOf("ar", "er", "or", "an", "en", "on", "al", "el", "as", "es", "os",
            "ra", "re", "ro", "na", "ne", "no", "la", "le", "lo", "sa", "se", "so",
            "ta", "te", "to", "ca", "co", "ma", "me", "mo", "pa", "pe", "po")

        room.currentSyllable = syllables.random()
        room.bombTimeRemaining = (10..20).random().toFloat()

        // Send NewSyllable message
        room.players.forEach { player ->
            sendMessage(player.session, ServerMessage.NewSyllable(
                syllable = room.currentSyllable!!,
                bombTime = room.bombTimeRemaining
            ))
        }

        // Send GameStarted with full game state
        val gameState = createGameStateDto(room)
        room.players.forEach { player ->
            sendMessage(player.session, ServerMessage.GameStarted(gameState))
        }

        startBombTimer(room)
    }

    private fun createGameStateDto(room: ServerGameRoom): GameStateDto {
        val playersDto = room.players.mapIndexed { index, player ->
            PlayerDto(
                id = player.id,
                name = player.name,
                lives = player.lives,
                isAlive = player.isAlive,
                isCurrentTurn = index == room.currentPlayerIndex
            )
        }

        val configDto = GameConfigDto(
            language = room.config["language"] as? String ?: "SPANISH",
            syllableDifficulty = room.config["syllableDifficulty"] as? String ?: "BEGINNER",
            minTurnDuration = room.config["minTurnDuration"] as? Int ?: 5,
            maxSyllableLifespan = room.config["maxSyllableLifespan"] as? Int ?: 2,
            initialLives = room.config["initialLives"] as? Int ?: 2,
            maxLives = room.config["maxLives"] as? Int ?: 3,
            maxPlayers = room.config["maxPlayers"] as? Int ?: 16
        )

        val bombState = room.currentSyllable?.let {
            BombStateDto(
                currentSyllable = it,
                timeRemaining = room.bombTimeRemaining,
                maxTime = room.bombTimeRemaining,
                syllableTurnsRemaining = room.config["maxSyllableLifespan"] as? Int ?: 2
            )
        }

        return GameStateDto(
            currentPlayerIndex = room.currentPlayerIndex,
            players = playersDto,
            config = configDto,
            bombState = bombState
        )
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
            room.players.forEach { player ->
                sendMessage(player.session, ServerMessage.PlayerEliminated(
                    playerId = currentPlayer.id,
                    playerName = currentPlayer.name
                ))
            }
        } else {
            room.players.forEach { player ->
                sendMessage(player.session, ServerMessage.BombExplodedEvent(
                    playerId = currentPlayer.id,
                    playerName = currentPlayer.name,
                    livesRemaining = currentPlayer.lives
                ))
            }
        }

        val alivePlayers = room.players.filter { it.isAlive }
        if (alivePlayers.size == 1) {
            room.players.forEach { player ->
                sendMessage(player.session, ServerMessage.GameFinished(
                    winnerId = alivePlayers.first().id,
                    winnerName = alivePlayers.first().name
                ))
            }
            return
        }

        nextTurn(room)
        startNewRound(room)
    }

    suspend fun submitWord(roomId: String, playerId: String, word: String) {
        val room = rooms[roomId] ?: return
        val currentPlayer = room.players.getOrNull(room.currentPlayerIndex) ?: return

        if (currentPlayer.id != playerId) {
            sendMessage(currentPlayer.session, ServerMessage.Error("Not your turn"))
            return
        }

        val syllable = room.currentSyllable ?: return
        val normalizedWord = word.lowercase().trim()

        // Basic validation
        if (!normalizedWord.contains(syllable.lowercase())) {
            sendMessage(currentPlayer.session, ServerMessage.WordRejected(
                word = word,
                reason = "Word doesn't contain syllable"
            ))
            return
        }

        if (normalizedWord in room.usedWords) {
            sendMessage(currentPlayer.session, ServerMessage.WordRejected(
                word = word,
                reason = "Word already used"
            ))
            return
        }

        // Word accepted
        room.usedWords.add(normalizedWord)
        room.bombTimerJob?.cancel()

        room.players.forEach { player ->
            sendMessage(player.session, ServerMessage.WordAccepted(
                word = word,
                playerId = playerId,
                gainedLife = false
            ))
        }

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
            room.players.forEach { player ->
                sendMessage(player.session, ServerMessage.PlayerLeft(playerId))
            }
        }
    }

    private suspend fun sendMessage(session: WebSocketSession, message: ServerMessage) {
        try {
            val jsonString = json.encodeToString(message)
            println("📤 Sending: ${message::class.simpleName}")
            println("📤 JSON: $jsonString")
            session.send(Frame.Text(jsonString))
            println("✅ Message sent successfully")
        } catch (e: Exception) {
            println("❌ Error sending message: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun sendToSession(session: WebSocketSession, message: Map<String, Any>) {
        try {
            // Build JSON manually since we're using Map<String, Any>
            val jsonBuilder = StringBuilder("{")
            message.entries.forEachIndexed { index, (key, value) ->
                if (index > 0) jsonBuilder.append(",")
                jsonBuilder.append("\"$key\":")
                when (value) {
                    is String -> jsonBuilder.append("\"$value\"")
                    is Number -> jsonBuilder.append(value)
                    is Boolean -> jsonBuilder.append(value)
                    is Map<*, *> -> jsonBuilder.append(mapToJson(value as Map<String, Any>))
                    is List<*> -> jsonBuilder.append(listToJson(value))
                    else -> jsonBuilder.append("null")
                }
            }
            jsonBuilder.append("}")
            val jsonString = jsonBuilder.toString()
            println("📤 Sending JSON: $jsonString")
            session.send(Frame.Text(jsonString))
            println("✅ Message sent successfully")
        } catch (e: Exception) {
            println("❌ Error sending message: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun mapToJson(map: Map<String, Any>): String {
        val jsonBuilder = StringBuilder("{")
        map.entries.forEachIndexed { index, (key, value) ->
            if (index > 0) jsonBuilder.append(",")
            jsonBuilder.append("\"$key\":")
            when (value) {
                is String -> jsonBuilder.append("\"$value\"")
                is Number -> jsonBuilder.append(value)
                is Boolean -> jsonBuilder.append(value)
                is Map<*, *> -> jsonBuilder.append(mapToJson(value as Map<String, Any>))
                is List<*> -> jsonBuilder.append(listToJson(value))
                else -> jsonBuilder.append("null")
            }
        }
        jsonBuilder.append("}")
        return jsonBuilder.toString()
    }

    private fun listToJson(list: List<*>): String {
        val jsonBuilder = StringBuilder("[")
        list.forEachIndexed { index, value ->
            if (index > 0) jsonBuilder.append(",")
            when (value) {
                is String -> jsonBuilder.append("\"$value\"")
                is Number -> jsonBuilder.append(value)
                is Boolean -> jsonBuilder.append(value)
                is Map<*, *> -> jsonBuilder.append(mapToJson(value as Map<String, Any>))
                is List<*> -> jsonBuilder.append(listToJson(value))
                else -> jsonBuilder.append("null")
            }
        }
        jsonBuilder.append("]")
        return jsonBuilder.toString()
    }

    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }
}
