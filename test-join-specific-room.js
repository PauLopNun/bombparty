const WebSocket = require('ws');

const RENDER_URL = 'wss://bombparty-server.onrender.com/game';
const ROOM_CODE = 'O9YY7X';

console.log('🔌 Conectando a Render.com...');
const ws = new WebSocket(RENDER_URL);

ws.on('open', () => {
    console.log('✅ Conexión establecida');
    console.log(`🚪 Uniéndose a sala: ${ROOM_CODE}`);

    const joinMessage = {
        type: 'join_room',
        roomId: ROOM_CODE,
        playerName: 'Claude Bot',
        avatar: '🤖'
    };

    ws.send(JSON.stringify(joinMessage));
    console.log('📤 Mensaje enviado:', JSON.stringify(joinMessage, null, 2));
});

ws.on('message', (data) => {
    try {
        const message = JSON.parse(data.toString());
        console.log('\n📨 Mensaje recibido del servidor:');
        console.log(JSON.stringify(message, null, 2));

        // Si recibimos que el juego empezó, enviamos una palabra de ejemplo
        if (message.type === 'game_started') {
            console.log('\n🎮 ¡El juego ha comenzado!');
        }

        if (message.type === 'new_syllable') {
            console.log(`\n💣 Nueva sílaba: ${message.syllable}`);
            console.log(`⏱️ Tiempo: ${message.bombTime}s`);
        }
    } catch (error) {
        console.log('❌ Error parseando mensaje:', error.message);
        console.log('Raw data:', data.toString());
    }
});

ws.on('error', (error) => {
    console.error('❌ WebSocket error:', error.message);
});

ws.on('close', (code, reason) => {
    console.log(`\n🔌 Conexión cerrada. Code: ${code}, Reason: ${reason || 'No reason provided'}`);
});

// Mantener el script vivo
setInterval(() => {
    if (ws.readyState === WebSocket.OPEN) {
        // Mantener conexión activa
    }
}, 30000);

console.log('🎮 Bot esperando mensajes... (Presiona Ctrl+C para salir)');
