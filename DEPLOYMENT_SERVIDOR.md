# 🌐 Desplegar el Servidor para Producción

## 🤔 El Problema

**Desarrollo (ahora):**
```
Tu PC → Servidor local (localhost:8080)
         ↓
    Tu emulador/celular se conecta
```

**Producción (app en Google Play):**
```
Usuario 1 en España
Usuario 2 en México     →  ¿Dónde está el servidor? ❌
Usuario 3 en Argentina
```

**Solución:** Alojar el servidor en la nube (internet) 24/7.

---

## 🎯 Opciones para Alojar el Servidor

### 1️⃣ **Railway** (Recomendado - GRATIS para empezar)

**Ventajas:**
- ✅ Gratis hasta 500 horas/mes (suficiente para empezar)
- ✅ Deploy automático desde GitHub
- ✅ Muy fácil de configurar
- ✅ Soporta WebSockets
- ✅ Te da una URL automática (ej: `mi-app.railway.app`)

**Costo después del plan gratis:**
- $5-10 USD/mes aprox.

**Pasos:**
1. Crea cuenta en https://railway.app
2. Conecta tu repositorio GitHub
3. Deploy automático
4. Te da una URL: `https://tu-app.railway.app`

---

### 2️⃣ **Render** (Alternativa GRATIS)

**Ventajas:**
- ✅ Plan gratis permanente
- ✅ Fácil de usar
- ✅ Soporta WebSockets
- ⚠️ Se "duerme" después de 15 min sin uso (tarda 30s en despertar)

**Costo:**
- Gratis para siempre (con limitaciones)
- Plan paid: $7/mes (servidor siempre activo)

**Ideal para:** Proyectos pequeños, demos, portfolios

---

### 3️⃣ **Fly.io** (Opción Intermedia)

**Ventajas:**
- ✅ Gratis hasta 3 apps pequeñas
- ✅ Servidores globales (baja latencia)
- ✅ Muy rápido

**Costo:**
- Gratis hasta cierto uso
- Después ~$5-15/mes

---

### 4️⃣ **VPS Tradicional** (Para apps grandes)

**Opciones:**
- DigitalOcean Droplet: $5-10/mes
- AWS EC2: Variable
- Google Cloud: Variable
- Linode: $5/mes

**Ventajas:**
- ✅ Control total
- ✅ Escalable

**Desventajas:**
- ❌ Necesitas configurar todo manualmente
- ❌ Más técnico

---

### 5️⃣ **Ngrok** (Solo para TESTING, no producción)

**Para qué sirve:**
- Exponer tu servidor local a internet temporalmente
- Pruebas rápidas con amigos

**NO es para producción:**
- Se cae cuando apagas tu PC
- URL cambia cada vez
- Limitado en el plan gratis

**Uso:**
```bash
# 1. Descarga ngrok
# 2. Inicia tu servidor local
gradlew.bat :server:run

# 3. En otra terminal
ngrok http 8080

# Te da una URL temporal como:
# https://abc123.ngrok.io
```

Cambias la URL en la app:
```kotlin
viewModel.connectToServer("wss://abc123.ngrok.io/game")
```

---

## 🚀 Guía: Desplegar en Railway (Recomendado)

### Paso 1: Preparar el Proyecto

1. **Crear archivo `Procfile` en la raíz del servidor:**

```bash
# server/Procfile
web: java -jar build/libs/server-1.0.0-all.jar
```

2. **Agregar tarea shadowJar al build.gradle del servidor:**

Ya lo tengo preparado, pero asegúrate de que esté en `server/build.gradle.kts`:

```kotlin
plugins {
    // ... otros plugins
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks {
    shadowJar {
        archiveBaseName.set("server")
        archiveVersion.set("1.0.0")
        archiveClassifier.set("all")
    }
}
```

3. **Crear `railway.json` en la raíz del proyecto:**

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "./gradlew :server:shadowJar"
  },
  "deploy": {
    "startCommand": "java -jar server/build/libs/server-1.0.0-all.jar",
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

---

### Paso 2: Subir a GitHub

```bash
# En la carpeta del proyecto
git init
git add .
git commit -m "Initial commit - BombParty"

# Crea un repo en GitHub y luego:
git remote add origin https://github.com/TU_USUARIO/bombparty.git
git push -u origin main
```

---

### Paso 3: Deploy en Railway

1. **Ve a https://railway.app**
2. **Sign Up** con GitHub
3. **New Project** → **Deploy from GitHub repo**
4. Selecciona tu repositorio `bombparty`
5. Railway automáticamente:
   - Detecta que es un proyecto Kotlin
   - Ejecuta el build
   - Inicia el servidor
6. Te da una URL como: `https://bombparty-production.railway.app`

---

### Paso 4: Configurar Variables de Entorno

En Railway, ve a **Variables**:

```
PORT=8080
HOST=0.0.0.0
```

---

### Paso 5: Actualizar la App

En `GameScreen.kt`, cambia la URL:

```kotlin
// Antes (desarrollo local)
viewModel.connectToServer("ws://10.0.2.2:8080/game")

// Después (producción)
viewModel.connectToServer("wss://bombparty-production.railway.app/game")
```

**Nota:** Usa `wss://` (WebSocket Secure) en lugar de `ws://` para HTTPS.

---

### Paso 6: Actualizar el Servidor para HTTPS

En `Application.kt`, el código ya soporta WebSocket seguro automáticamente cuando está detrás de un proxy HTTPS (como Railway).

No necesitas cambiar nada en el servidor, Railway maneja el HTTPS automáticamente.

---

## 🔧 Código para Producción

### Opción A: URL Hardcoded (Simple)

```kotlin
// GameScreen.kt
val serverUrl = "wss://tu-servidor.railway.app/game"
viewModel.connectToServer(serverUrl)
```

**Ventaja:** Simple
**Desventaja:** Si cambias de servidor, necesitas actualizar la app

---

### Opción B: URL Configurable (Recomendado)

Crear un archivo de configuración:

```kotlin
// app/src/main/java/com/bombparty/utils/Config.kt
package com.bombparty.utils

object Config {
    // Cambiar según el entorno
    const val IS_PRODUCTION = true  // true para producción, false para desarrollo

    val SERVER_URL = if (IS_PRODUCTION) {
        "wss://bombparty-production.railway.app/game"  // Servidor en Railway
    } else {
        "ws://10.0.2.2:8080/game"  // Servidor local (emulador)
    }
}
```

Luego en `GameScreen.kt`:

```kotlin
import com.bombparty.utils.Config

LaunchedEffect(Unit) {
    viewModel.connectToServer(Config.SERVER_URL)
}
```

---

### Opción C: Build Variants (Profesional)

En `app/build.gradle.kts`:

```kotlin
android {
    buildTypes {
        debug {
            buildConfigField("String", "SERVER_URL", "\"ws://10.0.2.2:8080/game\"")
        }
        release {
            buildConfigField("String", "SERVER_URL", "\"wss://tu-servidor.railway.app/game\"")
        }
    }
}
```

Uso:
```kotlin
viewModel.connectToServer(BuildConfig.SERVER_URL)
```

**Ventaja:** Automático según el tipo de build

---

## 💰 Costos Estimados

### Para una App Pequeña (hasta 100 usuarios simultáneos)

| Servicio | Plan | Costo Mensual |
|----------|------|---------------|
| Railway | Hobby | $5 USD |
| Render | Free | $0 (con limitaciones) |
| Render | Starter | $7 USD |
| Fly.io | Free | $0 (hasta cierto uso) |
| DigitalOcean | Basic | $6 USD |

### Para una App Grande (miles de usuarios)

Necesitarás:
- Servidor más potente: $20-100/mes
- Base de datos: $10-50/mes
- CDN/Load Balancer: $10-30/mes
- Monitoreo: $10-20/mes

**Total:** $50-200/mes aproximadamente

---

## 🎯 Mi Recomendación para Ti

### Fase 1: Desarrollo (Ahora)
```
✅ Servidor local en tu PC
✅ Pruebas con emulador
✅ Gratis, 100% control
```

### Fase 2: Testing con Amigos
```
✅ Ngrok (temporal, gratis)
✅ O Railway plan gratis
✅ Pruebas reales con otros
```

### Fase 3: Lanzamiento Beta
```
✅ Railway/Render plan gratis
✅ 10-50 usuarios
✅ $0-7/mes
```

### Fase 4: Producción
```
✅ Railway/Render plan paid
✅ O VPS si tienes conocimientos
✅ $5-20/mes
```

---

## 📱 Estrategia de Release

### Opción 1: Solo Servidor Propio (Como JKLM.FUN)

```
Tu Servidor en la Nube ← Todos los usuarios se conectan
```

**Ventajas:**
- Control total
- Todos juegan juntos
- Una sola sala global

**Desventajas:**
- Pagas el servidor siempre
- Si se cae, nadie puede jugar
- Costos aumentan con usuarios

---

### Opción 2: Peer-to-Peer (Avanzado)

Un jugador hace de "host" y otros se conectan directamente a él.

**Ventajas:**
- No necesitas servidor
- Gratis para ti

**Desventajas:**
- Más complejo de implementar
- Requiere NAT traversal
- Problemas con firewalls

**Librerías:** WebRTC, libp2p

---

### Opción 3: Híbrido

- **Servidor en la nube** para matchmaking
- **Peer-to-peer** para el juego real

Como Among Us o Fall Guys.

---

## 🔐 Consideraciones de Seguridad

Cuando despliegues en producción:

1. **Rate Limiting:** Limitar peticiones por IP
2. **Validación:** Validar todas las palabras en el servidor
3. **Autenticación:** Sistema de cuentas (opcional)
4. **HTTPS/WSS:** Siempre usar conexiones seguras
5. **Firewall:** Solo abrir puertos necesarios
6. **Monitoreo:** Logs y alertas

---

## 📊 Escalabilidad

### Para 10-100 usuarios:
```
1 servidor básico ($5-10/mes) ✅
```

### Para 100-1,000 usuarios:
```
1 servidor medio ($20-30/mes)
+ Base de datos
+ Caché (Redis)
```

### Para 1,000-10,000 usuarios:
```
Múltiples servidores
+ Load balancer
+ Base de datos escalable
+ CDN
```

---

## 🛠️ Herramientas Útiles

### Monitoreo
- **Railway/Render Dashboard:** Métricas básicas incluidas
- **Sentry:** Tracking de errores (gratis hasta 5k eventos/mes)
- **Datadog:** Monitoreo avanzado

### Analytics
- **Firebase Analytics:** Gratis, fácil de integrar
- **Mixpanel:** Gratis hasta 100k usuarios/mes

### Base de Datos (para después)
- **PostgreSQL:** (Railway/Render lo incluyen)
- **MongoDB Atlas:** Gratis hasta 512 MB
- **Firebase Firestore:** Gratis hasta cierto uso

---

## 📝 Checklist para Producción

Antes de lanzar:

- [ ] Servidor desplegado en la nube
- [ ] URL de producción configurada en la app
- [ ] HTTPS/WSS habilitado
- [ ] Diccionarios completos cargados
- [ ] Rate limiting implementado
- [ ] Manejo de errores robusto
- [ ] Logs configurados
- [ ] Monitoreo activo
- [ ] Política de privacidad
- [ ] Términos de servicio

---

## 🎯 Plan de Acción Sugerido

### Semana 1-2: Desarrollo
- ✅ Ya tienes esto - servidor funciona localmente
- Implementar pantallas faltantes
- Agregar diccionarios completos
- Pruebas locales

### Semana 3: Deploy Beta
- Subir código a GitHub
- Deploy en Railway (plan gratis)
- Probar con 5-10 amigos
- Recolectar feedback

### Semana 4: Refinamiento
- Arreglar bugs
- Mejorar UI/UX
- Optimizar servidor
- Agregar analytics

### Semana 5: Lanzamiento
- Upgrade a plan paid si es necesario ($5-7/mes)
- Publicar en Google Play
- Promocionar

---

## 💡 Consejo Final

**No necesitas gastar dinero al principio.**

1. Desarrolla todo localmente (gratis)
2. Prueba con Railway gratis o Render gratis
3. Solo paga cuando tengas usuarios reales
4. Empieza pequeño, escala después

**Railway gratis te da 500 horas/mes = suficiente para 20 días 24/7.**

Perfecto para empezar y probar.

---

## 🆘 Ayuda Adicional

Si necesitas ayuda con:
- Deploy en Railway
- Configurar HTTPS
- Optimizar el servidor
- Escalar la app

Solo pregúntame y te guío paso a paso.

---

**En resumen:** Usa Railway (gratis al inicio) y solo pagas ~$5/mes cuando tengas usuarios reales. 🚀
