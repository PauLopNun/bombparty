# 💣 BombParty - Android Game

Una versión personalizada del juego BombParty de JKLM.FUN, desarrollada en Kotlin con Jetpack Compose para Android.

## 📝 Descripción

BombParty es un juego multijugador de palabras donde los jugadores deben escribir palabras que contengan una sílaba específica antes de que explote la bomba. El objetivo es ser el último superviviente.

### Características principales

- 🎮 **Multijugador en línea**: Juega con 2-16 jugadores en tiempo real
- 🌍 **Soporte multiidioma**: Diccionarios en español e inglés
- ⚙️ **Personalizable**: Configura dificultad, vidas, duración de turnos y más
- 🎯 **Sistema de bonus**: Gana vidas usando letras especiales
- 💫 **UI moderna**: Interfaz construida con Jetpack Compose y Material 3
- ⏱️ **Timer dinámico**: Bomba con temporizador visual y animaciones

## 🏗️ Arquitectura

El proyecto sigue la arquitectura MVVM (Model-View-ViewModel) con Clean Architecture:

```
app/
├── data/
│   ├── model/           # Modelos de datos
│   └── repository/      # Repositorios (diccionarios, etc.)
├── domain/
│   └── model/          # Modelos de dominio (Player, GameState, etc.)
├── network/
│   ├── dto/            # Data Transfer Objects
│   └── WebSocketClient # Cliente WebSocket para comunicación
├── presentation/
│   ├── components/     # Componentes reutilizables de UI
│   ├── navigation/     # Sistema de navegación
│   ├── screens/        # Pantallas de la aplicación
│   └── theme/          # Tema y estilos
└── di/                 # Inyección de dependencias (Hilt)
```

## 🛠️ Tecnologías Utilizadas

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna declarativa
- **Material 3** - Componentes de diseño
- **Hilt** - Inyección de dependencias
- **Ktor Client** - Cliente HTTP y WebSocket
- **Kotlin Coroutines** - Programación asíncrona
- **Kotlin Serialization** - Serialización JSON
- **Navigation Compose** - Navegación entre pantallas

## 📦 Instalación

### Requisitos previos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- SDK de Android API 24+ (Android 7.0)

### Pasos

1. Clona el repositorio:
```bash
git clone <tu-repositorio>
cd bombpartykotlin
```

2. Abre el proyecto en Android Studio

3. Sincroniza el proyecto con Gradle

4. Ejecuta la aplicación en un emulador o dispositivo físico

## 🎮 Cómo Jugar

1. **Menú Principal**:
   - Crea una nueva sala o únete a una existente
   - Configura las opciones del juego (solo el host)

2. **Sala de Espera**:
   - Espera a que se unan otros jugadores
   - El host puede iniciar el juego cuando haya al menos 2 jugadores

3. **Juego**:
   - Aparece una sílaba en la bomba
   - Escribe una palabra que contenga esa sílaba
   - Envía la palabra antes de que explote la bomba
   - Si no envías a tiempo, pierdes una vida
   - Usa letras bonus (amarillas) para ganar vidas extra
   - El último jugador con vidas gana

## ⚙️ Configuración del Juego

### Dificultad de sílabas
- **Principiante**: Al menos 500 palabras por sílaba
- **Intermedio**: Al menos 300 palabras por sílaba
- **Avanzado**: Al menos 100 palabras por sílaba
- **Personalizado**: Define tu propio rango

### Otras opciones
- **Duración mínima de turno**: 5 segundos (predeterminado)
- **Vida máxima de sílaba**: 2 turnos (predeterminado)
- **Vidas iniciales**: 2 (predeterminado)
- **Vidas máximas**: 3 (predeterminado)
- **Jugadores máximos**: 2-16

## 🌐 Backend (Servidor)

El juego requiere un servidor backend para la funcionalidad multijugador. Se incluye un servidor básico en Kotlin con Ktor.

### Configurar el servidor

1. Navega al directorio del servidor:
```bash
cd server
```

2. Ejecuta el servidor:
```bash
./gradlew run
```

El servidor escuchará en el puerto 8080 por defecto.

### Configurar la URL del servidor en la app

En `GameViewModel.kt`, actualiza la URL del servidor:

```kotlin
viewModel.connectToServer("ws://TU_IP:8080/game")
```

Para pruebas locales:
- Emulador: `ws://10.0.2.2:8080/game`
- Dispositivo físico: `ws://TU_IP_LOCAL:8080/game`

## 📱 Estructura de Pantallas

1. **MenuScreen**: Pantalla principal con opciones
2. **CreateRoomScreen**: Crear nueva sala con configuración
3. **JoinRoomScreen**: Unirse a sala existente
4. **LobbyScreen**: Sala de espera antes del juego
5. **GameScreen**: Pantalla principal del juego
6. **SettingsScreen**: Configuración de la aplicación

## 🎨 Personalización

### Colores

Edita `app/src/main/java/com/bombparty/presentation/theme/Color.kt` para personalizar los colores.

### Diccionarios

Agrega palabras a los diccionarios en `app/src/main/assets/`:
- `dictionary_es.txt` - Palabras en español
- `dictionary_en.txt` - Palabras en inglés

Formato: una palabra por línea (mínimo 3 letras)

## 🔧 Próximas Mejoras

- [ ] Pantallas de crear/unir sala completas
- [ ] Sistema de autenticación de usuarios
- [ ] Historial de partidas
- [ ] Rankings y estadísticas
- [ ] Sistema de logros
- [ ] Chat en sala
- [ ] Sonidos y efectos
- [ ] Animaciones mejoradas
- [ ] Modo offline vs AI
- [ ] Soporte para más idiomas

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Haz fork del proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 👨‍💻 Autor

Desarrollado con ❤️ por [Tu Nombre]

## 🙏 Agradecimientos

- Inspirado en [BombParty de JKLM.FUN](https://jklm.fun/)
- Basado en el juego de mesa Tic Tac Boum

---

**Nota**: Este es un proyecto personal no oficial y no está afiliado con JKLM.FUN o Sparklin Labs.
