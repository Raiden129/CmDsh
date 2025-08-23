package com.securecam.dashboard.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Camera(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val rtspUrl: String,
    val streamingSettings: StreamingSettings = StreamingSettings()
)

@Serializable
data class StreamingSettings(
    val networkCachingMs: Int = 200,
    val useTcp: Boolean = true,
    val useUdp: Boolean = false,
    val bufferSize: Int = 1024,
    val maxLatency: Int = 0,
    val clockJitter: Int = 0,
    val clockSynchro: Int = 0,
    val rtspTimeout: Int = 5000,
    val httpTimeout: Int = 5000,
    val networkRetries: Int = 3,
    val useHardwareAcceleration: Boolean = true,
    val enableAdaptiveBuffering: Boolean = false,
    val customOptions: Map<String, String> = emptyMap()
) {
    /**
     * Validates and returns a safe version of the streaming settings
     * This prevents crashes by ensuring all values are within safe ranges
     */
    fun validate(): StreamingSettings {
        return copy(
            networkCachingMs = networkCachingMs.coerceIn(100, 10000),
            useTcp = if (!useTcp && !useUdp) true else useTcp, // Ensure at least one protocol is selected
            useUdp = if (!useTcp && !useUdp) false else useUdp,
            bufferSize = bufferSize.coerceIn(256, 8192),
            maxLatency = maxLatency.coerceIn(0, 5000),
            clockJitter = clockJitter.coerceIn(0, 1000),
            clockSynchro = clockSynchro.coerceIn(0, 1),
            rtspTimeout = rtspTimeout.coerceIn(1000, 30000),
            httpTimeout = httpTimeout.coerceIn(1000, 30000),
            networkRetries = networkRetries.coerceIn(0, 10)
        )
    }
    
    /**
     * Checks if the streaming settings are valid
     */
    fun isValid(): Boolean {
        return (useTcp || useUdp) && // At least one protocol must be selected
               networkCachingMs in 100..10000 &&
               bufferSize in 256..8192 &&
               maxLatency in 0..5000 &&
               clockJitter in 0..1000 &&
               clockSynchro in 0..1 &&
               rtspTimeout in 1000..30000 &&
               httpTimeout in 1000..30000 &&
               networkRetries in 0..10
    }
}

@Serializable
data class CamerasState(
    val cameras: List<Camera> = emptyList()
)
