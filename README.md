# HapTalk - Chat Realtime Ringan untuk Koneksi Lemah

HapTalk adalah aplikasi chat Android native berbasis Kotlin yang dirancang untuk bekerja optimal pada koneksi internet tidak stabil (3G/EDGE). Aplikasi ini menggunakan WebSocket untuk komunikasi realtime dengan fokus pada efisiensi bandwidth dan dukungan offline.

## 🎯 Fitur Utama

- ✅ **Single Public Room** - Satu room chat untuk semua pengguna
- ✅ **No Login Required** - Menggunakan sessionId acak yang disimpan lokal
- ✅ **Realtime WebSocket** - Komunikasi cepat via WebSocket (Ktor client)
- ✅ **Offline Support** - Queue pesan otomatis saat offline
- ✅ **Auto-Reconnect** - Reconnect otomatis dengan exponential backoff
- ✅ **Optimistic UI** - Pesan tampil langsung sebelum terkirim
- ✅ **Local Storage** - Persistensi pesan dengan Room Database
- ✅ **Lightweight** - Protokol JSON ringkas untuk hemat bandwidth
- ✅ **Material Design 3** - UI modern dengan Jetpack Compose

## 📱 Tech Stack

### Android App
- **Kotlin** 2.0.21
- **Jetpack Compose** - Modern declarative UI
- **Ktor Client** 2.3.12 - WebSocket client dengan auto-reconnect
- **Room Database** 2.6.1 - Local message persistence
- **DataStore** - Preferences untuk sessionId
- **Coroutines** - Asynchronous programming
- **Gson** - JSON serialization
- **Material 3** - UI components

### Backend Server
- **Node.js** - Runtime
- **ws** library - WebSocket server
- Deploy-ready untuk Railway, Render, atau Vercel

## 🚀 Quick Start

### 1. Setup Server (Local Development)

```bash
cd server
npm install
npm start
```

Server akan berjalan di `ws://localhost:3000`

**Output yang diharapkan:**
```
🚀 HapTalk WebSocket Server
📡 Server running on ws://localhost:3000
📱 For Android Emulator use: ws://10.0.2.2:3000
```

### 2. Build & Run Android App

1. Buka project di Android Studio
2. Sync Gradle dependencies (tunggu hingga selesai)
3. Jalankan di emulator atau device

**Untuk Emulator:**
- Server URL sudah di-set ke `ws://10.0.2.2:3000` (default)

**Untuk Device Fisik:**
- Edit `MainActivity.kt` line 39:
  ```kotlin
  val wsUrl = "ws://<YOUR_COMPUTER_IP>:3000"
  ```
- Pastikan device dan komputer di network yang sama

### 3. Testing Multi-Device

Untuk menguji chat antar device:

1. **Opsi 1: Multiple Emulators**
   - Jalankan 2+ emulator Android
   - Install APK di semua emulator
   - Kirim pesan dari satu, terima di lainnya

2. **Opsi 2: Emulator + Device Fisik**
   - Deploy server ke platform cloud (lihat bagian Deployment)
   - Update `wsUrl` di MainActivity dengan URL server public
   - Install di emulator dan device

## 📦 Build APK

### Debug APK
```bash
# Di terminal atau gunakan Android Studio
./gradlew assembleDebug
```

APK akan tersedia di:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (Unsigned)
```bash
./gradlew assembleRelease
```

APK akan tersedia di:
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

### Distribusi APK

1. **Upload ke Cloud Storage**
   - Google Drive / Dropbox / GitHub Releases
   - Dapatkan shareable link

2. **Generate QR Code**
   - Gunakan https://qr-code-generator.com
   - Input link download APK
   - Download QR code image

3. **Instalasi di Device**
   - Scan QR code
   - Download APK
   - Enable "Install from Unknown Sources"
   - Install APK

## 🌐 Deployment Server

### Deploy ke Railway

1. Install Railway CLI atau gunakan dashboard web
2. Login: `railway login`
3. Di folder `server/`:
   ```bash
   railway init
   railway up
   ```
4. Railway akan provide URL: `wss://your-app.railway.app`
5. Update `wsUrl` di MainActivity dengan URL Railway

### Deploy ke Render

1. Push code ke GitHub
2. Buat Web Service baru di https://render.com
3. Connect repository
4. Settings:
   - **Build Command**: `cd server && npm install`
   - **Start Command**: `cd server && npm start`
5. Deploy dan dapatkan URL

### Deploy ke Vercel (dengan modifikasi)

Vercel memerlukan HTTP endpoint, tambahkan file `server/index.js`:
```javascript
const express = require('express');
const expressWs = require('express-ws');
// ... setup WebSocket dengan express-ws
```

## 📱 Struktur Project

```
HapTalk/
├── app/                          # Android app
│   ├── src/main/java/com/learn/haptalk/
│   │   ├── MainActivity.kt       # Entry point & Compose setup
│   │   ├── data/
│   │   │   ├── ChatDatabase.kt   # Room database
│   │   │   ├── MessageEntity.kt  # Message data model
│   │   │   ├── MessageDao.kt     # Database operations
│   │   │   ├── Converters.kt     # Type converters
│   │   │   └── PreferencesManager.kt  # SessionId storage
│   │   ├── network/
│   │   │   ├── WebSocketManager.kt    # WebSocket client
│   │   │   └── ChatMessage.kt         # Network message format
│   │   ├── repo/
│   │   │   └── ChatRepository.kt      # Data layer abstraction
│   │   ├── vm/
│   │   │   ├── ChatViewModel.kt       # Business logic
│   │   │   └── ChatViewModelFactory.kt
│   │   └── ui/
│   │       ├── SplashScreen.kt        # Landing screen
│   │       ├── ChatScreen.kt          # Main chat UI
│   │       └── theme/                 # Compose theme
│   └── build.gradle.kts
├── server/
│   ├── server.js                 # WebSocket server
│   └── package.json
├── gradle/
│   └── libs.versions.toml        # Version catalog
└── README.md
```

## 🔧 Configuration

### WebSocket URL

Edit di `MainActivity.kt`:
```kotlin
val wsUrl = "ws://10.0.2.2:3000"  // Local emulator
// val wsUrl = "ws://192.168.1.100:3000"  // Local network
// val wsUrl = "wss://your-server.railway.app"  // Production
```

### Message Format

Protokol JSON ringkas untuk hemat bandwidth:
```json
{
  "u": "u4f29a1b",    // senderId (8 chars)
  "m": "Hello!",       // message text
  "t": 1731140000000   // timestamp (ms)
}
```

## ✅ Acceptance Testing Checklist

### Manual Testing

- [ ] **Splash Screen**
  - [ ] Animasi fade-in logo & title muncul
  - [ ] Tombol "Masuk ke Chat" berfungsi
  - [ ] SessionId dibuat saat pertama masuk

- [ ] **Chat Screen**
  - [ ] Pesan bisa diketik di input field
  - [ ] Tombol send mengirim pesan
  - [ ] Pesan muncul langsung (optimistic UI)
  - [ ] Status koneksi tampil (Terhubung/Menghubungkan/Offline)

- [ ] **Realtime Communication**
  - [ ] Pesan dikirim ke server
  - [ ] Pesan diterima dari client lain
  - [ ] Pesan sendiri align kanan (hijau)
  - [ ] Pesan orang lain align kiri (putih)
  - [ ] Timestamp ditampilkan

- [ ] **Offline Mode**
  - [ ] Matikan WiFi/data
  - [ ] Kirim pesan → status PENDING
  - [ ] Hidupkan koneksi
  - [ ] Pesan otomatis terkirim
  - [ ] Status berubah jadi SENT

- [ ] **Auto-Reconnect**
  - [ ] Server di-stop
  - [ ] Status berubah "Offline"
  - [ ] Server di-start lagi
  - [ ] App auto-reconnect
  - [ ] Status berubah "Terhubung"

- [ ] **Multi-Device**
  - [ ] 2 device/emulator terhubung
  - [ ] Pesan dari device A muncul di device B
  - [ ] Vice versa

## 🔍 Troubleshooting

### WebSocket Connection Failed

**Emulator → Local Server:**
```
Error: Connection refused
```
**Solution:** Pastikan menggunakan `10.0.2.2` bukan `localhost`

**Device Fisik → Local Server:**
```
Error: Failed to connect
```
**Solution:** 
1. Pastikan device & komputer di network sama
2. Gunakan IP lokal komputer (cek dengan `ipconfig`)
3. Disable firewall untuk port 3000

### Build Errors

**Room Compiler Error:**
```
ksp plugin not found
```
**Solution:** Sync Gradle, clean & rebuild:
```bash
./gradlew clean build
```

**Ktor Engine Error:**
```
No suitable engine found
```
**Solution:** Pastikan `ktor-client-cio` ada di dependencies

## 📊 Network Optimization

HapTalk dioptimalkan untuk koneksi lemah:

1. **Compact JSON** - Field 1 huruf (`u`, `m`, `t`)
2. **WebSocket** - Persistent connection, no HTTP overhead
3. **Exponential Backoff** - 2s → 4s → 8s → ... → 30s max
4. **Offline Queue** - Pesan disimpan dan dikirim saat online
5. **Ping Interval** - Keep-alive setiap 30 detik
6. **Local First** - Optimistic UI, data di Room

### Testing di Koneksi Lambat

**Android Emulator:**
1. Settings → Extended Controls
2. Cellular → Network type
3. Pilih "EDGE" atau "3G"

**Chrome DevTools (untuk web testing):**
1. F12 → Network tab
2. Throttling → Slow 3G

## 🛡️ Security Notes

⚠️ **Prototype Version - Tidak untuk Production**

- Tidak ada enkripsi E2E
- Server tidak menyimpan pesan (relay-only)
- SessionId tidak ter-authenticate
- Cocok untuk: testing, demo, internal use
- **Jangan** kirim data sensitif

**Untuk Production:**
- Implementasi TLS/SSL (`wss://`)
- Tambahkan autentikasi user
- Rate limiting di server
- Input validation & sanitization
- E2E encryption (Signal Protocol/OMEMO)

## 📝 License

MIT License - Free untuk digunakan dan dimodifikasi

## 🤝 Contributing

Silakan fork, improve, dan submit PR!

## 📧 Support

Jika ada masalah, cek:
1. Troubleshooting section di atas
2. Android Studio Logcat
3. Server terminal output

---

**Built with ❤️ for low-bandwidth environments**

🌐 Works on 3G/EDGE | 📱 Android 7.0+ | 🚀 Fast & Lightweight

