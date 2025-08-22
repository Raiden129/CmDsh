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
)

@Serializable
data class CamerasState(
    val cameras: List<Camera> = emptyList()
)
