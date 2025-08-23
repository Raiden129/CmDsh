package com.securecam.dashboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.securecam.dashboard.data.Camera
import com.securecam.dashboard.ui.components.AdaptiveVlcPlayer
import com.securecam.dashboard.ui.components.isAndroidTv

@Composable
fun PanelScreen(cameras: List<Camera>) {
    val context = LocalContext.current
    val isAndroidTv = remember { context.isAndroidTv() }
    
    if (cameras.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No cameras configured", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    // Limit cameras for Android TV to reduce GPU load on Mali G31 MP2
    val maxCameras = if (isAndroidTv) 4 else 6  // Reduced from 6 to 4 for Android TV
    val displayCameras = cameras.take(maxCameras)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

    ) {
        when (displayCameras.size) {
            1 -> SingleCameraLayout(displayCameras[0])
            2 -> TwoCameraLayout(displayCameras)
            3 -> ThreeCameraLayout(displayCameras)
            4 -> FourCameraLayout(displayCameras)
            5 -> if (!isAndroidTv) FiveCameraLayout(displayCameras) else FourCameraLayout(displayCameras.take(4))
            6 -> if (!isAndroidTv) SixCameraLayout(displayCameras) else FourCameraLayout(displayCameras.take(4))
        }
    }
}

@Composable
private fun CameraPanelTile(camera: Camera, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isAndroidTv = remember { context.isAndroidTv() }
    
    Box(modifier = modifier) {
        Box(Modifier.fillMaxSize().clip(MaterialTheme.shapes.large)) {
            AdaptiveVlcPlayer(
                url = camera.rtspUrl,
                modifier = Modifier.fillMaxSize(),
                // Adaptive caching based on device type and concurrent streams
                networkCachingMs = if (isAndroidTv) 1000 else 500
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0x80000000))
                    )
                )
        )
        Text(
            text = camera.name,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        )
    }
}

@Composable
private fun SingleCameraLayout(camera: Camera) {
    CameraPanelTile(camera = camera, modifier = Modifier.fillMaxSize())
}

@Composable
private fun TwoCameraLayout(cameras: List<Camera>) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(0.5.dp)
    ) {
        cameras.forEach { camera ->
            CameraPanelTile(camera = camera, modifier = Modifier.weight(1f).fillMaxHeight())
        }
    }
}

@Composable
private fun ThreeCameraLayout(cameras: List<Camera>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.5.dp)
    ) {
        CameraPanelTile(camera = cameras[0], modifier = Modifier.weight(1f).fillMaxWidth())
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.5.dp)
        ) {
            cameras.drop(1).forEach { camera ->
                CameraPanelTile(camera = camera, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun FourCameraLayout(cameras: List<Camera>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.5.dp)
    ) {
        // Top row - 2 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.5.dp)
        ) {
            cameras.take(2).forEach { camera ->
                CameraPanelTile(camera = camera, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
        // Bottom row - 2 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.5.dp)
        ) {
            cameras.drop(2).take(2).forEach { camera ->
                CameraPanelTile(camera = camera, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun FiveCameraLayout(cameras: List<Camera>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.5.dp)
    ) {
        // Top row - 2 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.5.dp)
        ) {
            cameras.take(2).forEach { camera ->
                CameraPanelTile(camera = camera, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
        // Bottom row - 3 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.5.dp)
        ) {
            cameras.drop(2).take(3).forEach { camera ->
                CameraPanelTile(camera = camera, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun SixCameraLayout(cameras: List<Camera>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.5.dp)
    ) {
        // Top row - 3 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.5.dp)
        ) {
            cameras.take(3).forEach { camera ->
                CameraPanelTile(camera = camera, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
        // Bottom row - 3 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.5.dp)
        ) {
            cameras.drop(3).take(3).forEach { camera ->
                CameraPanelTile(camera = camera, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}
