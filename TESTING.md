# Manual Testing Guide

## Test 1: Splash Screen & Session Creation
1. Launch app
2. Verify fade-in animation appears
3. Click "Masuk ke Chat" button
4. Should navigate to chat screen
5. Check app data: sessionId should be created

## Test 2: Basic Message Sending
1. Start WebSocket server: `cd server && npm start`
2. Type a message in input field
3. Click send button
4. Message should appear immediately (optimistic UI)
5. Message should have green background (own message)
6. Status icon should show ✓ (sent)

## Test 3: Multi-Device Communication
1. Start server
2. Launch app on emulator 1
3. Launch app on emulator 2 (or physical device)
4. Send message from emulator 1
5. Verify message appears on emulator 2
6. Message should have white background on emulator 2
7. Sender ID should be visible

## Test 4: Offline Queue
1. Disconnect WiFi/mobile data
2. Connection status should show "Offline"
3. Type and send a message
4. Message appears with ⏱ (pending) status
5. Reconnect internet
6. App auto-reconnects
7. Pending message automatically sent
8. Status changes to ✓

## Test 5: Auto-Reconnect
1. Start app with server running
2. Status: "Terhubung"
3. Stop server (Ctrl+C)
4. Status changes to "Offline" then "Menghubungkan..."
5. Restart server
6. App reconnects automatically within 30 seconds
7. Status: "Terhubung"

## Test 6: Message Persistence
1. Send several messages
2. Close app completely
3. Reopen app
4. All previous messages should be visible
5. Scroll works correctly

## Test 7: Network Throttling
1. Enable network throttling (emulator settings)
2. Set to "3G" or "EDGE"
3. Send messages
4. Verify messages still work (may be slower)
5. Check auto-reconnect behavior

## Expected Results Summary
- ✅ Messages appear instantly (optimistic UI)
- ✅ Own messages: green, right-aligned
- ✅ Others' messages: white, left-aligned
- ✅ Status icons visible and accurate
- ✅ Offline messages queued and sent on reconnect
- ✅ Connection auto-recovers with exponential backoff
- ✅ Data persists across app restarts

