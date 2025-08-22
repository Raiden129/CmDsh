package com.securecam.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.securecam.dashboard.data.Camera
import com.securecam.dashboard.data.StreamingSettings
import com.securecam.dashboard.ui.components.StreamingSettingsDialog
import com.securecam.dashboard.ui.components.NetworkDiagnostics

@Composable
fun AdminScreen(
    cameras: List<Camera>,
    onAdd: (String, String) -> Unit,
    onUpdate: (String, String, String) -> Unit,
    onDelete: (String) -> Unit,
    onUpdateStreamingSettings: (String, StreamingSettings) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    var editing: Camera? by remember { mutableStateOf(null) }
    var editName by remember { mutableStateOf("") }
    var editUrl by remember { mutableStateOf("") }
    var showStreamingSettings: Camera? by remember { mutableStateOf(null) }
    var showNetworkDiagnostics by remember { mutableStateOf(false) }

    if (editing != null) {
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text("Edit Camera") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Camera Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editUrl,
                        onValueChange = { editUrl = it },
                        label = { Text("RTSP URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    editing?.let { onUpdate(it.id, editName, editUrl) }
                    editing = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editing = null }) { Text("Cancel") }
            }
        )
    }
    
    // Streaming Settings Dialog
    showStreamingSettings?.let { camera ->
        StreamingSettingsDialog(
            settings = camera.streamingSettings,
            onSettingsChanged = { newSettings ->
                onUpdateStreamingSettings(camera.id, newSettings)
            },
            onDismiss = { showStreamingSettings = null }
        )
    }
    
    // Network Diagnostics Dialog
    if (showNetworkDiagnostics) {
        NetworkDiagnostics(
            onDismiss = { showNetworkDiagnostics = false },
            onApplyRecommendedSettings = { settings ->
                // Apply to all cameras or show a selection dialog
                // For now, just close the dialog
                showNetworkDiagnostics = false
            }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Add Camera", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Camera Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("RTSP URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (name.isNotBlank() && url.isNotBlank()) {
                    onAdd(name.trim(), url.trim())
                    name = ""
                    url = ""
                }
            },
            enabled = name.isNotBlank() && url.isNotBlank()
        ) { Text("Add Camera") }

        Divider(Modifier.padding(vertical = 8.dp))
        
        // Network Diagnostics Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Network Diagnostics", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { showNetworkDiagnostics = true }) {
                Text("Test Network")
            }
        }
        Text(
            "Test your network conditions to get recommended streaming settings",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Divider(Modifier.padding(vertical = 8.dp))
        Text("Saved Cameras", style = MaterialTheme.typography.titleLarge)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(cameras, key = { it.id }) { cam ->
                CameraRow(
                    cam = cam,
                    onEdit = {
                        editing = cam
                        editName = cam.name
                        editUrl = cam.rtspUrl
                    },
                    onDelete = { onDelete(cam.id) },
                    onSettings = { showStreamingSettings = cam }
                )
            }
        }
    }
}

@Composable
private fun CameraRow(cam: Camera, onEdit: () -> Unit, onDelete: () -> Unit, onSettings: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(cam.name, style = MaterialTheme.typography.titleMedium)
            Text(cam.rtspUrl, style = MaterialTheme.typography.bodySmall)
            
            // Show current streaming settings summary
            if (cam.streamingSettings != StreamingSettings()) {
                val settings = cam.streamingSettings
                Column {
                    Text(
                        "Custom streaming settings:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Cache: ${settings.networkCachingMs}ms, " +
                        "Protocol: ${if (settings.useTcp) "TCP" else if (settings.useUdp) "UDP" else "Auto"}, " +
                        "Buffer: ${settings.bufferSize}KB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Edit") }
                OutlinedButton(onClick = onSettings) { Text("Streaming Settings") }
                OutlinedButton(onClick = onDelete, colors = ButtonDefaults.outlinedButtonColors()) { Text("Delete") }
            }
        }
    }
}
