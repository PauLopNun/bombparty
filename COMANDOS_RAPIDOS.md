# ⚡ Comandos Rápidos - Deploy Completo

## 📋 Checklist Previo

Antes de empezar, necesitas:
- [ ] Cuenta de GitHub (gratis)
- [ ] Cuenta de Railway (gratis)
- [ ] Android Studio abierto

---

## 🚀 Parte 1: Subir a GitHub (5 minutos)

### 1. Crear repositorio en GitHub
1. Ve a https://github.com/new
2. Nombre: `bombparty`
3. Public
4. NO marques README
5. Create repository

### 2. Comandos en tu terminal

Abre la terminal de Android Studio o PowerShell en la carpeta del proyecto:

```bash
# Navegar a la carpeta (si usas PowerShell)
cd C:\Users\paulo\Desktop\bombpartykotlin

# Inicializar git
git init

# Agregar todos los archivos
git add .

# Primer commit
git commit -m "Initial commit - BombParty"

# Conectar con GitHub (CAMBIA TU_USUARIO)
git remote add origin https://github.com/TU_USUARIO/bombparty.git

# Subir
git branch -M main
git push -u origin main
```

**Si pide credenciales:**
- Usuario: tu usuario de GitHub
- Password: Personal Access Token (no tu contraseña)

**Crear token:**
1. GitHub → Settings → Developer settings
2. Personal access tokens → Tokens (classic)
3. Generate new token
4. Marca: `repo`
5. Copia el token

✅ **Verifica:** Ve a tu repo en GitHub, deberías ver todos los archivos

---

## 🚀 Parte 2: Deploy en Railway (5 minutos)

### 1. Ir a Railway
https://railway.app

### 2. Login con GitHub

### 3. New Project → Deploy from GitHub repo

### 4. Selecciona tu repo: `TU_USUARIO/bombparty`

### 5. Deploy automático
Railway detectará que es Kotlin y empezará a compilar.

### 6. Generar dominio
1. Settings → Networking
2. Generate Domain
3. Copiar la URL (algo como: `bombparty-production-abc123.up.railway.app`)

### 7. Verificar
Abre en el navegador:
```
https://TU_URL.railway.app/health
```
Debe decir: **OK**

✅ **Servidor funcionando!**

---

## 🚀 Parte 3: Configurar la App (2 minutos)

### 1. Abrir Config.kt
```
app/src/main/java/com/bombparty/utils/Config.kt
```

### 2. Cambiar estas 2 líneas:

```kotlin
// Línea 15 - Cambiar a true
const val IS_PRODUCTION = true

// Línea 28 - Tu URL de Railway (SIN https://, CON wss://)
"wss://bombparty-production-abc123.up.railway.app/game"
```

**Ejemplo real:**
```kotlin
const val IS_PRODUCTION = true

val SERVER_URL = if (IS_PRODUCTION) {
    "wss://bombparty-production-abc123.up.railway.app/game"  // ← TU URL
} else {
    "ws://10.0.2.2:8080/game"
}
```

### 3. Guardar y hacer commit

```bash
git add .
git commit -m "Configure production server"
git push
```

---

## 🚀 Parte 4: Crear Release (2 minutos)

### Crear tag y push

```bash
git tag -a v1.0.0 -m "Primera versión"
git push origin v1.0.0
```

### Verificar en GitHub
1. Ve a tu repo
2. Pestaña **Actions** (verás el build en progreso)
3. Espera 5-10 minutos
4. Pestaña **Releases** → verás `v1.0.0`
5. Descarga `BombParty-v1.0.0.apk`

✅ **APK lista!**

---

## 🚀 Parte 5: Instalar en Móvil (2 minutos)

### Desde el móvil:

1. Abre el navegador
2. Ve a: `https://github.com/TU_USUARIO/bombparty/releases`
3. Descarga la APK
4. Habilita "Instalar desde fuentes desconocidas"
5. Instala
6. Abre la app

✅ **¡Funciona!**

---

## 🔄 Para Futuras Actualizaciones

Cada vez que hagas cambios:

```bash
# 1. Hacer cambios en el código
# 2. Commit y push
git add .
git commit -m "Descripción del cambio"
git push

# 3. Crear nuevo tag (incrementa versión)
git tag -a v1.0.1 -m "Versión 1.0.1"
git push origin v1.0.1

# 4. Esperar 5-10 min
# 5. Nueva APK disponible en Releases
```

---

## 📊 Verificar Todo Funciona

### Servidor (Railway)
```
https://tu-url.railway.app/health
→ Debe decir: OK
```

### Logs en Railway
```
Railway Dashboard → Tu proyecto → Deployments → Logs
→ Verás: "Starting server on 0.0.0.0:XXXX"
```

### App en Móvil
```
Abre la app → Debería conectarse al servidor
```

### Logs cuando un usuario se conecta
```
Railway logs mostrarán:
[INFO] WebSocket - Client connected
```

---

## 🐛 Si Algo Falla

### Railway no inicia:
```bash
# Ver logs en Railway Dashboard
# Común: Puerto incorrecto
# Solución: Variables → Borrar PORT si existe
```

### GitHub Actions falla:
```bash
# Permisos de gradlew
git update-index --chmod=+x gradlew
git commit -m "Fix permissions"
git push

# Crear tag de nuevo
git tag -a v1.0.1 -m "Fix"
git push origin v1.0.1
```

### App no se conecta:
```
1. Verifica Config.kt:
   - IS_PRODUCTION = true
   - URL correcta (empieza con wss://)
2. Recompila la app
3. Crea nuevo tag
4. Descarga nuevo APK
```

---

## 💰 Costos

- **GitHub:** Gratis
- **Railway:** $5 USD gratis/mes (suficiente para empezar)
- **Google Play (futuro):** $25 USD una sola vez

---

## ✅ Tiempo Total

- GitHub: 5 min
- Railway: 5 min
- Configurar app: 2 min
- Release: 2 min (+ 10 min de build)
- Instalar: 2 min

**Total: ~30 minutos** (incluyendo esperas)

---

## 🎯 URLs Importantes

Guarda estas URLs:

```
📦 Tu repositorio:
https://github.com/TU_USUARIO/bombparty

🚀 Railway Dashboard:
https://railway.app/dashboard

🌐 Tu servidor:
https://tu-url.railway.app

📱 Releases (para descargar APK):
https://github.com/TU_USUARIO/bombparty/releases
```

---

## 📱 Compartir con Amigos

Envíales este link:
```
https://github.com/TU_USUARIO/bombparty/releases/latest
```

Ellos:
1. Descargan la APK
2. Instalan
3. Juegan juntos (conectados al mismo servidor)

---

**¿Listo? ¡Empieza con la Parte 1!**
