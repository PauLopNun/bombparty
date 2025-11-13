package com.bombparty.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GameRoom(
    val id: String,
    val hostId: String,
    val config: GameConfig,
    val players: List<Player> = emptyList(),
    val isStarted: Boolean = false
) {
    val isFull: Boolean
        get() = players.size >= config.maxPlayers

    fun canStart(): Boolean = players.size >= 2 && !isStarted

    fun addPlayer(player: Player): GameRoom? {
        if (isFull || players.any { it.id == player.id }) return null
        return copy(players = players + player)
    }

    fun removePlayer(playerId: String): GameRoom {
        return copy(players = players.filter { it.id != playerId })
    }

    fun updatePlayer(playerId: String, update: (Player) -> Player): GameRoom {
        return copy(
            players = players.map { if (it.id == playerId) update(it) else it }
        )
    }
}
