const WebSocket = require('ws');

const ROOM_CODE = 'RITQJR';
const PLAYER_NAME = 'TestBot';

console.log(`🤖 Joining room: ${ROOM_CODE} as ${PLAYER_NAME}`);
const ws = new WebSocket('wss://bombparty-zzgp.onrender.com/game');

ws.on('open', () => {
    console.log('✅ WebSocket CONNECTED!');
    console.log(`📤 Sending join_room message for room ${ROOM_CODE}...`);
    ws.send(JSON.stringify({
        type: 'join_room',
        roomId: ROOM_CODE,
        playerName: PLAYER_NAME
    }));
});

let messageCount = 0;
ws.on('message', (data) => {
    messageCount++;
    const msg = JSON.parse(data.toString());
    console.log(`\n📩 MESSAGE #${messageCount} - Type: ${msg.type}`);
    console.log(JSON.stringify(msg, null, 2));

    // Respond to game events
    if (msg.type === 'GameStarted') {
        console.log('🎮 GAME STARTED!');
    }

    if (msg.type === 'NewSyllable') {
        console.log(`💣 New syllable: ${msg.syllable}`);
    }

    if (msg.type === 'GameStateUpdate') {
        const currentPlayer = msg.gameState?.currentPlayerIndex;
        console.log(`🎯 Current turn: Player ${currentPlayer}`);
    }
});

ws.on('error', (err) => {
    console.log('❌ ERROR:', err.message);
});

ws.on('close', () => {
    console.log('🔌 Connection closed');
    console.log(`📊 Total messages received: ${messageCount}`);
});

// Keep alive for 5 minutes to test the game
setTimeout(() => {
    console.log('⏰ Test finished after 5 minutes');
    ws.close();
    process.exit(0);
}, 300000);
