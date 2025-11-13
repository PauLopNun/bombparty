# 🖥️ Guía Completa del Servidor - BombParty

## 🤔 ¿Qué es y para qué sirve?

El **servidor** es un programa que:
- Corre en tu computadora (o en internet)
- Coordina a todos los jugadores
- Sincroniza el juego en tiempo real
- Valida las palabras y maneja las reglas

**Analogía**: Es como el árbitro en un partido de fútbol - coordina todo y asegura que todos sigan las reglas.

---

## 🎯 Opción 1: Ejecutar desde Android Studio (RECOMENDADO)

### Paso 1: Abrir la ventana de Terminal en Android Studio

1. En Android Studio, en la parte inferior, busca la pestaña **"Terminal"**
2. Click en ella (se abrirá una consola)

### Paso 2: Ejecutar el servidor

En la terminal que se abrió, escribe:

**En Windows:**
```bash
gradlew.bat :server:run
```

**En Mac/Linux:**
```bash
./gradlew :server:run
```

### Paso 3: Verificar que funciona

Verás algo como esto:
```
[main] INFO Application - Responding at http://0.0.0.0:8080
```

✅ **¡Listo!** El servidor está corriendo.

**IMPORTANTE:** NO cierres esta ventana de terminal mientras quieras jugar.

---

## 🎯 Opción 2: Ejecutar desde CMD/PowerShell (Windows)

### Paso 1: Abrir PowerShell

1. Presiona `Windows + R`
2. Escribe `powershell`
3. Presiona Enter

### Paso 2: Navegar a la carpeta del proyecto

```powershell
cd C:\Users\paulo\Desktop\bombpartykotlin
```

### Paso 3: Ejecutar el servidor

```powershell
.\gradlew.bat :server:run
```

### Paso 4: Verificar

Verás mensajes en la consola. Cuando veas:
```
[main] INFO Application - Responding at http://0.0.0.0:8080
```

✅ El servidor está listo.

---

## 🎯 Opción 3: Crear un archivo .bat para Windows (MÁS FÁCIL)

Voy a crear un archivo que hace todo automáticamente.

### Archivo: `iniciar-servidor.bat`

Simplemente:
1. Encuentra el archivo `iniciar-servidor.bat` en la carpeta del proyecto
2. Haz doble click
3. Se abrirá una ventana negra con el servidor corriendo

---

## 🔌 Conectar la App al Servidor

Una vez el servidor esté corriendo, necesitas decirle a la app dónde encontrarlo.

### Para Emulador de Android

1. Abre el archivo:
   ```
   app/src/main/java/com/bombparty/presentation/screens/game/GameViewModel.kt
   ```

2. Busca la línea 46 (aproximadamente) que dice:
   ```kotlin
   viewModel.connectToServer("ws://your-server-url:8080/game")
   ```

3. Cámbiala por:
   ```kotlin
   viewModel.connectToServer("ws://10.0.2.2:8080/game")
   ```

**¿Por qué 10.0.2.2?**
Es una dirección especial que el emulador entiende como "mi computadora".

### Para Dispositivo Físico (Celular/Tablet Real)

1. Asegúrate de que tu dispositivo y tu PC estén en la **misma red WiFi**

2. Encuentra tu IP local:
   - Abre PowerShell o CMD
   - Escribe: `ipconfig`
   - Busca "Dirección IPv4" (algo como 192.168.1.100)

3. En el código, usa:
   ```kotlin
   viewModel.connectToServer("ws://192.168.1.100:8080/game")
   ```
   (Reemplaza 192.168.1.100 con tu IP real)

---

## ✅ Verificar que el Servidor Funciona

### Método 1: Navegador Web

1. Abre Chrome, Firefox, o cualquier navegador
2. Ve a: `http://localhost:8080/health`
3. Deberías ver: **OK**

### Método 2: PowerShell/CMD

```bash
curl http://localhost:8080/health
```

Debería responder: **OK**

---

## 🐛 Solución de Problemas

### Problema 1: "El término 'gradlew' no se reconoce"

**Solución:**
Asegúrate de estar en la carpeta correcta:
```powershell
cd C:\Users\paulo\Desktop\bombpartykotlin
```

Luego usa:
```powershell
.\gradlew.bat :server:run
```
(Nota el `.\` al principio)

---

### Problema 2: "Puerto 8080 ya está en uso"

**Solución A:** Cerrar el programa que usa el puerto

1. Abre PowerShell como Administrador
2. Escribe:
   ```powershell
   netstat -ano | findstr :8080
   ```
3. Verás algo como: `TCP  0.0.0.0:8080  0.0.0.0:0  LISTENING  1234`
4. El número al final (1234) es el PID. Mátalo con:
   ```powershell
   taskkill /PID 1234 /F
   ```

**Solución B:** Usar otro puerto

1. Abre `server/src/main/kotlin/com/bombparty/server/Application.kt`
2. En la línea 12, cambia `port = 8080` por `port = 8081`
3. En GameViewModel.kt, cambia `:8080` por `:8081`

---

### Problema 3: "Cannot connect to server" en la app

**Causas comunes:**

1. **El servidor no está corriendo**
   - Verifica que la terminal con el servidor siga abierta
   - Verifica en el navegador: `http://localhost:8080/health`

2. **URL incorrecta en el código**
   - Para emulador: `ws://10.0.2.2:8080/game`
   - Para dispositivo físico: `ws://TU_IP:8080/game`

3. **Firewall bloqueando**
   - Windows puede preguntar si quieres permitir la conexión
   - Click en "Permitir acceso"

4. **Dispositivo físico en red diferente**
   - Asegúrate de que tu celular y PC estén en la misma WiFi

---

### Problema 4: El servidor se cierra solo

**Solución:**
- NO cierres la ventana de terminal/PowerShell
- Si necesitas detenerlo, presiona `Ctrl + C`

---

## 📱 Flujo Completo para Probar

### Configuración Inicial (Solo una vez)

1. **Abre Android Studio**
2. **Abre Terminal** (pestaña inferior)
3. **Ejecuta:** `gradlew.bat :server:run` (Windows) o `./gradlew :server:run` (Mac/Linux)
4. **Espera** a ver: "Responding at http://0.0.0.0:8080"
5. **Edita GameViewModel.kt** línea ~46:
   - Para emulador: `ws://10.0.2.2:8080/game`
   - Para dispositivo: `ws://TU_IP:8080/game`
6. **Compila y ejecuta** la app

### Cada vez que quieras jugar

1. **Inicia el servidor** (terminal: `gradlew.bat :server:run`)
2. **Ejecuta la app** (botón Run ▶️ en Android Studio)
3. **Juega**
4. **Cuando termines**, en la terminal presiona `Ctrl + C` para detener el servidor

---

## 🎮 Probar Multijugador

### En un solo dispositivo (Prueba básica)

1. Ejecuta el servidor
2. Ejecuta la app
3. Navega por los menús (aunque no hará mucho todavía)

### Con múltiples dispositivos

1. **Ejecuta el servidor** en tu PC
2. **Ejecuta la app** en 2 emuladores o dispositivos:
   - Emulador 1: Jugador 1
   - Emulador 2: Jugador 2
3. **Jugador 1**: Crea una sala (cuando implementes CreateRoomScreen)
4. **Jugador 2**: Se une con el código
5. **Juegan juntos**

---

## 💡 Consejos

### Durante Desarrollo

- **Deja el servidor corriendo** mientras desarrollas
- Si cambias código del servidor, presiona `Ctrl + C` y vuelve a ejecutar
- Si cambias código de la app, solo haz "Run" nuevamente

### Logs Útiles

En la terminal del servidor verás mensajes como:
```
[INFO] WebSocket - Client connected
[INFO] GameManager - Room created: ABC123
[INFO] GameManager - Player joined: Player1
```

Esto te ayuda a saber qué está pasando.

### Errores Comunes

- **"Connection refused"**: El servidor no está corriendo
- **"Timeout"**: URL incorrecta o firewall bloqueando
- **"Invalid room"**: El código de sala no existe

---

## 🎓 Entendiendo el Servidor

### ¿Qué hace exactamente?

1. **Espera conexiones** en el puerto 8080
2. **Crea salas** cuando un jugador lo pide
3. **Maneja mensajes** de los jugadores:
   - "Crear sala"
   - "Unirse a sala"
   - "Iniciar juego"
   - "Enviar palabra"
4. **Sincroniza** a todos los jugadores
5. **Aplica reglas** del juego

### Archivos importantes del servidor

- `Application.kt`: Inicia el servidor
- `GameManager.kt`: Lógica del juego
- `GameRoutes.kt`: Maneja conexiones WebSocket

---

## 🚀 Siguiente Nivel: Servidor en Internet

Por ahora el servidor corre en tu PC. Si quieres que amigos jueguen desde otras redes:

### Opciones:

1. **ngrok** (Gratis, fácil):
   - Descarga ngrok
   - Ejecuta: `ngrok http 8080`
   - Te da una URL pública: `ws://abc123.ngrok.io/game`

2. **Servidor en la nube** (Railway, Heroku, etc.):
   - Subes el código del servidor
   - Obtienes una URL permanente

Esto es avanzado, por ahora enfócate en hacerlo funcionar localmente.

---

## ✅ Checklist Rápido

Antes de ejecutar la app, verifica:

- [ ] Servidor corriendo (terminal muestra "Responding at...")
- [ ] URL correcta en GameViewModel.kt
- [ ] Si usas dispositivo físico, misma WiFi
- [ ] Firewall permite conexiones
- [ ] Puerto 8080 disponible

---

## 🆘 ¿Todavía tienes problemas?

Comparte:
1. El mensaje de error exacto
2. Si usas emulador o dispositivo físico
3. Los logs del servidor (lo que aparece en la terminal)

---

**Próximo paso:** Una vez que el servidor esté corriendo y conectado, ¡podrás empezar a jugar!
