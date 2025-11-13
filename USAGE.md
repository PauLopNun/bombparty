# 📖 Guía de Uso - BombParty

Esta guía te enseñará paso a paso cómo jugar BombParty desde la instalación hasta dominar el juego.

## 📱 Instalación

### Paso 1: Descargar la APK

1. Desde tu móvil Android, abre el navegador
2. Ve a: [https://github.com/PauLopNun/bombparty/releases/latest](https://github.com/PauLopNun/bombparty/releases/latest)
3. Toca el archivo `BombParty-vX.X.X.apk` para descargarlo
4. Espera a que termine la descarga

### Paso 2: Habilitar Instalación de Fuentes Desconocidas

**Android 8.0 y superior:**
1. Cuando intentes instalar, aparecerá un mensaje
2. Toca "Configuración"
3. Activa "Permitir desde esta fuente"
4. Vuelve atrás

**Android 7.x y anterior:**
1. Ve a Configuración → Seguridad
2. Activa "Fuentes desconocidas"
3. Confirma

### Paso 3: Instalar

1. Abre el archivo APK descargado
2. Toca "Instalar"
3. Espera a que termine la instalación
4. Toca "Abrir" o encuentra la app en tu lista de aplicaciones

---

## 🎮 Tu Primera Partida

### Opción A: Crear una Sala (Ser el Host)

#### 1. Pantalla Principal

Al abrir la app verás el menú principal con tres opciones:

- **Crear Sala** ← Toca aquí
- Unirse a Sala
- Configuración

#### 2. Configurar la Partida

Aparecerá la pantalla de configuración con estas opciones:

**Nombre de Jugador**
- Escribe tu nombre o apodo
- Máximo 20 caracteres

**Idioma del Diccionario**
- 🇪🇸 Español (recomendado si todos hablan español)
- 🇬🇧 English

**Dificultad**
- 🟢 Principiante: Sílabas fáciles (500+ palabras)
- 🟡 Intermedio: Sílabas moderadas (300+ palabras)
- 🔴 Avanzado: Sílabas difíciles (100+ palabras)
- ⚙️ Personalizado: Define tu propio rango

**Opciones Avanzadas** (puedes dejarlas por defecto)
- Duración mínima de turno: 5 segundos
- Vida máxima de sílaba: 2 turnos
- Vidas iniciales: 2
- Vidas máximas: 3
- Jugadores máximos: 16

#### 3. Crear la Sala

1. Toca el botón "Crear Sala"
2. Espera unos segundos mientras se conecta al servidor
3. Verás la **Sala de Espera (Lobby)**

#### 4. Sala de Espera

En el lobby verás:

- **Código de la sala** (en la parte superior)
  - Ejemplo: `ABCD1234`
  - Comparte este código con tus amigos

- **Lista de jugadores**
  - Tu nombre aparecerá con una corona 👑 (eres el host)
  - Los jugadores que se unan aparecerán aquí

- **Botón "Iniciar Juego"**
  - Solo visible para el host
  - Se habilita cuando hay al menos 2 jugadores

#### 5. Compartir el Código

Envía el código de sala a tus amigos por:
- WhatsApp
- Telegram
- Discord
- SMS
- O díselo verbalmente

#### 6. Iniciar el Juego

1. Espera a que se unan al menos 1 amigo (mínimo 2 jugadores)
2. Toca "Iniciar Juego"
3. ¡Comienza la partida!

---

### Opción B: Unirse a una Sala

#### 1. Pantalla Principal

- Crear Sala
- **Unirse a Sala** ← Toca aquí
- Configuración

#### 2. Ingresar Código

1. Escribe tu **nombre de jugador**
2. Ingresa el **código de sala** que te compartieron
   - Ejemplo: `ABCD1234`
3. Toca "Unirse"

#### 3. Sala de Espera

- Verás la lista de jugadores ya conectados
- El host tendrá una corona 👑
- Espera a que el host inicie el juego

---

## 🎯 Jugando

### Pantalla de Juego

Una vez que el juego inicia, verás:

```
┌─────────────────────────────┐
│     Lista de Jugadores      │
│  👑 Host        ❤️❤️❤️      │
│     Jugador2    ❤️❤️        │
│  ➡️ Jugador3    ❤️❤️❤️      │ ← Turno actual
│     Jugador4    ❤️❤️        │
└─────────────────────────────┘

┌─────────────────────────────┐
│                             │
│          💣 [BO] 💣         │ ← Sílaba actual
│                             │
│         ⏱️ 8s ⏱️            │ ← Tiempo restante
│                             │
└─────────────────────────────┘

┌─────────────────────────────┐
│  [_______________]  [Enviar]│ ← Campo de texto
└─────────────────────────────┘

Palabras usadas: BOLA,ABO...
```

### Tu Turno

#### 1. Observa la Sílaba

En el centro de la bomba verás una sílaba, por ejemplo: **BO**

#### 2. Piensa una Palabra

La palabra debe:
- ✅ Contener la sílaba "BO" (en cualquier posición)
- ✅ Tener mínimo 3 letras
- ✅ No haber sido usada en esta partida
- ✅ Estar en el diccionario

Ejemplos válidos para "BO":
- **BO**LA
- CA**BO**
- TRA**BO**
- SÍM**BO**LO

#### 3. Escribir y Enviar

1. Toca el campo de texto
2. Escribe la palabra (sin acentos, todo en mayúsculas o minúsculas)
3. Presiona **Enter** en el teclado o toca **Enviar**

#### 4. Resultado

**✅ Palabra Correcta:**
- La bomba pasa al siguiente jugador
- Tu palabra aparece en la lista de "Palabras usadas"
- Recibes una confirmación visual

**❌ Palabra Incorrecta:**
- Aparece un mensaje de error:
  - "Palabra ya usada"
  - "Palabra no encontrada en el diccionario"
  - "La palabra no contiene la sílaba"
  - "Palabra muy corta"
- Puedes intentar con otra palabra
- Si el tiempo se acaba, pierdes una vida ❤️

#### 5. Tiempo Agotado

Si no respondes a tiempo:
- 💔 Pierdes una vida
- La bomba "explota" con una animación
- La bomba pasa al siguiente jugador con una nueva sílaba

---

### Sistema de Vidas

- Cada jugador empieza con **2 vidas** ❤️❤️ (configurable)
- Pierdes una vida si:
  - ⏱️ Se te acaba el tiempo
  - ❌ No puedes escribir una palabra válida
- Ganas una vida si:
  - ⭐ Usas una letra **bonus** (amarilla)
- Máximo de vidas: **3** ❤️❤️❤️ (configurable)
- Sin vidas: **Eliminado** 💀

---

### Letras Bonus ⭐

#### ¿Qué son?

Aparecen letras **amarillas** en la pantalla de juego.

#### ¿Cómo funcionan?

1. Si tu palabra contiene la letra bonus, ganas **+1 vida**
2. Ejemplo:
   - Sílaba: **RA**
   - Letra bonus: **P**
   - Si escribes **PA**RA o PE**RA** → ¡+1 vida!

#### Estrategia

- Intenta usar las letras bonus cuando puedas
- Pero no arriesgues perder tiempo buscando palabras complicadas

---

### Vida de la Sílaba

Las sílabas tienen una "vida" limitada (por defecto 2 turnos):

- Si todos los jugadores logran responder con la misma sílaba
- Después de 2 turnos, cambia a una nueva sílaba automáticamente
- Esto evita que se abuse de palabras fáciles

---

### Aceleración del Temporizador

- Al inicio de la partida: 10-15 segundos por turno
- Conforme avanza: El tiempo disminuye gradualmente
- Últimas rondas: Puede ser tan bajo como 3-5 segundos
- Esto aumenta la dificultad y emoción

---

## 🏆 Ganando la Partida

### Condición de Victoria

El último jugador que tenga vidas es el **ganador** 🎉

### Pantalla de Victoria

Al terminar verás:

```
┌─────────────────────────────┐
│      🎉 ¡VICTORIA! 🎉       │
│                             │
│      👑 [Nombre] 👑         │
│       HA GANADO!            │
│                             │
│   [Volver al Menú]          │
└─────────────────────────────┘
```

### Después de la Partida

Opciones:
- **Volver al Menú**: Regresa a la pantalla principal
- **Crear Nueva Sala**: Empieza otra partida
- **Cerrar la App**: Guarda tus estadísticas (si aplica)

---

## ⚙️ Configuración

### Acceder a Configuración

Desde el menú principal:
- Toca el ícono de **engranaje** ⚙️
- O toca **Configuración**

### Opciones Disponibles

**Sonido**
- Activar/desactivar efectos de sonido
- Volumen

**Notificaciones**
- Vibración al perder vida
- Sonido de alerta

**Idioma de la Interfaz**
- Español
- English

**Acerca de**
- Versión de la app
- Créditos
- Licencia

---

## 💡 Consejos y Estrategias

### Para Principiantes

1. **Practica con palabras comunes**
   - No busques palabras complicadas al inicio
   - Usa vocabulario cotidiano

2. **Observa las palabras de otros**
   - Aprende de las palabras que usan otros jugadores
   - Inspírate para futuras rondas

3. **Gestiona tu tiempo**
   - No uses todo el tiempo en cada turno
   - Deja margen para errores de escritura

4. **Lee la lista de palabras usadas**
   - Evita repetir palabras
   - Te ayuda a pensar en nuevas opciones

### Para Jugadores Intermedios

1. **Memoriza palabras útiles**
   - Ten un repertorio mental de palabras cortas
   - Útil para sílabas comunes (LA, RA, DO, etc.)

2. **Prioriza velocidad sobre perfección**
   - Una palabra simple rápida es mejor que una compleja tardía
   - Evita quedarte sin tiempo

3. **Usa el teclado predictivo**
   - Activa las sugerencias del teclado
   - Te ayuda a escribir más rápido

4. **Practica con letras bonus**
   - Cuando tengas 2 vidas, intenta obtener la bonus
   - No arriesgues si tienes 1 vida

### Para Jugadores Avanzados

1. **Sílabas difíciles**
   - Prepárate mentalmente para sílabas raras (QU, X, W)
   - Ten palabras de respaldo en mente

2. **Anticipa sílabas**
   - Mientras otros juegan, piensa en palabras para la sílaba actual
   - Estarás preparado si vuelve a ti

3. **Estrategia de vidas**
   - Con 3 vidas, puedes arriesgar más
   - Con 1 vida, juega seguro

4. **Posición de sílaba**
   - Practica encontrar sílabas al inicio, medio y final
   - Ejemplos: **MA**no, ca**MA**, ali**MA**

---

## 🐛 Problemas Comunes

### No puedo conectarme al servidor

**Síntomas:**
- Mensaje: "Error de conexión"
- La app no crea ni se une a salas

**Soluciones:**
1. Verifica tu conexión a Internet
   - Abre el navegador y prueba una página web
2. Reinicia la app
   - Cierra completamente y vuelve a abrir
3. Verifica el estado del servidor
   - Visita: https://97b87797-ba85-4845-a26d-11759c5ea25f.railway.app/health
   - Debe decir "OK"
4. Reinstala la app
   - Desinstala y vuelve a instalar la última versión

---

### La partida se congela

**Síntomas:**
- La interfaz no responde
- El temporizador no avanza
- No puedes escribir

**Soluciones:**
1. Cierra y vuelve a abrir la app
2. Verifica tu conexión a Internet
3. Si persiste, reporta el bug con capturas de pantalla

---

### Mi palabra no es aceptada

**Causas posibles:**

1. **Palabra no en el diccionario**
   - Verifica la ortografía
   - Asegúrate de que es una palabra real
   - No uses nombres propios

2. **Palabra ya usada**
   - Revisa la lista de "Palabras usadas"
   - Prueba con otra palabra

3. **No contiene la sílaba**
   - Verifica que la sílaba esté completa en tu palabra
   - Ejemplo: Si la sílaba es "BO", "B" + "OLA" no cuenta

4. **Muy corta**
   - Mínimo 3 letras
   - "BO" no es válida, "BOLA" sí

---

### No veo a mis amigos en la sala

**Soluciones:**
1. Verifica que usen el **mismo código de sala**
2. Asegúrate de que estén conectados a Internet
3. Pídeles que reinicien la app
4. El host puede salir y crear nueva sala

---

### El juego va muy rápido/lento

**Ajustar dificultad:**

Solo el **host** puede ajustar antes de iniciar:
1. Al crear la sala, toca "Configuración avanzada"
2. Cambia "Duración mínima de turno"
   - Más tiempo: 10-15 segundos (principiantes)
   - Menos tiempo: 3-5 segundos (expertos)
3. Guarda cambios y crea la sala

---

## 📞 Soporte y Contacto

### Reportar Bugs

1. Ve a: [https://github.com/PauLopNun/bombparty/issues](https://github.com/PauLopNun/bombparty/issues)
2. Toca "New Issue"
3. Describe el problema:
   - ¿Qué estabas haciendo?
   - ¿Qué esperabas que pasara?
   - ¿Qué pasó realmente?
   - Adjunta capturas de pantalla si es posible

### Sugerencias

¿Tienes ideas para mejorar el juego?
- Abre un issue en GitHub con la etiqueta "enhancement"
- Describe tu sugerencia en detalle

---

## 🎉 ¡Disfruta el Juego!

Ahora que conoces todos los detalles, ¡es hora de jugar y divertirte con tus amigos!

**Recuerda:**
- Practica para mejorar
- No te frustres si pierdes al inicio
- Lo importante es divertirse 🎮

---

**¿Más preguntas?** Consulta el [README.md](README.md) o abre un issue en GitHub.
