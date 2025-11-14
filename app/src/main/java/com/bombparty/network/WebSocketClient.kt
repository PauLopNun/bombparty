package com.bombparty.network

import com.bombparty.network.dto.ServerMessage
import com.bombparty.network.dto.WebSocketMessage
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketClient @Inject constructor() {
    private var session: DefaultClientWebSocketSession? = null
    private var lastServerUrl: String? = null
    private var isReconnecting = false

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        classDiscriminator = "type"
        encodeDefaults = true  // Enviar todos los campos, incluso con valores por defecto
    }

    private val client = HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(0, TimeUnit.SECONDS)  // Sin timeout de lectura
                writeTimeout(30, TimeUnit.SECONDS)
                // Mantener conexión viva más agresivamente
                pingInterval(15, TimeUnit.SECONDS)
            }
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(json)
            pingInterval = 15_000  // ping cada 15 segundos (más frecuente)
            maxFrameSize = Long.MAX_VALUE
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    suspend fun connect(
        serverUrl: String,
        onConnected: () -> Unit = {}
    ): Flow<ServerMessage> = flow {
        try {
            client.webSocket(serverUrl) {
                session = this
                println("WebSocket: Connected successfully to $serverUrl")
                onConnected()

                incoming.receiveAsFlow().collect { frame ->
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        try {
                            println("=== WebSocket RAW MESSAGE ===")
                            println(text)
                            println("=== ATTEMPTING TO PARSE ===")
                            val message = json.decodeFromString<ServerMessage>(text)
                            println("=== SUCCESSFULLY PARSED: ${message::class.simpleName} ===")

                            // Log specific message details
                            when (message) {
                                is ServerMessage.RoomCreated -> {
                                    println("RoomCreated: roomId=${message.room.id}, playerId=${message.playerId}, players=${message.room.players.size}")
                                    message.room.players.forEach { player ->
                                        println("  Player: id=${player.id}, name=${player.name}, avatar=${player.avatar}")
                                    }
                                }
                                is ServerMessage.RoomJoined -> {
                                    println("RoomJoined: roomId=${message.room.id}, playerId=${message.playerId}, players=${message.room.players.size}")
                                    message.room.players.forEach { player ->
                                        println("  Player: id=${player.id}, name=${player.name}, avatar=${player.avatar}")
                                    }
                                }
                                is ServerMessage.PlayerJoined -> {
                                    println("PlayerJoined: id=${message.player.id}, name=${message.player.name}, avatar=${message.player.avatar}")
                                }
                                else -> {
                                    println("Other message type: ${message::class.simpleName}")
                                }
                            }

                            emit(message)
                        } catch (e: Exception) {
                            println("=== ERROR PARSING MESSAGE ===")
                            println("Error: ${e.message}")
                            println("Raw message: $text")
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("WebSocket error: ${e.message}")
            throw e
        } finally {
            session = null
        }
    }

    suspend fun sendMessage(message: WebSocketMessage) {
        try {
            val jsonString = json.encodeToString(
                WebSocketMessage.serializer(),
                message
            )
            println("WebSocket: Sending: $jsonString")
            session?.send(Frame.Text(jsonString))
        } catch (e: Exception) {
            println("Error sending message: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    fun isConnected(): Boolean = session?.isActive == true

    suspend fun disconnect() {
        try {
            session?.close()
        } catch (e: Exception) {
            println("Error disconnecting: ${e.message}")
        } finally {
            session = null
        }
    }
}
