package com.learn.haptalk.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn.haptalk.R

/**
 * Splash/Landing screen with fade-in animation
 * Shows app logo and entry button
 */
@Composable
fun SplashScreen(
    onEnterChat: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    // Trigger animation on first composition
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "fade_in"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // App Logo/Title
            Text(
                text = "💬",
                fontSize = 80.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .graphicsLayer { this.alpha = alpha }
            )

            Text(
                text = "HapTalk",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .graphicsLayer { this.alpha = alpha }
            )

            Text(
                text = "Chat Ringan untuk Koneksi Lemah",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .graphicsLayer { this.alpha = alpha }
            )

            Button(
                onClick = onEnterChat,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .graphicsLayer { this.alpha = alpha },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Masuk ke Chat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Extension function for graphicsLayer alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

private fun Modifier.graphicsLayer(block: androidx.compose.ui.graphics.GraphicsLayerScope.() -> Unit): Modifier {
    return this.graphicsLayer(block = block)
}

