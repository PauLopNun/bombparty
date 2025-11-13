# 💣 BombParty

Una versión multiplayer del popular juego de palabras BombParty, desarrollada en Kotlin con Jetpack Compose para Android.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.4-green.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📝 Descripción

BombParty es un juego multijugador de palabras donde los jugadores compiten escribiendo palabras que contengan una sílaba específica antes de que explote la bomba. El último jugador en pie gana la partida.

### ✨ Características

- 🎮 **Multijugador en tiempo real** - Juega con 2-16 jugadores simultáneamente
- 🌍 **Multiidioma** - Soporta español e inglés
- ⚙️ **Altamente configurable** - Personaliza dificultad, vidas, duración y más
- 🎯 **Sistema de bonus** - Gana vidas usando letras especiales (amarillas)
- 💫 **UI moderna** - Interfaz fluida con Jetpack Compose y Material 3
- ⏱️ **Temporizador dinámico** - Bomba con animaciones visuales
- ☁️ **Servidor en la nube** - Backend desplegado en Railway (24/7)

## 📱 Instalación

### Descargar APK

1. Ve a la [página de releases](https://github.com/PauLopNun/bombparty/releases/latest)
2. Descarga el archivo `BombParty-vX.X.X.apk`
3. En tu dispositivo Android:
   - Habilita "Instalar desde fuentes desconocidas" (Configuración → Seguridad)
   - Abre el archivo APK descargado
   - Sigue las instrucciones de instalación

### Requisitos

- Android 7.0 (API 24) o superior
- Conexión a Internet para juego multijugador

## 🎮 Cómo Jugar

### Inicio Rápido

1. **Crear Sala**
   - Abre la app y selecciona "Crear Sala"
   - Configura las opciones del juego (opcional)
   - Comparte el código de sala con tus amigos

2. **Unirse a Sala**
   - Selecciona "Unirse a Sala"
   - Ingresa el código de sala que te compartieron
   - Espera en el lobby hasta que el host inicie el juego

3. **Jugando**
   - Observa la sílaba que aparece en la bomba
   - Escribe una palabra que contenga esa sílaba
   - Presiona Enter o el botón de envío
   - Si la palabra es válida, la bomba pasa al siguiente jugador
   - Si no respondes a tiempo, pierdes una vida
   - El último jugador con vidas gana

### Reglas

- Las palabras deben tener **mínimo 3 letras**
- La palabra debe **contener la sílaba** mostrada
- No se pueden **repetir palabras** en la misma partida
- Las **letras amarillas** (bonus) te dan una vida extra si las usas
- El temporizador se **acelera** conforme avanza la partida

## ⚙️ Configuración del Juego

### Dificultad

- **Principiante**: Sílabas con 500+ palabras posibles
- **Intermedio**: Sílabas con 300+ palabras posibles
- **Avanzado**: Sílabas con 100+ palabras posibles
- **Personalizado**: Define tu propio rango

### Opciones Personalizables

| Opción | Rango | Predeterminado |
|--------|-------|----------------|
| Duración mínima de turno | 3-30s | 5s |
| Vida máxima de sílaba | 1-10 turnos | 2 |
| Vidas iniciales | 1-10 | 2 |
| Vidas máximas | 2-10 | 3 |
| Jugadores máximos | 2-16 | 16 |

## 🏗️ Arquitectura del Proyecto

El proyecto sigue **Clean Architecture** con patrón **MVVM**:

```
app/
├── data/
│   ├── model/              # Modelos de datos
│   └── repository/         # Acceso a diccionarios
├── domain/
│   └── model/              # Entidades de negocio
├── network/
│   ├── dto/                # Data Transfer Objects
│   └── WebSocketClient.kt  # Cliente WebSocket
├── presentation/
│   ├── components/         # Componentes UI reutilizables
│   ├── navigation/         # Sistema de navegación
│   ├── screens/            # Pantallas de la app
│   ├── theme/              # Tema Material 3
│   └── viewmodel/          # ViewModels
├── di/                     # Inyección de dependencias (Hilt)
└── utils/                  # Utilidades y configuración

server/
└── src/main/kotlin/        # Servidor Ktor
    ├── Application.kt      # Punto de entrada
    ├── GameManager.kt      # Lógica de juego
    └── models/             # Modelos del servidor
```

## 🛠️ Stack Tecnológico

### Android App

- **[Kotlin](https://kotlinlang.org/)** - Lenguaje de programación
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - UI declarativa moderna
- **[Material 3](https://m3.material.io/)** - Sistema de diseño
- **[Hilt](https://dagger.dev/hilt/)** - Inyección de dependencias
- **[Ktor Client](https://ktor.io/docs/client.html)** - Cliente HTTP/WebSocket
- **[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Concurrencia
- **[Kotlin Serialization](https://kotlinlang.org/docs/serialization.html)** - JSON
- **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)** - Navegación

### Backend

- **[Ktor Server](https://ktor.io/)** - Framework web asíncrono
- **[WebSockets](https://ktor.io/docs/websocket.html)** - Comunicación en tiempo real
- **[Railway](https://railway.app/)** - Plataforma de deployment

## 🌐 Servidor

El servidor backend está desplegado en **Railway** y corre 24/7.

### Características del Servidor

- Gestión de salas y jugadores
- Validación de palabras con diccionarios español/inglés
- Sistema de turnos y temporizador
- Detección de desconexiones y reconexión automática
- Logs detallados para debugging

### Configuración

La URL del servidor se configura automáticamente en producción. Para desarrollo local, edita:

`app/src/main/java/com/bombparty/utils/Config.kt`

```kotlin
const val IS_PRODUCTION = false  // true para producción
```

## 🚀 Desarrollo

### Requisitos Previos

- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK 17**
- **Android SDK** API 24+

### Setup Local

```bash
# Clonar el repositorio
git clone https://github.com/PauLopNun/bombparty.git
cd bombparty

# Abrir en Android Studio
# File → Open → Seleccionar carpeta del proyecto

# Sincronizar Gradle
# Sync Now (cuando Android Studio lo sugiera)

# Ejecutar
# Run → Run 'app'
```

### Ejecutar Servidor Localmente

```bash
cd server
./gradlew run
```

El servidor iniciará en `http://localhost:8080`

### Build APK

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (sin firmar)
./gradlew assembleRelease
```

Los APKs se generan en: `app/build/outputs/apk/`

## 📦 Releases

Las releases se generan automáticamente mediante **GitHub Actions** cuando se crea un nuevo tag:

```bash
# Crear nuevo tag
git tag -a v1.0.1 -m "Descripción de la versión"

# Push del tag
git push origin v1.0.1
```

GitHub Actions compilará y publicará el APK automáticamente en la página de [Releases](https://github.com/PauLopNun/bombparty/releases).

## 🎨 Personalización

### Agregar Palabras al Diccionario

Edita los archivos en `app/src/main/assets/`:

- `dictionary_es.txt` - Palabras en español
- `dictionary_en.txt` - Palabras en inglés

Formato: una palabra por línea (mínimo 3 letras)

### Modificar Colores

Edita `app/src/main/java/com/bombparty/presentation/theme/Color.kt`

### Ajustar Configuración

Edita `app/src/main/java/com/bombparty/utils/Config.kt`

## 🐛 Troubleshooting

### La app no se conecta al servidor

1. Verifica tu conexión a Internet
2. Comprueba que el servidor esté activo: [Health Check](https://97b87797-ba85-4845-a26d-11759c5ea25f.railway.app/health)
3. Reinstala la app

### Error al instalar APK

- Asegúrate de haber habilitado "Fuentes desconocidas"
- Si tienes una versión anterior instalada, desinstálala primero
- Verifica que tu dispositivo tenga espacio suficiente

### El juego se congela

- Cierra y vuelve a abrir la app
- Verifica tu conexión a Internet
- Reporta el bug con capturas de pantalla

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 🤝 Contribuir

Las contribuciones son bienvenidas:

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 🙏 Créditos

- Inspirado en [BombParty de JKLM.FUN](https://jklm.fun/)
- Basado en el juego de mesa "Tic Tac Boum"

---

**Nota**: Este es un proyecto personal no oficial y no está afiliado con JKLM.FUN o Sparklin Labs.

---

¿Preguntas o sugerencias? [Abre un issue](https://github.com/PauLopNun/bombparty/issues)
