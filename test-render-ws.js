const WebSocket = require('ws');
console.log('🧪 Testing WebSocket on Render.com...');
const ws = new WebSocket('wss://bombparty-zzgp.onrender.com/game');

ws.on('open', () => {
    console.log('✅ WebSocket CONNECTED!');
    console.log('⏱️  Waiting 1 second to check for welcome message...');
    setTimeout(() => {
        console.log('📤 Sending create_room message...');
        ws.send(JSON.stringify({
            type: 'create_room',
            playerName: 'TestPlayer',
            config: {}
        }));
    }, 1000);
});

let messageCount = 0;
ws.on('message', (data) => {
    messageCount++;
    console.log(`📩 RECEIVED MESSAGE #${messageCount}:`, data.toString());
    const msg = JSON.parse(data.toString());
    console.log('📊 Message type:', msg.type);

    if (msg.type === 'RoomCreated') {
        console.log('✅ SUCCESS! Room created:', msg.room.id);
        ws.close();
        process.exit(0);
    }
});

ws.on('error', (err) => {
    console.log('❌ ERROR:', err.message);
    process.exit(1);
});

ws.on('close', () => {
    console.log('🔌 Connection closed');
    if (messageCount === 0) {
        console.log('⚠️  No messages received before close');
    }
});

setTimeout(() => {
    console.log('⏰ TIMEOUT');
    console.log(`📊 Total messages received: ${messageCount}`);
    ws.close();
    process.exit(1);
}, 15000);
