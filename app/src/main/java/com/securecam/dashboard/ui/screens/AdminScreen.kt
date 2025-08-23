package com.securecam.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
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
    onResetAllStreamingSettings: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    var editing: Camera? by remember { mutableStateOf(null) }
    var editName by remember { mutableStateOf("") }
    var editUrl by remember { mutableStateOf("") }
    var showStreamingSettings: Camera? by remember { mutableStateOf(null) }
    var showNetworkDiagnostics by remember { mutableStateOf(false) }

    // Update edit fields when editing camera changes
    LaunchedEffect(editing) {
        editing?.let { camera ->
            editName = camera.name
            editUrl = camera.rtspUrl
        }
    }

    // Edit Camera Dialog
    editing?.let { camera ->
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text("Edit Camera") },
            text = {
                CameraFormFields(
                    name = editName,
                    onNameChange = { editName = it },
                    url = editUrl,
                    onUrlChange = { editUrl = it }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdate(camera.id, editName, editUrl)
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
            onApplyRecommendedSettings = { _ ->
                // Apply to all cameras or show a selection dialog
                // For now, just close the dialog
                showNetworkDiagnostics = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Add Camera Section
        Text("Add Camera", style = MaterialTheme.typography.titleLarge)

        CameraFormFields(
            name = name,
            onNameChange = { name = it },
            url = url,
            onUrlChange = { url = it }
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
        ) {
            Text("Add Camera")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Network Diagnostics Section
        NetworkDiagnosticsSection(
            onTestNetwork = { showNetworkDiagnostics = true }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Saved Cameras", style = MaterialTheme.typography.titleLarge)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(cameras, key = { it.id }) { camera ->
                CameraRow(
                    camera = camera,
                    onEdit = { editing = camera },
                    onDelete = { onDelete(camera.id) },
                    onSettings = { showStreamingSettings = camera },
                    onResetAllStreamingSettings = onResetAllStreamingSettings
                )
            }
        }
    }
}

@Composable
private fun CameraFormFields(
    name: String,
    onNameChange: (String) -> Unit,
    url: String,
    onUrlChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Camera Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("RTSP URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NetworkDiagnosticsSection(
    onTestNetwork: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Network Diagnostics", style = MaterialTheme.typography.titleLarge)
        OutlinedButton(onClick = onTestNetwork) {
            Text("Test Network")
        }
    }
    Text(
        "Test your network conditions to get recommended streaming settings",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun CameraRow(
    camera: Camera,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSettings: () -> Unit,
    onResetAllStreamingSettings: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(camera.name, style = MaterialTheme.typography.titleMedium)
            Text(camera.rtspUrl, style = MaterialTheme.typography.bodySmall)

            // Show current streaming settings summary
            if (camera.streamingSettings != StreamingSettings()) {
                StreamingSettingsSummary(settings = camera.streamingSettings)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Edit") }
                OutlinedButton(onClick = onSettings) { Text("Streaming Settings") }
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Delete")
                }
            }
            
            // Emergency recovery section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Emergency Recovery",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        "If the app crashes due to streaming settings, use this to reset all cameras to default settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    OutlinedButton(
                        onClick = onResetAllStreamingSettings,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Reset All Streaming Settings")
                    }
                }
            }
        }
    }
}

@Composable
private fun StreamingSettingsSummary(settings: StreamingSettings) {
    Column {
        Text(
            "Custom streaming settings:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Cache: ${settings.networkCachingMs}ms, " +
                    "Protocol: ${when {
                        settings.useTcp -> "TCP"
                        settings.useUdp -> "UDP"
                        else -> "Auto"
                    }}, " +
                    "Buffer: ${settings.bufferSize}KB",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
