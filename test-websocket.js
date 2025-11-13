const WebSocket = require('ws');

console.log('='.repeat(50));
console.log('Testing WebSocket connection to Railway');
console.log('='.repeat(50));

const ws = new WebSocket('wss://97b87797-ba85-4845-a26d-11759c5ea25f.railway.app/game');

ws.on('open', function open() {
    console.log('✅ WebSocket connected successfully!');
    console.log('📤 Sending create_room message...');

    const message = {
        type: 'create_room',
        playerName: 'TestPlayer',
        config: {
            language: 'SPANISH',
            syllableDifficulty: 'BEGINNER',
            minTurnDuration: 8,
            maxSyllableLifespan: 2,
            initialLives: 2,
            maxLives: 3,
            maxPlayers: 8,
            bonusAlphabet: {}
        }
    };

    ws.send(JSON.stringify(message));
});

ws.on('message', function message(data) {
    console.log('📩 Received message:', data.toString());
    const parsed = JSON.parse(data.toString());
    console.log('📊 Parsed data:', JSON.stringify(parsed, null, 2));

    if (parsed.type === 'RoomCreated') {
        console.log('✅ SUCCESS! Room created:', parsed.room.id);
        ws.close();
    }
});

ws.on('error', function error(err) {
    console.log('❌ WebSocket error:', err.message);
});

ws.on('close', function close() {
    console.log('🔌 WebSocket connection closed');
});

// Timeout after 30 seconds
setTimeout(() => {
    console.log('⏰ Timeout - closing connection');
    ws.close();
    process.exit(1);
}, 30000);
