const WebSocket = require('ws');
console.log('🧪 Testing WebSocket connection to Railway...');
const ws = new WebSocket('wss://97b87797-ba85-4845-a26d-11759c5ea25f.railway.app/game');

ws.on('open', () => {
    console.log('✅ WebSocket CONNECTED!');
    console.log('📤 Sending test message...');
    ws.send(JSON.stringify({
        type: 'create_room',
        playerName: 'TestPlayer',
        config: {}
    }));
});

ws.on('message', (data) => {
    console.log('📩 RECEIVED:', data.toString());
    ws.close();
    process.exit(0);
});

ws.on('error', (err) => {
    console.log('❌ ERROR:', err.message);
    process.exit(1);
});

ws.on('close', () => {
    console.log('🔌 Connection closed');
});

setTimeout(() => {
    console.log('⏰ TIMEOUT - No response after 10 seconds');
    ws.close();
    process.exit(1);
}, 10000);
