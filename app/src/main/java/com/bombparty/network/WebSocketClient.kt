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
                            println("WebSocket: Received: $text")
                            val message = json.decodeFromString<ServerMessage>(text)
                            emit(message)
                        } catch (e: Exception) {
                            println("Error parsing message: ${e.message}")
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
