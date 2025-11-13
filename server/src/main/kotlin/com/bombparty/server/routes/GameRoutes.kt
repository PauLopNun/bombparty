package com.bombparty.server.routes

import com.bombparty.server.game.GameManager
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun Application.configureRouting(gameManager: GameManager) {
    routing {
        webSocket("/game") {
            var currentPlayerId: String? = null
            var currentRoomId: String? = null

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        val json = Json.parseToJsonElement(text).jsonObject

                        when (val type = json["type"]?.jsonPrimitive?.content) {
                            "create_room" -> {
                                val playerName = json["playerName"]?.jsonPrimitive?.content ?: "Player"
                                currentRoomId = gameManager.createRoom(this, playerName)
                                currentPlayerId = currentRoomId // Simplified for this example
                            }

                            "join_room" -> {
                                val roomId = json["roomId"]?.jsonPrimitive?.content ?: continue
                                val playerName = json["playerName"]?.jsonPrimitive?.content ?: "Player"
                                currentPlayerId = gameManager.joinRoom(roomId, playerName, this)
                                currentRoomId = roomId
                            }

                            "start_game" -> {
                                val roomId = json["roomId"]?.jsonPrimitive?.content ?: continue
                                gameManager.startGame(roomId)
                            }

                            "submit_word" -> {
                                val roomId = json["roomId"]?.jsonPrimitive?.content ?: continue
                                val playerId = json["playerId"]?.jsonPrimitive?.content ?: continue
                                val word = json["word"]?.jsonPrimitive?.content ?: continue
                                gameManager.submitWord(roomId, playerId, word)
                            }

                            else -> {
                                println("Unknown message type: $type")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("WebSocket error: ${e.message}")
            } finally {
                gameManager.handleDisconnect(this)
            }
        }

        get("/health") {
            call.respondText("OK")
        }
    }
}
