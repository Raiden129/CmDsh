package com.securecam.dashboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.securecam.dashboard.data.Camera
import com.securecam.dashboard.ui.components.VlcPlayer

@Composable
fun PanelScreen(cameras: List<Camera>) {
    if (cameras.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No cameras configured", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    // Take only up to 6 cameras
    val displayCameras = cameras.take(6)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(4.dp)
    ) {
        when (displayCameras.size) {
            1 -> SingleCameraLayout(displayCameras[0])
            2 -> TwoCameraLayout(displayCameras)
            3 -> ThreeCameraLayout(displayCameras)
            4 -> FourCameraLayout(displayCameras)
            5 -> FiveCameraLayout(displayCameras)
            6 -> SixCameraLayout(displayCameras)
        }
    }
}

@Composable
private fun SingleCameraLayout(camera: Camera) {
    Box(modifier = Modifier.fillMaxSize()) {
        VlcPlayer(
            url = camera.rtspUrl,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TwoCameraLayout(cameras: List<Camera>) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        cameras.forEach { camera ->
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                VlcPlayer(
                    url = camera.rtspUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ThreeCameraLayout(cameras: List<Camera>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // First camera takes top half
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            VlcPlayer(
                url = cameras[0].rtspUrl,
                modifier = Modifier.fillMaxSize()
            )
        }
        // Bottom two cameras share bottom half
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            cameras.drop(1).forEach { camera ->
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    VlcPlayer(
                        url = camera.rtspUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun FourCameraLayout(cameras: List<Camera>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Top row - 2 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            cameras.take(2).forEach { camera ->
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    VlcPlayer(
                        url = camera.rtspUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        // Bottom row - 2 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            cameras.drop(2).take(2).forEach { camera ->
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    VlcPlayer(
                        url = camera.rtspUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun FiveCameraLayout(cameras: List<Camera>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Top row - 2 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            cameras.take(2).forEach { camera ->
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    VlcPlayer(
                        url = camera.rtspUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        // Bottom row - 3 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            cameras.drop(2).take(3).forEach { camera ->
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    VlcPlayer(
                        url = camera.rtspUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun SixCameraLayout(cameras: List<Camera>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Top row - 3 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            cameras.take(3).forEach { camera ->
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    VlcPlayer(
                        url = camera.rtspUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        // Bottom row - 3 cameras
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            cameras.drop(3).take(3).forEach { camera ->
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    VlcPlayer(
                        url = camera.rtspUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}