const WebSocket = require('ws');

// Create WebSocket server on port 3000
const wss = new WebSocket.Server({ port: 3000 });

console.log('🚀 HapTalk WebSocket Server');
console.log('📡 Server running on ws://localhost:3000');
console.log('📱 For Android Emulator use: ws://10.0.2.2:3000');
console.log('---');

// Track connected clients
let clientCount = 0;

wss.on('connection', (ws) => {
    clientCount++;
    const clientId = clientCount;
    console.log(`✅ Client #${clientId} connected (Total: ${wss.clients.size})`);

    // Handle incoming messages
    ws.on('message', (data) => {
        const message = data.toString();
        console.log(`📨 Received from #${clientId}: ${message}`);

        // Broadcast to all connected clients (including sender for echo confirmation)
        let broadcastCount = 0;
        wss.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(message);
                broadcastCount++;
            }
        });

        console.log(`📤 Broadcasted to ${broadcastCount} clients`);
    });

    // Handle disconnection
    ws.on('close', () => {
        console.log(`❌ Client #${clientId} disconnected (Total: ${wss.clients.size})`);
    });

    // Handle errors
    ws.on('error', (error) => {
        console.error(`⚠️ Error on client #${clientId}:`, error.message);
    });

    // Send welcome message (optional)
    const welcome = JSON.stringify({
        u: 'server',
        m: 'Welcome to HapTalk!',
        t: Date.now()
    });
    ws.send(welcome);
});

// Handle server errors
wss.on('error', (error) => {
    console.error('❌ Server error:', error);
});

// Graceful shutdown
process.on('SIGINT', () => {
    console.log('\n🛑 Shutting down server...');
    wss.close(() => {
        console.log('👋 Server closed');
        process.exit(0);
    });
});

