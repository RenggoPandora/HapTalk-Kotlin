# Railway Deployment Guide

## Quick Deploy to Railway

### Prerequisites
- GitHub account
- Railway account (https://railway.app)

### Steps

1. **Push code to GitHub**
   ```bash
   git init
   git add .
   git commit -m "Initial commit - HapTalk server"
   git branch -M main
   git remote add origin <your-repo-url>
   git push -u origin main
   ```

2. **Deploy to Railway**
   - Go to https://railway.app
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Choose your HapTalk repository
   - Railway auto-detects Node.js
   - Click "Deploy"

3. **Configure Environment**
   - Railway will automatically run `npm install` and `npm start`
   - Note the generated URL (e.g., `haptalk-production.up.railway.app`)

4. **Get WebSocket URL**
   - Railway provides: `https://your-app.railway.app`
   - WebSocket URL: `wss://your-app.railway.app` (note: wss not ws)

5. **Update Android App**
   - Edit `MainActivity.kt` line 39:
   ```kotlin
   val wsUrl = "wss://your-app.railway.app"
   ```
   - Rebuild and test

### Alternative: Railway CLI

```bash
# Install Railway CLI
npm i -g @railway/cli

# Login
railway login

# Initialize project
cd server
railway init

# Deploy
railway up

# Get domain
railway domain
```

## Deploy to Render

1. Go to https://render.com
2. New → Web Service
3. Connect GitHub repository
4. Settings:
   - **Name**: haptalk-server
   - **Root Directory**: server
   - **Environment**: Node
   - **Build Command**: `npm install`
   - **Start Command**: `npm start`
5. Create Web Service
6. Copy the URL provided
7. Update Android app with `wss://your-app.onrender.com`

## Deploy to Fly.io

```bash
# Install flyctl
curl -L https://fly.io/install.sh | sh

# Login
fly auth login

# Launch app (in server directory)
cd server
fly launch

# Deploy
fly deploy

# Get URL
fly info
```

## Testing Production Server

```bash
# Test WebSocket connection
wscat -c wss://your-server-url

# Send test message
{"u":"test","m":"Hello","t":1234567890}

# Should echo back
```

## Monitoring

### Railway
- Dashboard shows logs in real-time
- Metrics: CPU, Memory, Network
- Auto-restarts on crash

### Render
- Logs tab shows console output
- Free tier: auto-sleeps after inactivity
- Wakes on first request (may be slow)

## Cost Estimate

- **Railway**: $5/month for hobby plan (500 hours free trial)
- **Render**: Free tier available (with sleep)
- **Fly.io**: Free tier: 3 shared VMs

## Troubleshooting

**WebSocket upgrade failed:**
- Ensure using `wss://` not `ws://` for HTTPS
- Check firewall/security groups
- Verify server logs

**Connection timeout:**
- Railway/Render free tier may sleep
- First connection takes longer
- Consider paid tier for always-on

**CORS issues (if adding HTTP endpoints):**
```javascript
// Add to server.js if needed
const express = require('express');
const cors = require('cors');
app.use(cors());
```

