package com.securecam.dashboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.securecam.dashboard.data.StreamingSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NetworkDiagnostics(
    onDismiss: () -> Unit,
    onApplyRecommendedSettings: (StreamingSettings) -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<NetworkTestResults?>(null) }
    val scope = rememberCoroutineScope()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Network Diagnostics") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isRunning && results == null) {
                    Text(
                        "This will test your network conditions to recommend optimal streaming settings.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = {
                            isRunning = true
                            scope.launch {
                                // Simulate network test
                                delay(2000)
                                results = NetworkTestResults(
                                    latency = 45,
                                    bandwidth = 25.5,
                                    packetLoss = 0.2,
                                    jitter = 12
                                )
                                isRunning = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Network Test")
                    }
                }
                
                if (isRunning) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Text("Testing network conditions...")
                    }
                }
                
                results?.let { testResults ->
                    Text("Network Test Results", style = MaterialTheme.typography.titleSmall)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Latency:")
                        Text("${testResults.latency}ms")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bandwidth:")
                        Text("${testResults.bandwidth} Mbps")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Packet Loss:")
                        Text("${testResults.packetLoss}%")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Jitter:")
                        Text("${testResults.jitter}ms")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val recommendation = when {
                        testResults.latency < 50 && testResults.bandwidth > 20 -> "Low Latency"
                        testResults.packetLoss > 1.0 -> "High Stability"
                        testResults.jitter > 20 -> "High Stability"
                        else -> "Balanced"
                    }
                    
                    Text("Recommended: $recommendation", style = MaterialTheme.typography.titleSmall)
                    
                    Button(
                        onClick = {
                            val recommendedSettings = when (recommendation) {
                                "Low Latency" -> StreamingSettings(
                                    networkCachingMs = 100,
                                    useTcp = true,
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
                                "High Stability" -> StreamingSettings(
                                    networkCachingMs = 500,
                                    useTcp = true,
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
                                else -> StreamingSettings(
                                    networkCachingMs = 200,
                                    useTcp = true,
                                    bufferSize = 1024,
                                    maxLatency = 50,
                                    clockJitter = 0,
                                    clockSynchro = 0,
                                    rtspTimeout = 5000,
                                    httpTimeout = 5000,
                                    networkRetries = 3,
                                    useHardwareAcceleration = true,
                                    enableAdaptiveBuffering = false
                                )
                            }
                            onApplyRecommendedSettings(recommendedSettings)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Recommended Settings")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

data class NetworkTestResults(
    val latency: Int,
    val bandwidth: Double,
    val packetLoss: Double,
    val jitter: Int
)