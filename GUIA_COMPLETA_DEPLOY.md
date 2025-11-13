# 🚀 Guía Completa: Deploy y GitHub Releases

## 📋 Resumen de lo que vamos a hacer:

1. ✅ Subir código a GitHub
2. ✅ Deploy del servidor en Railway
3. ✅ Configurar la app para usar el servidor de Railway
4. ✅ Configurar GitHub Actions para APK automático
5. ✅ Crear un Release y descargar en tu móvil

---

## 🎯 PASO 1: Crear Repositorio en GitHub

### 1.1 Crear el Repositorio

1. Ve a https://github.com
2. Click en el **+** (arriba derecha) → **New repository**
3. Configura:
   - **Repository name**: `bombparty` (o el nombre que prefieras)
   - **Description**: `BombParty - Juego de palabras multijugador para Android`
   - **Public** o **Private** (tu elección)
   - ❌ **NO** marques "Add a README" (ya tenemos uno)
4. Click en **Create repository**

### 1.2 Copiar la URL

Verás algo como:
```
https://github.com/TU_USUARIO/bombparty.git
```

**Guarda esta URL**, la necesitarás.

---

## 🎯 PASO 2: Subir Código a GitHub

### 2.1 Abrir Terminal en tu Proyecto

**Opción A: Desde Android Studio**
1. Abre Android Studio
2. Click en la pestaña **Terminal** (abajo)

**Opción B: Desde PowerShell**
1. Presiona `Windows + R`
2. Escribe `powershell`
3. Navega a tu proyecto:
   ```powershell
   cd C:\Users\paulo\Desktop\bombpartykotlin
   ```

### 2.2 Inicializar Git

```bash
git init
```

Deberías ver: `Initialized empty Git repository`

### 2.3 Agregar todos los archivos

```bash
git add .
```

### 2.4 Hacer el primer commit

```bash
git commit -m "Initial commit - BombParty Android"
```

### 2.5 Conectar con GitHub

Reemplaza `TU_USUARIO` con tu usuario de GitHub:

```bash
git remote add origin https://github.com/TU_USUARIO/bombparty.git
```

Ejemplo:
```bash
git remote add origin https://github.com/paulo123/bombparty.git
```

### 2.6 Subir el código

```bash
git branch -M main
git push -u origin main
```

**Si te pide credenciales:**
- Usuario: Tu usuario de GitHub
- Contraseña: **Personal Access Token** (no tu contraseña normal)

**¿No tienes token?**
1. Ve a GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token (classic)
3. Dale permisos: `repo` (marca toda la sección)
4. Copia el token y úsalo como contraseña

### 2.7 Verificar

Ve a tu repositorio en GitHub. Deberías ver todos los archivos. ✅

---

## 🎯 PASO 3: Deploy en Railway

### 3.1 Crear Cuenta en Railway

1. Ve a https://railway.app
2. Click en **Start a New Project**
3. **Login with GitHub** (recomendado)
4. Autoriza Railway

### 3.2 Crear Nuevo Proyecto

1. En el dashboard, click en **New Project**
2. Selecciona **Deploy from GitHub repo**
3. **Configure GitHub App** (si es primera vez)
   - Autoriza Railway a acceder a tus repos
4. Selecciona tu repositorio: `TU_USUARIO/bombparty`
5. Click en **Deploy Now**

### 3.3 Configurar Variables de Entorno

Railway empezará a hacer el deploy. Mientras tanto:

1. En tu proyecto de Railway, ve a **Variables**
2. Agrega estas variables:
   - `PORT`: `8080`
   - `HOST`: `0.0.0.0`

### 3.4 Esperar el Deploy

Verás logs en tiempo real. Espera a ver:
```
Starting server on 0.0.0.0:8080
[main] INFO Application - Responding at...
```

✅ **Deploy exitoso!**

### 3.5 Obtener la URL del Servidor

1. En tu proyecto de Railway, ve a **Settings**
2. Busca la sección **Networking**
3. Click en **Generate Domain**
4. Railway te dará una URL como:
   ```
   bombparty-production-abc123.up.railway.app
   ```

**Copia esta URL.** La necesitarás.

### 3.6 Verificar que Funciona

Abre tu navegador y ve a:
```
https://TU_URL_DE_RAILWAY.railway.app/health
```

Deberías ver: **OK** ✅

---

## 🎯 PASO 4: Configurar la App para Railway

### 4.1 Actualizar Config.kt

Abre el archivo:
```
app/src/main/java/com/bombparty/utils/Config.kt
```

Cambia estas líneas:

```kotlin
// Cambiar a true
const val IS_PRODUCTION = true

// Cambiar la URL (usa tu URL de Railway)
val SERVER_URL = if (IS_PRODUCTION) {
    "wss://TU_URL_DE_RAILWAY.railway.app/game"  // ← TU URL AQUÍ
} else {
    "ws://10.0.2.2:8080/game"
}
```

**Ejemplo real:**
```kotlin
const val IS_PRODUCTION = true

val SERVER_URL = if (IS_PRODUCTION) {
    "wss://bombparty-production-abc123.up.railway.app/game"
} else {
    "ws://10.0.2.2:8080/game"
}
```

**IMPORTANTE:** Usa `wss://` (con S), no `ws://`

### 4.2 Commit y Push

```bash
git add .
git commit -m "Configure production server URL"
git push
```

---

## 🎯 PASO 5: Crear Release en GitHub

### 5.1 Crear un Tag

En la terminal:

```bash
git tag -a v1.0.0 -m "Primera versión - BombParty v1.0.0"
git push origin v1.0.0
```

### 5.2 GitHub Actions se Ejecuta Automáticamente

1. Ve a tu repositorio en GitHub
2. Click en la pestaña **Actions**
3. Verás el workflow "Build and Release APK" ejecutándose
4. Espera unos 5-10 minutos

### 5.3 Verificar el Release

1. Ve a la pestaña **Releases** (barra lateral derecha)
2. Deberías ver **v1.0.0**
3. Verás un archivo `BombParty-v1.0.0.apk`

✅ **¡Listo! La APK está disponible para descargar.**

---

## 🎯 PASO 6: Instalar en tu Móvil

### 6.1 Desde el Móvil

1. Abre el navegador en tu móvil
2. Ve a tu repositorio de GitHub
3. Click en **Releases**
4. Click en `BombParty-v1.0.0.apk`
5. Descarga

### 6.2 Habilitar Instalación de Fuentes Desconocidas

**Android 8.0+:**
1. Cuando intentes instalar, Android te pedirá permiso
2. Click en **Configuración**
3. Habilita **Permitir de esta fuente**

**Android anterior:**
1. Ajustes → Seguridad
2. Habilita **Fuentes desconocidas**

### 6.3 Instalar

1. Abre el APK descargado
2. Click en **Instalar**
3. Espera...
4. Click en **Abrir**

✅ **¡La app está instalada!**

---

## 🎯 PASO 7: Probar la App

### 7.1 Abrir la App

Verás el menú principal de BombParty.

### 7.2 Verificar Conexión

La app intentará conectarse al servidor de Railway automáticamente.

**Para verificar:**
- Los logs en Railway mostrarán: "WebSocket - Client connected"

### 7.3 Probar Multijugador

**Necesitas 2 dispositivos:**

1. **Dispositivo 1:** Abre la app
2. **Dispositivo 2:** Abre la app (o descarga de GitHub Releases)
3. Intenta crear/unirse a una sala (cuando implementes esas pantallas)

---

## 🔄 Cómo Actualizar la App (Futuros Releases)

### Cada vez que hagas cambios:

```bash
# 1. Haz tus cambios en el código
# 2. Commit
git add .
git commit -m "Descripción de los cambios"
git push

# 3. Crear nuevo tag (incrementa la versión)
git tag -a v1.0.1 -m "Versión 1.0.1 - Descripción"
git push origin v1.0.1

# 4. GitHub Actions compilará automáticamente
# 5. Nueva APK disponible en Releases
```

---

## 📊 Monitoreo y Logs

### Ver Logs del Servidor (Railway)

1. Ve a tu proyecto en Railway
2. Click en tu servicio
3. Pestaña **Deployments**
4. Click en el deployment activo
5. Verás logs en tiempo real

**Logs útiles:**
```
[INFO] WebSocket - Client connected
[INFO] GameManager - Room created: ABC123
[INFO] GameManager - Player joined: Player1
```

### Ver Logs de la App (Android)

1. Conecta tu móvil por USB
2. En Android Studio: **Logcat**
3. Filtra por: `com.bombparty`

---

## 🐛 Solución de Problemas

### Problema 1: GitHub Actions Falla

**Error: "Permission denied"**

**Solución:**
```bash
git update-index --chmod=+x gradlew
git commit -m "Fix gradlew permissions"
git push
```

---

### Problema 2: Railway No Inicia

**Síntoma:** Logs muestran "Port in use" o error similar

**Solución:**
1. Ve a Railway → Settings → Variables
2. Verifica que `PORT` no esté definido (Railway lo asigna automáticamente)
3. Si está, bórralo
4. Redeploy: Settings → Redeploy

---

### Problema 3: App No Se Conecta

**Síntoma:** "Connection error" en la app

**Verificar:**

1. **¿El servidor está corriendo?**
   - Ve a `https://tu-url.railway.app/health`
   - Debe decir "OK"

2. **¿La URL está correcta?**
   - Abre `Config.kt`
   - Verifica que `SERVER_URL` tenga tu URL de Railway
   - Debe empezar con `wss://` (no `ws://`)

3. **¿IS_PRODUCTION está en true?**
   - En `Config.kt`, verifica: `const val IS_PRODUCTION = true`

4. **¿Recompilaste después de cambiar Config.kt?**
   - Borra la app del móvil
   - Crea nuevo release (tag nuevo)
   - Descarga e instala el nuevo APK

---

### Problema 4: APK No Se Genera

**Síntoma:** GitHub Actions se ejecuta pero no aparece APK en Releases

**Solución:**
1. Ve a Actions → Click en el workflow fallido
2. Lee los errores
3. Común: Falta configurar secretos para firmar (pero no es necesario)
4. La APK se generará sin firmar (funcionará igual)

---

## 💰 Costos de Railway

### Plan Gratis (Hobby)
- **$5 USD de crédito gratis al mes**
- Suficiente para:
  - 500 horas de servidor activo
  - ~20 días 24/7
  - Perfecto para empezar

### Cuando se Acabe el Crédito Gratis
- Railway te avisará
- Puedes agregar tarjeta de crédito
- Solo pagas lo que usas (~$5-10/mes para uso normal)

### Cómo Reducir Costos
- Railway cobra por tiempo activo
- Si nadie está jugando, el servidor sigue corriendo
- Solución avanzada: Implementar "sleep mode" después de inactividad

---

## 🎯 Checklist Final

Antes de compartir tu app:

- [ ] Código en GitHub
- [ ] Servidor desplegado en Railway
- [ ] Config.kt actualizado con URL de Railway
- [ ] IS_PRODUCTION = true
- [ ] GitHub Actions configurado
- [ ] Release v1.0.0 creado
- [ ] APK descargada y probada en móvil
- [ ] Conexión al servidor funciona
- [ ] Pantallas básicas implementadas (Menu, Game)
- [ ] README actualizado con instrucciones

---

## 📱 Compartir tu App

### Opción 1: GitHub Releases (Lo que haremos ahora)

**Pros:**
- ✅ Gratis
- ✅ Control total
- ✅ Updates fáciles

**Cons:**
- ❌ Usuarios deben habilitar "Fuentes desconocidas"
- ❌ Sin updates automáticos
- ❌ Sin analytics

**Cómo compartir:**
```
¡Descarga BombParty!
https://github.com/TU_USUARIO/bombparty/releases/latest
```

---

### Opción 2: Google Play (Futuro)

Para publicar en Google Play Store:

1. **Crear cuenta de desarrollador**: $25 USD (una sola vez)
2. **Generar keystore** para firmar la app
3. **Crear bundle**: `./gradlew :app:bundleRelease`
4. **Subir a Play Console**
5. **Llenar formularios** (descripción, screenshots, etc.)
6. **Esperar revisión** (1-3 días)

**Pros:**
- ✅ Instalación fácil para usuarios
- ✅ Updates automáticos
- ✅ Más confianza (Google verifica)
- ✅ Analytics incluidos

**Cons:**
- ❌ Cuesta $25 USD
- ❌ Proceso de revisión
- ❌ Debe cumplir políticas de Google

---

## 🔐 Firmar la APK (Opcional, Recomendado)

Para firmar tu APK (quita el warning de "App no verificada"):

### 1. Generar Keystore

```bash
keytool -genkey -v -keystore bombparty-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias bombparty
```

Te pedirá:
- Contraseña del keystore (guárdala)
- Nombre, organización, etc.
- Contraseña del alias (puede ser la misma)

### 2. Configurar en app/build.gradle.kts

Agrega (antes de `android {`):

```kotlin
// Configuración de firma
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    // ... configuración existente

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] ?: "bombparty-release-key.jks")
            storePassword = keystoreProperties["storePassword"] as String?
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... resto de configuración
        }
    }
}
```

### 3. Crear keystore.properties

En la raíz del proyecto:

```properties
storeFile=bombparty-release-key.jks
storePassword=TU_CONTRASEÑA
keyAlias=bombparty
keyPassword=TU_CONTRASEÑA
```

**IMPORTANTE:** Agrega a `.gitignore`:
```
keystore.properties
*.jks
```

### 4. Configurar GitHub Secrets

Para que GitHub Actions firme automáticamente:

1. Convierte tu keystore a base64:
   ```bash
   base64 -i bombparty-release-key.jks | tr -d '\n'
   ```

2. Ve a GitHub → Repositorio → Settings → Secrets and variables → Actions

3. Agrega estos secretos:
   - `SIGNING_KEY`: (el base64 del keystore)
   - `ALIAS`: `bombparty`
   - `KEY_STORE_PASSWORD`: Tu contraseña
   - `KEY_PASSWORD`: Tu contraseña

---

## 🎉 ¡Felicidades!

Si llegaste hasta aquí:

✅ Tienes tu servidor corriendo 24/7 en Railway
✅ Tu código está en GitHub
✅ GitHub compila APKs automáticamente
✅ Puedes descargar e instalar en tu móvil
✅ Otros pueden descargar desde GitHub Releases

---

## 📚 Próximos Pasos

1. **Implementar pantallas faltantes:**
   - CreateRoomScreen
   - JoinRoomScreen
   - LobbyScreen

2. **Mejorar el juego:**
   - Más palabras en diccionarios
   - Animaciones
   - Sonidos
   - Estadísticas

3. **Monetización (opcional):**
   - Ads con AdMob
   - Versión premium
   - Donaciones

4. **Publicar en Play Store:**
   - Cuando tengas versión estable
   - Con buenos screenshots
   - Descripción atractiva

---

**¿Necesitas ayuda en algún paso? ¡Pregúntame!**
