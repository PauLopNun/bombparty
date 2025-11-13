@echo off
echo ========================================
echo    SERVIDOR BOMBPARTY
echo ========================================
echo.
echo Iniciando servidor en puerto 8080...
echo.
echo IMPORTANTE: NO cierres esta ventana mientras juegues
echo Para detener el servidor, presiona Ctrl+C
echo.
echo ========================================
echo.

gradlew.bat :server:run

echo.
echo ========================================
echo Servidor detenido.
echo Presiona cualquier tecla para cerrar...
pause >nul
