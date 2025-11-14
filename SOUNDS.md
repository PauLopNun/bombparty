# Archivos de Sonido para BombParty

## 📁 Ubicación
Los archivos de sonido deben colocarse en: `app/src/main/res/raw/`

Si la carpeta `raw` no existe, créala dentro de `app/src/main/res/`

## 🎵 Archivos Necesarios

| Archivo | Evento | Descripción |
|---------|--------|-------------|
| `bomb_tick.mp3` o `.ogg` | Bomba activa | Sonido de tick-tock que se repite mientras la bomba está activa. Debe ser corto (0.5-1s) para que suene bien en loop. |
| `bomb_explode.mp3` o `.ogg` | Bomba explota | Sonido de explosión cuando se acaba el tiempo. |
| `word_correct.mp3` o `.ogg` | Palabra aceptada | Sonido positivo/satisfactorio cuando el jugador envía una palabra válida. |
| `word_incorrect.mp3` o `.ogg` | Palabra rechazada | Sonido negativo/error cuando la palabra es inválida. |
| `game_start.mp3` o `.ogg` | Inicio de partida | Sonido que se reproduce cuando comienza el juego. |
| `victory.mp3` o `.ogg` | Victoria | Música o fanfarria de victoria cuando alguien gana. |

## 🔍 Dónde Conseguir Sonidos Gratis

### Sitios Recomendados (Royalty-Free):
1. **Freesound.org** - https://freesound.org
   - Busca: "tick tock", "bomb", "explosion", "success", "error", "victory"
   - Requiere cuenta gratuita
   - Licencias Creative Commons

2. **Zapsplat** - https://www.zapsplat.com
   - Categorías: Game Sounds
   - Descarga gratuita con atribución

3. **Mixkit** - https://mixkit.co/free-sound-effects/
   - 100% gratis sin atribución
   - Sonidos de juegos

4. **OpenGameArt** - https://opengameart.org
   - Específico para juegos
   - Múltiples licencias

## ⚙️ Formato Recomendado
- **Formato**: OGG (más eficiente que MP3 en Android)
- **Calidad**: 44.1 kHz, 128 kbps
- **Duración**:
  - bomb_tick: 0.5-1 segundo (se reproducirá en loop)
  - bomb_explode: 1-2 segundos
  - word_correct: 0.3-0.7 segundos
  - word_incorrect: 0.3-0.7 segundos
  - game_start: 1-3 segundos
  - victory: 3-5 segundos

## 🛠️ Convertir a OGG

Si descargas MP3, puedes convertir a OGG con herramientas online:
- https://convertio.co/es/mp3-ogg/
- https://www.online-convert.com/es/convertir-a-ogg
- O usa Audacity (software gratuito)

## ✅ Instalación

1. Descarga los 6 archivos de sonido
2. Nómbralos exactamente como se indica arriba
3. Colócalos en `app/src/main/res/raw/`
4. Compila la app

¡El sistema de sonido ya está integrado y funcionará automáticamente!

## 🔇 Notas

- Si no añades los archivos, la app funcionará perfectamente sin sonidos
- El SoundManager detecta automáticamente qué archivos existen
- No se producirán errores si falta algún archivo de sonido
- Los sonidos se reproducen con los siguientes eventos del juego:
  - `GAME_START`: Cuando comienza la partida
  - `BOMB_TICK`: Loop continuo mientras hay una sílaba activa
  - `BOMB_EXPLODE`: Cuando explota la bomba
  - `WORD_CORRECT`: Palabra aceptada (+1 vida o normal)
  - `WORD_INCORRECT`: Palabra rechazada
  - `VICTORY`: Cuando alguien gana la partida
