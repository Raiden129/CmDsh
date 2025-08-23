package com.securecam.dashboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.securecam.dashboard.data.StreamingSettings

@Composable
fun StreamingSettingsDialog(
    settings: StreamingSettings,
    onSettingsChanged: (StreamingSettings) -> Unit,
    onDismiss: () -> Unit
) {
    // Ensure we always start with valid settings
    var localSettings by remember { mutableStateOf(settings.validate()) }
    
    // Update local settings when input settings change
    LaunchedEffect(settings) {
        localSettings = settings.validate()
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Streaming Settings") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Network Caching
                OutlinedTextField(
                    value = localSettings.networkCachingMs.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            networkCachingMs = it.toIntOrNull() ?: 200
                        )
                    },
                    label = { Text("Network Caching (ms)") },
                    supportingText = { Text("Lower values = lower latency, higher values = more stability") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Protocol Selection
                Text("Protocol Settings", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = localSettings.useTcp,
                            onCheckedChange = { 
                                if (it) {
                                    localSettings = localSettings.copy(
                                        useTcp = true,
                                        useUdp = false
                                    )
                                } else if (!localSettings.useUdp) {
                                    // Ensure at least one protocol is selected
                                    localSettings = localSettings.copy(useTcp = true)
                                } else {
                                    localSettings = localSettings.copy(useTcp = false)
                                }
                            }
                        )
                        Text("TCP (More stable)")
                    }
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = localSettings.useUdp,
                            onCheckedChange = { 
                                if (it) {
                                    localSettings = localSettings.copy(
                                        useUdp = true,
                                        useTcp = false
                                    )
                                } else if (!localSettings.useTcp) {
                                    // Ensure at least one protocol is selected
                                    localSettings = localSettings.copy(useUdp = true)
                                } else {
                                    localSettings = localSettings.copy(useUdp = false)
                                }
                            }
                        )
                        Text("UDP (Lower latency)")
                    }
                }
                
                // Protocol validation warning
                if (!localSettings.useTcp && !localSettings.useUdp) {
                    Text(
                        text = "⚠️ At least one protocol must be selected",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Buffer Settings
                OutlinedTextField(
                    value = localSettings.bufferSize.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            bufferSize = it.toIntOrNull() ?: 1024
                        )
                    },
                    label = { Text("Buffer Size (KB)") },
                    supportingText = { Text("Larger buffers = more stability, smaller = lower latency") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Latency Settings
                OutlinedTextField(
                    value = localSettings.maxLatency.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            maxLatency = it.toIntOrNull() ?: 0
                        )
                    },
                    label = { Text("Max Latency (ms)") },
                    supportingText = { Text("0 = auto, higher values = more buffering") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Timeout Settings
                OutlinedTextField(
                    value = localSettings.rtspTimeout.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            rtspTimeout = it.toIntOrNull() ?: 5000
                        )
                    },
                    label = { Text("RTSP Timeout (ms)") },
                    supportingText = { Text("Connection timeout for RTSP streams") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.httpTimeout.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            httpTimeout = it.toIntOrNull() ?: 5000
                        )
                    },
                    label = { Text("HTTP Timeout (ms)") },
                    supportingText = { Text("Connection timeout for HTTP streams") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Retry Settings
                OutlinedTextField(
                    value = localSettings.networkRetries.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            networkRetries = it.toIntOrNull() ?: 3
                        )
                    },
                    label = { Text("Network Retries") },
                    supportingText = { Text("Number of retry attempts for failed connections") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Advanced Options
                Text("Advanced Options", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = localSettings.useHardwareAcceleration,
                            onCheckedChange = { 
                                localSettings = localSettings.copy(useHardwareAcceleration = it)
                            }
                        )
                        Text("Hardware Acceleration")
                    }
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = localSettings.enableAdaptiveBuffering,
                            onCheckedChange = { 
                                localSettings = localSettings.copy(enableAdaptiveBuffering = it)
                            }
                        )
                        Text("Adaptive Buffering")
                    }
                }
                
                // Clock Settings
                Text("Clock Settings", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = localSettings.clockJitter.toString(),
                        onValueChange = { 
                            localSettings = localSettings.copy(
                                clockJitter = it.toIntOrNull() ?: 0
                            )
                        },
                        label = { Text("Clock Jitter") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = localSettings.clockSynchro.toString(),
                        onValueChange = { 
                            localSettings = localSettings.copy(
                                clockSynchro = it.toIntOrNull() ?: 0
                            )
                        },
                        label = { Text("Clock Synchro") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Preset Configurations
                Text("Preset Configurations", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            localSettings = StreamingSettings(
                                networkCachingMs = 100,
                                useTcp = true,
                                useUdp = false,
                                bufferSize = 512,
                                maxLatency = 0,
                                clockJitter = 0,
                                clockSynchro = 0,
                                rtspTimeout = 3000,
                                httpTimeout = 3000,
                                networkRetries = 5,
                                useHardwareAcceleration = true,
                                enableAdaptiveBuffering = false
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Low Latency")
                    }
                    OutlinedButton(
                        onClick = {
                            localSettings = StreamingSettings(
                                networkCachingMs = 500,
                                useTcp = true,
                                useUdp = false,
                                bufferSize = 2048,
                                maxLatency = 100,
                                clockJitter = 0,
                                clockSynchro = 0,
                                rtspTimeout = 10000,
                                httpTimeout = 10000,
                                networkRetries = 3,
                                useHardwareAcceleration = true,
                                enableAdaptiveBuffering = true
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("High Stability")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            localSettings = StreamingSettings(
                                networkCachingMs = 200,
                                useTcp = false,
                                useUdp = true,
                                bufferSize = 1024,
                                maxLatency = 0,
                                clockJitter = 0,
                                clockSynchro = 0,
                                rtspTimeout = 5000,
                                httpTimeout = 5000,
                                networkRetries = 3,
                                useHardwareAcceleration = true,
                                enableAdaptiveBuffering = false
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("UDP Fast")
                    }
                    OutlinedButton(
                        onClick = {
                            localSettings = StreamingSettings()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset to Default")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validate settings before saving
                    if (localSettings.isValid()) {
                        onSettingsChanged(localSettings)
                        onDismiss()
                    }
                },
                enabled = localSettings.isValid()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}