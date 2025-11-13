# 🎯 PASOS SIMPLES - Ejecutar BombParty

## 📋 Resumen Rápido (3 pasos)

1. ✅ Ejecutar el servidor
2. ✅ Ejecutar la app
3. ✅ ¡Jugar!

---

## 🚀 MÉTODO 1: Super Fácil (Windows)

### Paso 1: Ejecutar el Servidor

Encuentra el archivo `iniciar-servidor.bat` en la carpeta del proyecto:
```
C:\Users\paulo\Desktop\bombpartykotlin\iniciar-servidor.bat
```

**HAZ DOBLE CLICK** en él.

Verás una ventana negra que dice:
```
========================================
   SERVIDOR BOMBPARTY
========================================

Iniciando servidor en puerto 8080...
```

Después de unos segundos verás:
```
[main] INFO Application - Responding at http://0.0.0.0:8080
```

✅ **¡Listo!** El servidor está corriendo.

**IMPORTANTE:** NO cierres esta ventana.

---

### Paso 2: Ejecutar la App

1. Abre **Android Studio**
2. Abre el proyecto (si no está abierto)
3. Espera a que termine de sincronizar (barra de progreso abajo)
4. Click en el botón verde **▶️ Run** (arriba a la derecha)
5. Espera a que la app se instale en el emulador

---

### Paso 3: ¡Jugar!

La app se abrirá y verás el menú principal.

**Nota:** Las pantallas de "Crear Sala" y "Unirse a Sala" aún no están completamente implementadas, pero puedes navegar por los menús.

---

## 🚀 MÉTODO 2: Desde Android Studio

### Paso 1: Abrir Terminal en Android Studio

1. Abre **Android Studio**
2. En la parte inferior, busca la pestaña **"Terminal"**
3. Click en ella

Verás algo como:
```
PS C:\Users\paulo\Desktop\bombpartykotlin>
```

---

### Paso 2: Ejecutar el Servidor

En la terminal, escribe:
```bash
.\gradlew.bat :server:run
```

Presiona **Enter**.

Verás muchos mensajes. Espera a ver:
```
[main] INFO Application - Responding at http://0.0.0.0:8080
```

✅ **El servidor está listo.**

---

### Paso 3: Ejecutar la App

1. Click en el botón verde **▶️ Run** (arriba a la derecha)
2. Espera a que la app se instale
3. ¡Listo!

---

## 🔍 Verificar que Todo Funciona

### Verificar el Servidor

Abre tu navegador (Chrome, Firefox, etc.) y ve a:
```
http://localhost:8080/health
```

Deberías ver:
```
OK
```

Si ves eso, ¡el servidor funciona perfectamente! ✅

---

## 🛑 Detener el Servidor

Cuando termines de jugar:

1. Ve a la ventana donde está corriendo el servidor
2. Presiona **Ctrl + C**
3. El servidor se detendrá

---

## 🖥️ Para Emulador vs Dispositivo Físico

### Ya está configurado para Emulador ✅

Por defecto, la app está configurada para conectarse al servidor si usas el **emulador de Android**.

No necesitas cambiar nada.

---

### Para Dispositivo Físico (Celular/Tablet)

Si quieres probar en tu celular:

**1. Encuentra tu IP:**

Abre PowerShell/CMD y escribe:
```bash
ipconfig
```

Busca algo como:
```
Adaptador de LAN inalámbrica Wi-Fi:
   Dirección IPv4. . . . . . . . . : 192.168.1.100
```

Esa es tu IP (en este ejemplo: **192.168.1.100**)

**2. Edita GameScreen.kt:**

Abre el archivo:
```
app/src/main/java/com/bombparty/presentation/screens/game/GameScreen.kt
```

Busca la línea 44 (aproximadamente):
```kotlin
viewModel.connectToServer("ws://10.0.2.2:8080/game")
```

Cámbiala por (usando TU IP):
```kotlin
viewModel.connectToServer("ws://192.168.1.100:8080/game")
```

**3. Conecta tu celular:**

- Conecta el celular con USB
- Habilita "Depuración USB" en el celular
- En Android Studio, selecciona tu dispositivo
- Click en Run ▶️

---

## ❓ Preguntas Frecuentes

### ¿Necesito tener el servidor corriendo siempre?

Solo cuando quieras usar la funcionalidad multijugador. Si solo quieres ver las pantallas de la UI, no es necesario.

---

### ¿Puedo cerrar Android Studio después de ejecutar el servidor?

No, si ejecutaste el servidor desde la terminal de Android Studio, al cerrar Android Studio se cerrará el servidor.

Usa el archivo `iniciar-servidor.bat` si quieres que el servidor siga corriendo independientemente.

---

### ¿El servidor consume muchos recursos?

No, es muy ligero. Apenas notarás que está corriendo.

---

### ¿Puedo ejecutar el servidor y la app en diferentes computadoras?

Sí, pero necesitarás:
1. El servidor corriendo en una computadora con IP fija o dominio
2. Configurar la URL en GameScreen.kt con esa IP
3. Asegurarte de que el firewall permita conexiones en el puerto 8080

---

## 🐛 Si Algo Sale Mal

### Error: "El término 'gradlew' no se reconoce"

Asegúrate de estar en la carpeta correcta:
```bash
cd C:\Users\paulo\Desktop\bombpartykotlin
```

Luego usa:
```bash
.\gradlew.bat :server:run
```

---

### Error: "Puerto 8080 ya está en uso"

Algo más está usando ese puerto. Opciones:

**A) Cerrar lo que lo usa:**
```powershell
# En PowerShell como Administrador
netstat -ano | findstr :8080
# Anota el número (PID) al final
taskkill /PID NUMERO /F
```

**B) Usar otro puerto:**

1. Edita `server/src/main/kotlin/com/bombparty/server/Application.kt`
2. Cambia `port = 8080` por `port = 8081`
3. En GameScreen.kt cambia `:8080` por `:8081`

---

### La app no se conecta al servidor

1. ¿El servidor está corriendo? Verifica en el navegador: `http://localhost:8080/health`
2. ¿Usas emulador? La URL debe ser `ws://10.0.2.2:8080/game`
3. ¿Usas dispositivo físico? La URL debe ser `ws://TU_IP:8080/game`
4. ¿Misma red WiFi? Tu PC y dispositivo deben estar en la misma red

---

## 📚 Siguientes Pasos

Una vez que el servidor y la app funcionen:

1. **Explora el código** - Revisa los archivos creados
2. **Implementa las pantallas faltantes**:
   - CreateRoomScreen (crear sala)
   - JoinRoomScreen (unirse a sala)
   - LobbyScreen (sala de espera)
3. **Prueba el multijugador** - Ejecuta 2 emuladores
4. **Agrega features** - Sonidos, animaciones, más configuraciones

---

## 🎓 Recursos

- **SERVIDOR_GUIA.md** - Guía detallada del servidor
- **README.md** - Documentación completa
- **QUICKSTART.md** - Guía de inicio rápido
- **PROJECT_SUMMARY.md** - Resumen del proyecto

---

## ✅ Checklist Final

Antes de ejecutar la app:

- [ ] Servidor corriendo (ventana con "Responding at...")
- [ ] Android Studio abierto
- [ ] Proyecto sincronizado (sin errores)
- [ ] Emulador listo o dispositivo conectado
- [ ] URL correcta en GameScreen.kt

---

**¡Estás listo para comenzar! 🎉**

Si tienes dudas, revisa **SERVIDOR_GUIA.md** para más detalles.
