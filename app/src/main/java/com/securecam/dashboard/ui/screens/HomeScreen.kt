package com.securecam.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.securecam.dashboard.data.Camera
import com.securecam.dashboard.ui.components.VlcPlayer

@Composable
fun HomeScreen(
    cameras: List<Camera>,
    onOpenAdmin: () -> Unit,
) {
    if (cameras.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No cameras configured", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onOpenAdmin) { Text("Add a Camera") }
            }
        }
        return
    }

    // Adaptive grid that works on phones and larger screens
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(cameras, key = { it.id }) { cam ->
            CameraCard(cam)
        }
    }
}

@Composable
private fun CameraCard(cam: Camera) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
                VlcPlayer(
                    url = cam.rtspUrl, 
                    modifier = Modifier.matchParentSize(),
                    streamingSettings = cam.streamingSettings
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(cam.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 12.dp))
            Spacer(Modifier.height(6.dp))
        }
    }
}
