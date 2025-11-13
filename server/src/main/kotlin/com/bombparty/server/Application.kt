package com.bombparty.server

import com.bombparty.server.game.GameManager
import com.bombparty.server.routes.configureRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import java.time.Duration

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val host = System.getenv("HOST") ?: "0.0.0.0"

    println("=".repeat(50))
    println("Starting BombParty Server")
    println("Host: $host")
    println("Port: $port")
    println("WebSocket endpoint: ws://$host:$port/game")
    println("=".repeat(50))

    embeddedServer(Netty, port = port, host = host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
    }

    install(CORS) {
        anyHost()
        allowHeader("Content-Type")
        allowHeader("Sec-WebSocket-Protocol")
        allowHeader("Sec-WebSocket-Version")
        allowHeader("Sec-WebSocket-Key")
        allowHeader("Sec-WebSocket-Extensions")
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(30)
        timeout = Duration.ofSeconds(60)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val gameManager = GameManager()
    configureRouting(gameManager)
}
