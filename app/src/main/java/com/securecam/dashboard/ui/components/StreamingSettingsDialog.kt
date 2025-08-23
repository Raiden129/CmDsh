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
                
                // Protocol selection removed; rely on LibVLC defaults
                
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
                            localSettings = StreamingSettings.lowLatency()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ultra Low Latency")
                    }
                    OutlinedButton(
                        onClick = {
                            localSettings = StreamingSettings.balanced()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Balanced")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            localSettings = StreamingSettings.highStability()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("High Stability")
                    }
                    OutlinedButton(
                        onClick = {
                            localSettings = StreamingSettings.udpOptimized()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("UDP Optimized")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            localSettings = StreamingSettings()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset to Default")
                    }
                }
                
                // Advanced LibVLC Options
                Text("Advanced LibVLC Options", style = MaterialTheme.typography.titleSmall)
                
                // Adaptive Buffer Settings
                OutlinedTextField(
                    value = localSettings.adaptiveMaxBuffer.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            adaptiveMaxBuffer = it.toIntOrNull() ?: 2000
                        )
                    },
                    label = { Text("Adaptive Max Buffer (ms)") },
                    supportingText = { Text("Maximum adaptive buffer size") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.adaptiveMinBuffer.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            adaptiveMinBuffer = it.toIntOrNull() ?: 500
                        )
                    },
                    label = { Text("Adaptive Min Buffer (ms)") },
                    supportingText = { Text("Minimum adaptive buffer size") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.adaptiveLiveBuffer.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            adaptiveLiveBuffer = it.toIntOrNull() ?: 1000
                        )
                    },
                    label = { Text("Adaptive Live Buffer (ms)") },
                    supportingText = { Text("Live stream adaptive buffer size") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Performance Options
                Text("Performance Options", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = localSettings.skipLoopFilter,
                            onCheckedChange = { 
                                localSettings = localSettings.copy(skipLoopFilter = it)
                            }
                        )
                        Text("Skip Loop Filter")
                    }
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = localSettings.skipFrame,
                            onCheckedChange = { 
                                localSettings = localSettings.copy(skipFrame = it)
                            }
                        )
                        Text("Skip Frames")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = localSettings.skipIdct,
                            onCheckedChange = { 
                                localSettings = localSettings.copy(skipIdct = it)
                            }
                        )
                        Text("Skip IDCT")
                    }
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = localSettings.lowLatencyMode,
                            onCheckedChange = { 
                                localSettings = localSettings.copy(lowLatencyMode = it)
                            }
                        )
                        Text("Ultra Low Latency")
                    }
                }
                
                // Advanced Network Options
                Text("Advanced Network Options", style = MaterialTheme.typography.titleSmall)
                
                OutlinedTextField(
                    value = localSettings.avcodecThreads.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            avcodecThreads = it.toIntOrNull() ?: 0
                        )
                    },
                    label = { Text("AVCodec Threads") },
                    supportingText = { Text("0 = auto-detect, 1-32 = manual") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.jitterBufferSize.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            jitterBufferSize = it.toIntOrNull() ?: 500
                        )
                    },
                    label = { Text("Jitter Buffer Size (ms)") },
                    supportingText = { Text("UDP jitter buffer size") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.multicastTtl.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            multicastTtl = it.toIntOrNull() ?: 1
                        )
                    },
                    label = { Text("Multicast TTL") },
                    supportingText = { Text("UDP multicast TTL value") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.bandwidthLimit.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            bandwidthLimit = it.toIntOrNull() ?: 0
                        )
                    },
                    label = { Text("Bandwidth Limit (kbps)") },
                    supportingText = { Text("0 = unlimited") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.frameDropThreshold.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            frameDropThreshold = it.toIntOrNull() ?: 10
                        )
                    },
                    label = { Text("Frame Drop Threshold") },
                    supportingText = { Text("Frame drop threshold for low latency") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.bufferWatermark.toString(),
                    onValueChange = { 
                        localSettings = localSettings.copy(
                            bufferWatermark = it.toIntOrNull() ?: 80
                        )
                    },
                    label = { Text("Buffer Watermark (%)") },
                    supportingText = { Text("Buffer watermark percentage") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Video Processing Options
                Text("Video Processing Options", style = MaterialTheme.typography.titleSmall)
                
                OutlinedTextField(
                    value = localSettings.deinterlaceMode,
                    onValueChange = { 
                        localSettings = localSettings.copy(deinterlaceMode = it)
                    },
                    label = { Text("Deinterlace Mode") },
                    supportingText = { Text("blend, bob, linear, x, yadif, yadif2x") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.videoFilter,
                    onValueChange = { 
                        localSettings = localSettings.copy(videoFilter = it)
                    },
                    label = { Text("Video Filter") },
                    supportingText = { Text("Custom VLC video filters") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.networkInterface,
                    onValueChange = { 
                        localSettings = localSettings.copy(networkInterface = it)
                    },
                    label = { Text("Network Interface") },
                    supportingText = { Text("Specific network interface name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = localSettings.rtspUserAgent,
                    onValueChange = { 
                        localSettings = localSettings.copy(rtspUserAgent = it)
                    },
                    label = { Text("RTSP User Agent") },
                    supportingText = { Text("Custom RTSP user agent string") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Statistics and Monitoring
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(
                            checked = localSettings.enableStats,
                            onCheckedChange = { 
                                localSettings = localSettings.copy(enableStats = it)
                            }
                        )
                        Text("Enable Statistics")
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