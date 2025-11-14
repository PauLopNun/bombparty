package com.bombparty.network.dto

import com.bombparty.domain.model.*

// Convert DTOs to domain models

fun PlayerDto.toDomain(): Player = Player(
    id = id,
    name = name,
    lives = lives,
    isAlive = isAlive,
    avatar = avatar
)

fun GameConfigDto.toDomain(): GameConfig {
    val syllableDiff = when (syllableDifficulty) {
        "BEGINNER" -> SyllableDifficulty.BEGINNER
        "INTERMEDIATE" -> SyllableDifficulty.INTERMEDIATE
        "ADVANCED" -> SyllableDifficulty.ADVANCED
        else -> SyllableDifficulty.BEGINNER
    }

    val lang = when (language) {
        "SPANISH" -> GameLanguage.SPANISH
        "ENGLISH" -> GameLanguage.ENGLISH
        "BOTH" -> GameLanguage.BOTH
        else -> GameLanguage.SPANISH
    }

    return GameConfig(
        language = lang,
        syllableDifficulty = syllableDiff,
        customMinWords = customMinWords,
        customMaxWords = customMaxWords,
        minTurnDuration = minTurnDuration,
        maxSyllableLifespan = maxSyllableLifespan,
        initialLives = initialLives,
        maxLives = maxLives,
        maxPlayers = maxPlayers
    )
}

fun GameRoomDto.toDomain(): GameRoom = GameRoom(
    id = id,
    hostId = hostId,
    config = config.toDomain(),
    players = players.map { it.toDomain() },
    isStarted = isStarted
)

fun BombStateDto.toDomain(): BombState = BombState(
    currentSyllable = currentSyllable,
    timeRemaining = timeRemaining,
    maxTime = maxTime,
    syllableTurnsRemaining = syllableTurnsRemaining
)

fun GameStateDto.toDomain(): GameState = GameState(
    roomId = roomId,
    players = players.map { it.toDomain() },
    config = config.toDomain(),
    status = status,
    currentPlayerIndex = currentPlayerIndex,
    bombState = bombState?.toDomain(),
    usedWordsInRound = usedWordsInRound,
    winnerId = winnerId
)
