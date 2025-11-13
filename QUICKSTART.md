# 🚀 Guía de Inicio Rápido - BombParty

## Estructura del Proyecto

```
bombpartykotlin/
├── app/              # Aplicación Android (cliente)
├── server/           # Servidor backend (Kotlin Ktor)
├── build.gradle.kts  # Configuración raíz de Gradle
└── settings.gradle.kts
```

## 📱 Ejecutar la Aplicación Android

### Opción 1: Con Android Studio (Recomendado)

1. Abre el proyecto en Android Studio
2. Espera a que Gradle sincronice
3. Conecta un dispositivo o inicia un emulador
4. Click en "Run" (▶️) o presiona `Shift + F10`

### Opción 2: Línea de comandos

```bash
# Compilar
./gradlew :app:assembleDebug

# Instalar en dispositivo conectado
./gradlew :app:installDebug
```

## 🖥️ Ejecutar el Servidor Backend

### Requisitos
- JDK 17 o superior
- Puerto 8080 disponible

### Opción 1: Con Gradle (Recomendado)

```bash
# Desde la raíz del proyecto
./gradlew :server:run
```

El servidor estará disponible en `http://localhost:8080`

### Opción 2: Compilar JAR y ejecutar

```bash
# Compilar
./gradlew :server:shadowJar

# Ejecutar
java -jar server/build/libs/server-1.0.0-all.jar
```

### Verificar que el servidor está corriendo

```bash
curl http://localhost:8080/health
# Debe responder: OK
```

## 🔌 Conectar la App al Servidor

### Para Emulador Android
```kotlin
// En GameViewModel.kt o donde conectes al servidor
viewModel.connectToServer("ws://10.0.2.2:8080/game")
```

### Para Dispositivo Físico

1. Asegúrate de que el dispositivo y tu PC estén en la misma red WiFi
2. Encuentra tu IP local:
   - Windows: `ipconfig` (busca IPv4)
   - Linux/Mac: `ifconfig` o `ip addr`
3. Usa tu IP local:
```kotlin
viewModel.connectToServer("ws://TU_IP_LOCAL:8080/game")
```

Ejemplo:
```kotlin
viewModel.connectToServer("ws://192.168.1.100:8080/game")
```

## 🎮 Probar el Juego

### Configuración para pruebas locales

1. **Inicia el servidor**:
```bash
./gradlew :server:run
```

2. **Ejecuta la app en múltiples dispositivos/emuladores**:
   - Primer dispositivo: Crea una sala
   - Segundo dispositivo: Únete con el código de sala
   - Inicia el juego desde el dispositivo que creó la sala

### Flujo de prueba

1. Abrir app → Menú principal
2. Jugador 1: "Crear Sala" → Anota el código de sala
3. Jugador 2: "Unirse a Sala" → Ingresa el código
4. Jugador 1: Presiona "Iniciar Juego"
5. Los jugadores escriben palabras por turnos

## 🐛 Solución de Problemas

### Error: "Connection refused"
- Verifica que el servidor esté corriendo
- Verifica la URL del servidor en el código
- Para emulador, usa `10.0.2.2` en lugar de `localhost`

### Error: "Unable to connect"
- Verifica que el firewall permita conexiones en el puerto 8080
- Si usas dispositivo físico, verifica que esté en la misma red

### El gradle no se ejecuta
Windows:
```bash
gradlew.bat :server:run
```

Linux/Mac:
```bash
chmod +x gradlew
./gradlew :server:run
```

### El servidor no acepta conexiones de dispositivos
- Asegúrate de que el servidor está escuchando en `0.0.0.0` (todas las interfaces)
- Verifica configuración de firewall

## 📝 Configuración del Servidor en Producción

Para desplegar en un servidor real:

1. **Actualizar URL en la app**:
```kotlin
viewModel.connectToServer("ws://tu-servidor.com:8080/game")
```

2. **Variables de entorno** (opcional):
```kotlin
// Application.kt
val port = System.getenv("PORT")?.toInt() ?: 8080
val host = System.getenv("HOST") ?: "0.0.0.0"
```

3. **Ejecutar servidor**:
```bash
# Con variables de entorno
PORT=8080 HOST=0.0.0.0 java -jar server.jar
```

## 🔥 Logs del Servidor

Los logs se imprimen en la consola. Verás mensajes como:
```
[INFO] Application - Responding at http://0.0.0.0:8080
[INFO] WebSocket - Client connected
[INFO] GameManager - Room created: ABC123
[INFO] GameManager - Player joined: Player1
```

## 📚 Recursos Adicionales

- [Documentación de Ktor](https://ktor.io/docs/)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [README.md](README.md) - Documentación completa

## 💡 Consejos

1. **Desarrollo rápido**: Usa el emulador con Hot Reload de Compose
2. **Testing multijugador**: Usa Android Studio para lanzar múltiples emuladores
3. **Debug WebSocket**: Revisa los logs del servidor para ver mensajes
4. **Performance**: El servidor puede manejar múltiples salas simultáneamente

---

¿Necesitas ayuda? Crea un issue en el repositorio.
