const WebSocket = require('ws');

const ROOM_CODE = 'TESTROOM';
const PLAYER_NAME = 'KeepAliveBot';

console.log(`🤖 Testing keepalive...`);
const ws = new WebSocket('wss://bombparty-zzgp.onrender.com/game');

ws.on('open', () => {
    console.log('✅ WebSocket CONNECTED!');

    // Create a room first
    console.log('📤 Creating room...');
    ws.send(JSON.stringify({
        type: 'create_room',
        playerName: PLAYER_NAME,
        config: {}
    }));

    // Send ping frames every 3 seconds to keep connection alive
    const pingInterval = setInterval(() => {
        if (ws.readyState === WebSocket.OPEN) {
            console.log('💓 Sending ping...');
            ws.ping();
        }
    }, 3000);

    // Clean up on close
    ws.on('close', () => {
        clearInterval(pingInterval);
    });
});

let messageCount = 0;
ws.on('message', (data) => {
    messageCount++;
    const msg = JSON.parse(data.toString());
    console.log(`\n📩 MESSAGE #${messageCount} - Type: ${msg.type}`);

    if (msg.type === 'RoomCreated') {
        console.log(`✅ Room created: ${msg.room.id}`);
        console.log('⏰ Waiting 60 seconds to test if connection stays alive...');
    }
});

ws.on('pong', () => {
    console.log('💚 Received pong from server');
});

ws.on('error', (err) => {
    console.log('❌ ERROR:', err.message);
});

ws.on('close', () => {
    console.log('🔌 Connection closed');
    console.log(`📊 Total messages received: ${messageCount}`);
});

// Keep alive for 2 minutes to test
setTimeout(() => {
    console.log('⏰ Test finished after 2 minutes');
    ws.close();
    process.exit(0);
}, 120000);
