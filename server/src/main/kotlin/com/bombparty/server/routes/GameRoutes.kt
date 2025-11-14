package com.bombparty.server.routes

import com.bombparty.server.game.GameManager
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun Application.configureRouting(gameManager: GameManager) {
    routing {
        webSocket("/game") {
            val connectionId = System.currentTimeMillis()
            println("=" + "=".repeat(60))
            println("🟢🟢🟢 WEBSOCKET CONNECTION #$connectionId RECEIVED 🟢🟢🟢")
            println("=" + "=".repeat(60))

            var currentPlayerId: String? = null
            var currentRoomId: String? = null

            try {
                println("📡 WebSocket #$connectionId: Listening for messages...")

                for (frame in incoming) {
                    println("📦 Frame received on connection #$connectionId: ${frame::class.simpleName}")

                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        println("=" + "=".repeat(60))
                        println("📨 MESSAGE RECEIVED on #$connectionId:")
                        println(text)
                        println("=" + "=".repeat(60))

                        try {
                            val json = Json.parseToJsonElement(text).jsonObject
                            val type = json["type"]?.jsonPrimitive?.content
                            println("📋 Message type: '$type'")

                            when (type) {
                                "create_room" -> {
                                    println("🏠🏠🏠 CREATING ROOM (conn #$connectionId) 🏠🏠🏠")
                                    val playerName = json["playerName"]?.jsonPrimitive?.content ?: "Player"
                                    val avatar = json["avatar"]?.jsonPrimitive?.content ?: "😀"
                                    println("👤 Player: $playerName, Avatar: $avatar")
                                    val config = json["config"]?.jsonObject
                                    println("⚙️ Config: $config")

                                    currentRoomId = gameManager.createRoom(this, playerName, avatar, config)
                                    currentPlayerId = currentRoomId
                                    println("✅✅✅ ROOM CREATED: $currentRoomId ✅✅✅")
                                }

                            "join_room" -> {
                                val roomId = json["roomId"]?.jsonPrimitive?.content ?: continue
                                val playerName = json["playerName"]?.jsonPrimitive?.content ?: "Player"
                                val avatar = json["avatar"]?.jsonPrimitive?.content ?: "😀"
                                currentPlayerId = gameManager.joinRoom(roomId, playerName, avatar, this)
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
                                    println("❓ Unknown type: '$type'")
                                }
                            }
                        } catch (parseError: Exception) {
                            println("❌ Parse error: ${parseError.message}")
                            parseError.printStackTrace()
                        }
                    } else {
                        println("⚠️ Non-text frame: ${frame::class.simpleName}")
                    }
                }
            } catch (e: Exception) {
                println("❌❌❌ WebSocket ERROR on #$connectionId:")
                println("Message: ${e.message}")
                e.printStackTrace()
            } finally {
                println("🔌 WebSocket #$connectionId: Connection CLOSED")
                gameManager.handleDisconnect(this)
            }
        }

        get("/health") {
            call.respondText("OK")
        }

        // Test endpoint para verificar que la serialización funciona
        get("/test-room") {
            val json = Json {
                prettyPrint = true
                classDiscriminator = "type"
            }

            val configDto = com.bombparty.server.dto.GameConfigDto()
            val playerDto = com.bombparty.server.dto.PlayerDto(
                id = "test-123",
                name = "TestPlayer",
                lives = 2
            )
            val roomDto = com.bombparty.server.dto.GameRoomDto(
                id = "TEST01",
                hostId = "test-123",
                config = configDto,
                players = listOf(playerDto)
            )
            val message = com.bombparty.server.dto.ServerMessage.RoomCreated(roomDto, "test-123")

            call.respondText(
                json.encodeToString(message),
                io.ktor.http.ContentType.Application.Json
            )
        }
    }
}
