package com.bombparty.network

import com.bombparty.network.dto.ServerMessage
import com.bombparty.network.dto.WebSocketMessage
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketClient @Inject constructor() {
    private var session: DefaultClientWebSocketSession? = null

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    suspend fun connect(
        serverUrl: String
    ): Flow<ServerMessage> = flow {
        try {
            client.webSocket(serverUrl) {
                session = this

                incoming.receiveAsFlow().collect { frame ->
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        try {
                            val message = Json.decodeFromString<ServerMessage>(text)
                            emit(message)
                        } catch (e: Exception) {
                            println("Error parsing message: ${e.message}")
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
            val jsonString = Json.encodeToString(
                WebSocketMessage.serializer(),
                message
            )
            session?.send(Frame.Text(jsonString))
        } catch (e: Exception) {
            println("Error sending message: ${e.message}")
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
