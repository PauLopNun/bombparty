package com.bombparty.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class GameStatus {
    WAITING,
    PLAYING,
    FINISHED
}

@Serializable
data class BombState(
    val currentSyllable: String,
    val timeRemaining: Float, // in seconds
    val maxTime: Float,
    val syllableTurnsRemaining: Int
)

@Serializable
data class GameState(
    val roomId: String,
    val players: List<Player>,
    val config: GameConfig,
    val status: GameStatus = GameStatus.WAITING,
    val currentPlayerIndex: Int = 0,
    val bombState: BombState? = null,
    val usedWordsInRound: Set<String> = emptySet(),
    val winnerId: String? = null,
    val currentBonusLetters: Set<Char> = emptySet()
) {
    val currentPlayer: Player?
        get() = players.getOrNull(currentPlayerIndex)

    val alivePlayers: List<Player>
        get() = players.filter { it.isAlive }

    fun nextTurn(): GameState {
        val aliveIndices = players.indices.filter { players[it].isAlive }
        if (aliveIndices.isEmpty()) return this

        val currentAliveIndex = aliveIndices.indexOf(currentPlayerIndex)
        val nextAliveIndex = (currentAliveIndex + 1) % aliveIndices.size
        val nextPlayerIndex = aliveIndices[nextAliveIndex]

        val updatedPlayers = players.mapIndexed { index, player ->
            player.copy(isCurrentTurn = index == nextPlayerIndex)
        }

        return copy(
            players = updatedPlayers,
            currentPlayerIndex = nextPlayerIndex,
            bombState = bombState?.let {
                if (it.syllableTurnsRemaining > 1) {
                    it.copy(syllableTurnsRemaining = it.syllableTurnsRemaining - 1)
                } else {
                    null // Will trigger new syllable generation
                }
            }
        )
    }

    fun updatePlayer(playerId: String, update: (Player) -> Player): GameState {
        return copy(
            players = players.map { if (it.id == playerId) update(it) else it }
        )
    }

    fun addUsedWord(word: String): GameState {
        return copy(usedWordsInRound = usedWordsInRound + word.lowercase())
    }

    fun isWordUsed(word: String): Boolean {
        val normalizedWord = word.lowercase()
        return normalizedWord in usedWordsInRound ||
               players.any { normalizedWord in it.usedWords }
    }

    fun checkWinner(): GameState {
        val alive = alivePlayers
        if (alive.size == 1 && status == GameStatus.PLAYING) {
            return copy(
                status = GameStatus.FINISHED,
                winnerId = alive.first().id
            )
        }
        return this
    }

    fun selectBonusLetters(): GameState {
        val eligibleLetters = config.bonusAlphabet
            .filter { it.value > 0 }
            .keys
            .mapNotNull { it.firstOrNull() }
            .toSet()
        val selectedLetters = eligibleLetters.shuffled().take(3).toSet()
        return copy(currentBonusLetters = selectedLetters)
    }
}
