# ✅ Checklist de Deploy - BombParty

Usa este checklist para asegurarte de completar todos los pasos.

---

## 📋 PREPARACIÓN

- [ ] Tengo cuenta de GitHub
- [ ] Tengo cuenta de Railway
- [ ] Android Studio está abierto
- [ ] El proyecto se sincronizó sin errores
- [ ] Probé el servidor localmente y funciona

---

## 🌐 GITHUB

### Crear Repositorio
- [ ] Fui a https://github.com/new
- [ ] Nombre: `bombparty` (o similar)
- [ ] Tipo: Public
- [ ] NO marqué "Add README"
- [ ] Click en "Create repository"
- [ ] Copié la URL del repositorio

### Subir Código
- [ ] Abrí terminal en el proyecto
- [ ] Ejecuté: `git init`
- [ ] Ejecuté: `git add .`
- [ ] Ejecuté: `git commit -m "Initial commit"`
- [ ] Ejecuté: `git remote add origin [MI_URL]`
- [ ] Ejecuté: `git branch -M main`
- [ ] Ejecuté: `git push -u origin main`
- [ ] Verifiqué en GitHub que están todos los archivos

---

## 🚂 RAILWAY

### Crear Proyecto
- [ ] Fui a https://railway.app
- [ ] Login con GitHub
- [ ] Click en "New Project"
- [ ] Seleccioné "Deploy from GitHub repo"
- [ ] Autoricé Railway (si es primera vez)
- [ ] Seleccioné mi repositorio
- [ ] Click en "Deploy Now"

### Configurar
- [ ] Esperé a que termine el primer deploy
- [ ] Vi logs: "Starting server on 0.0.0.0:XXXX"
- [ ] Fui a Settings → Networking
- [ ] Click en "Generate Domain"
- [ ] Copié mi URL: `__________.railway.app`

### Verificar
- [ ] Abrí navegador
- [ ] Fui a: `https://MI_URL.railway.app/health`
- [ ] Vi: "OK"

**Si no funciona:**
- [ ] Revisé logs en Railway
- [ ] Busqué errores
- [ ] Pedí ayuda

---

## ⚙️ CONFIGURAR APP

### Actualizar Config.kt
- [ ] Abrí: `app/src/main/java/com/bombparty/utils/Config.kt`
- [ ] Cambié: `const val IS_PRODUCTION = true`
- [ ] Cambié: `"wss://MI_URL.railway.app/game"`
- [ ] Guardé el archivo
- [ ] Ejecuté: `git add .`
- [ ] Ejecuté: `git commit -m "Configure production server"`
- [ ] Ejecuté: `git push`

---

## 🏷️ CREAR RELEASE

### Primera Versión
- [ ] Ejecuté: `git tag -a v1.0.0 -m "Primera versión"`
- [ ] Ejecuté: `git push origin v1.0.0`
- [ ] Fui a GitHub → Actions
- [ ] Vi el workflow ejecutándose
- [ ] Esperé 5-10 minutos
- [ ] Fui a GitHub → Releases
- [ ] Vi mi release: v1.0.0
- [ ] Vi el archivo: `BombParty-v1.0.0.apk`

**Si GitHub Actions falló:**
- [ ] Revisé los logs del error
- [ ] Ejecuté: `git update-index --chmod=+x gradlew`
- [ ] Ejecuté: `git commit -m "Fix gradlew permissions"`
- [ ] Ejecuté: `git push`
- [ ] Creé el tag de nuevo con v1.0.1

---

## 📱 PROBAR EN MÓVIL

### Descargar APK
- [ ] Desde el móvil, abrí navegador
- [ ] Fui a: `github.com/MI_USUARIO/bombparty/releases`
- [ ] Click en `BombParty-v1.0.0.apk`
- [ ] Esperé a que descargue

### Instalar
- [ ] Abrí la APK descargada
- [ ] Android me advirtió sobre fuente desconocida
- [ ] Click en "Configuración"
- [ ] Habilité "Permitir de esta fuente"
- [ ] Volví atrás
- [ ] Click en "Instalar"
- [ ] Esperé...
- [ ] Click en "Abrir"

### Probar
- [ ] La app se abrió
- [ ] Vi el menú principal
- [ ] La app se conectó al servidor (sin errores)
- [ ] Revisé logs de Railway: vi "Client connected"

---

## 🎉 ÉXITO

Si marcaste TODO:

✅ Tu código está en GitHub
✅ Tu servidor está en Railway
✅ GitHub compila APKs automáticamente
✅ La app funciona en tu móvil
✅ Se conecta al servidor en la nube
✅ Otros pueden descargar desde GitHub

---

## 📊 POST-DEPLOY

### Compartir
- [ ] Copié el link de releases
- [ ] Lo compartí con amigos
- [ ] Les expliqué cómo instalar

### Monitorear
- [ ] Revisé logs de Railway regularmente
- [ ] Verifiqué que el servidor sigue activo
- [ ] Revisé uso de recursos en Railway

### Próximos pasos
- [ ] Implementar CreateRoomScreen
- [ ] Implementar JoinRoomScreen
- [ ] Implementar LobbyScreen
- [ ] Agregar más palabras a diccionarios
- [ ] Mejorar UI/UX
- [ ] Agregar animaciones
- [ ] Agregar sonidos

---

## 🆘 SI ALGO FALLA

### El servidor no inicia en Railway
- [ ] Revisé logs en Railway Dashboard
- [ ] Verifiqué que Application.kt usa PORT del environment
- [ ] Intenté redeploy (Settings → Redeploy)
- [ ] Pedí ayuda con el error específico

### GitHub Actions falla
- [ ] Vi el error en Actions → [nombre del workflow]
- [ ] Error de permisos → arreglé gradlew
- [ ] Error de build → revisé build.gradle.kts
- [ ] Pedí ayuda con el log del error

### App no se conecta
- [ ] Verifiqué que el servidor responde: `/health`
- [ ] Revisé Config.kt: IS_PRODUCTION = true
- [ ] Revisé Config.kt: URL correcta con wss://
- [ ] Recompilé la app
- [ ] Creé nuevo release
- [ ] Instalé nuevo APK

### APK no se genera
- [ ] Revisé que el tag se creó: `git tag`
- [ ] Revisé Actions en GitHub
- [ ] Esperé 10-15 minutos
- [ ] Intenté crear nuevo tag

---

## 💰 COSTOS ACTUALES

Después del deploy:

- **GitHub**: $0 (gratis)
- **Railway**: $0 (hasta agotar $5 gratis)
- **Total**: $0/mes

**Cuando se agote el crédito gratis:**
- Railway: ~$5-10/mes (según uso)

---

## 📈 MÉTRICAS

Llena esto después del deploy:

```
Fecha de deploy: ___/___/2025
Versión: v1.0.0
URL del servidor: ____________________
Link de releases: ____________________
Número de usuarios: ___
```

---

## 🎯 OBJETIVOS

**Corto plazo (1 semana):**
- [ ] 5 personas probaron la app
- [ ] Recolecté feedback
- [ ] Arreglé bugs críticos

**Medio plazo (1 mes):**
- [ ] Todas las pantallas implementadas
- [ ] 50+ usuarios
- [ ] Diccionarios completos

**Largo plazo (3 meses):**
- [ ] 500+ usuarios
- [ ] Publicado en Google Play
- [ ] Monetización implementada

---

**¡Guarda este archivo y ve marcando tu progreso!**

---

## 🔗 Links Rápidos

```
📦 Repositorio: https://github.com/______/bombparty
🚀 Railway: https://railway.app/project/_______
🌐 Servidor: https://________.railway.app
📱 Releases: https://github.com/______/bombparty/releases
📊 Actions: https://github.com/______/bombparty/actions
```

(Llena tus URLs arriba ↑)
