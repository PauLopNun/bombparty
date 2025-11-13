const WebSocket = require('ws');
console.log('🧪 Testing WebSocket on Render.com...');
const ws = new WebSocket('wss://bombparty-zzgp.onrender.com/game');

ws.on('open', () => {
    console.log('✅ WebSocket CONNECTED!');
    console.log('📤 Sending create_room message...');
    ws.send(JSON.stringify({
        type: 'create_room',
        playerName: 'TestPlayer',
        config: {}
    }));
});

ws.on('message', (data) => {
    console.log('📩 RECEIVED:', data.toString());
    const msg = JSON.parse(data.toString());
    console.log('📊 Message type:', msg.type);
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
    console.log('⏰ TIMEOUT');
    ws.close();
    process.exit(1);
}, 15000);
