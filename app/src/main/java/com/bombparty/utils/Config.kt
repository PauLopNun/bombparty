package com.bombparty.utils

/**
 * Configuración de la aplicación
 *
 * Cambia IS_PRODUCTION según el entorno:
 * - false: Desarrollo local (servidor en tu PC)
 * - true: Producción (servidor en la nube)
 */
object Config {
    /**
     * CAMBIAR A true CUANDO DESPLIEGUES A PRODUCCIÓN
     *
     * false = Desarrollo local (servidor en localhost/10.0.2.2)
     * true = Producción (servidor en Railway/Render/etc.)
     */
    const val IS_PRODUCTION = true

    /**
     * URL del servidor según el entorno
     *
     * DESARROLLO:
     * - Emulador: ws://10.0.2.2:8080/game
     * - Dispositivo físico: ws://TU_IP:8080/game (ejemplo: ws://192.168.1.100:8080/game)
     *
     * PRODUCCIÓN:
     * - Railway: wss://tu-app.railway.app/game
     * - Render: wss://tu-app.onrender.com/game
     * - Otro: wss://tu-dominio.com/game
     *
     * Nota: Usa wss:// (WebSocket Secure) en producción con HTTPS
     */
    val SERVER_URL = if (IS_PRODUCTION) {
        // Servidor de producción en Railway
        "wss://97b87797-ba85-4845-a26d-11759c5ea25f.railway.app/game"
    } else {
        // URL para desarrollo local (emulador de Android)
        "ws://10.0.2.2:8080/game"

        // Si usas dispositivo físico, descomenta y usa tu IP:
        // "ws://192.168.1.100:8080/game"
    }

    /**
     * Configuración adicional
     */
    const val CONNECTION_TIMEOUT = 10000L // 10 segundos
    const val RECONNECT_DELAY = 3000L     // 3 segundos
    const val MAX_RECONNECT_ATTEMPTS = 5

    /**
     * URLs útiles para verificar el servidor
     */
    val SERVER_HEALTH_URL = if (IS_PRODUCTION) {
        "https://97b87797-ba85-4845-a26d-11759c5ea25f.railway.app/health"
    } else {
        "http://localhost:8080/health"
    }
}
