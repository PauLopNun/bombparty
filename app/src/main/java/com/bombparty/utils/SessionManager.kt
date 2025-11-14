package com.bombparty.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestiona la persistencia de la sesión del juego
 * para permitir reconexión automática
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "bombparty_session",
        Context.MODE_PRIVATE
    )

    companion object {
        // Factory method for manual creation when needed outside of Hilt scope
        fun create(context: Context): SessionManager {
            return SessionManager(context.applicationContext)
        }
    }

    var roomId: String?
        get() = prefs.getString(KEY_ROOM_ID, null)
        set(value) = prefs.edit().putString(KEY_ROOM_ID, value).apply()

    var playerId: String?
        get() = prefs.getString(KEY_PLAYER_ID, null)
        set(value) = prefs.edit().putString(KEY_PLAYER_ID, value).apply()

    var playerName: String?
        get() = prefs.getString(KEY_PLAYER_NAME, null)
        set(value) = prefs.edit().putString(KEY_PLAYER_NAME, value).apply()

    var playerAvatar: String?
        get() = prefs.getString(KEY_PLAYER_AVATAR, "😀")
        set(value) = prefs.edit().putString(KEY_PLAYER_AVATAR, value).apply()

    var isInGame: Boolean
        get() = prefs.getBoolean(KEY_IN_GAME, false)
        set(value) = prefs.edit().putBoolean(KEY_IN_GAME, value).apply()

    fun hasActiveSession(): Boolean {
        return !roomId.isNullOrEmpty() && !playerId.isNullOrEmpty() && isInGame
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_ROOM_ID = "room_id"
        private const val KEY_PLAYER_ID = "player_id"
        private const val KEY_PLAYER_NAME = "player_name"
        private const val KEY_PLAYER_AVATAR = "player_avatar"
        private const val KEY_IN_GAME = "in_game"
    }
}
