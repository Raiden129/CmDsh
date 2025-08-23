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
    val customOptions: Map<String, String> = emptyMap(),
    
    // Enhanced LibVLC-specific options
    val adaptiveMaxBuffer: Int = 2000,
    val adaptiveMinBuffer: Int = 500,
    val adaptiveLiveBuffer: Int = 1000,
    val skipLoopFilter: Boolean = true,
    val skipFrame: Boolean = false,
    val skipIdct: Boolean = false,
    val avcodecThreads: Int = 0, // 0 = auto-detect
    val jitterBufferSize: Int = 500,
    val deinterlaceMode: String = "blend", // blend, bob, linear, x, yadif, yadif2x
    val videoFilter: String = "", // Custom video filters
    val networkInterface: String = "", // Specific network interface
    val multicastTtl: Int = 1, // Multicast TTL for UDP streams
    val rtspUserAgent: String = "SecureCam/1.0", // Custom user agent
    val enableStats: Boolean = false, // Enable streaming statistics
    val lowLatencyMode: Boolean = false, // Ultra-low latency mode
    val bandwidthLimit: Int = 0, // Bandwidth limit in kbps (0 = unlimited)
    val frameDropThreshold: Int = 10, // Frame drop threshold for low latency
    val bufferWatermark: Int = 80 // Buffer watermark percentage
) {
    /**
     * Validates and returns a safe version of the streaming settings
     * This prevents crashes by ensuring all values are within safe ranges
     */
    fun validate(): StreamingSettings {
        return copy(
            networkCachingMs = networkCachingMs.coerceIn(50, 15000),
            useTcp = if (!useTcp && !useUdp) true else useTcp, // Ensure at least one protocol is selected
            useUdp = if (!useTcp && !useUdp) false else useUdp,
            bufferSize = bufferSize.coerceIn(128, 16384),
            maxLatency = maxLatency.coerceIn(0, 10000),
            clockJitter = clockJitter.coerceIn(0, 2000),
            clockSynchro = clockSynchro.coerceIn(0, 1),
            rtspTimeout = rtspTimeout.coerceIn(1000, 60000),
            httpTimeout = httpTimeout.coerceIn(1000, 60000),
            networkRetries = networkRetries.coerceIn(0, 20),
            
            // Enhanced options validation
            adaptiveMaxBuffer = adaptiveMaxBuffer.coerceIn(500, 10000),
            adaptiveMinBuffer = adaptiveMinBuffer.coerceIn(100, 5000),
            adaptiveLiveBuffer = adaptiveLiveBuffer.coerceIn(100, 5000),
            avcodecThreads = avcodecThreads.coerceIn(0, 32),
            jitterBufferSize = jitterBufferSize.coerceIn(100, 2000),
            multicastTtl = multicastTtl.coerceIn(1, 255),
            bandwidthLimit = bandwidthLimit.coerceIn(0, 100000),
            frameDropThreshold = frameDropThreshold.coerceIn(1, 100),
            bufferWatermark = bufferWatermark.coerceIn(10, 95)
        )
    }
    
    /**
     * Checks if the streaming settings are valid
     */
    fun isValid(): Boolean {
        return (useTcp || useUdp) && // At least one protocol must be selected
               networkCachingMs in 50..15000 &&
               bufferSize in 128..16384 &&
               maxLatency in 0..10000 &&
               clockJitter in 0..2000 &&
               clockSynchro in 0..1 &&
               rtspTimeout in 1000..60000 &&
               httpTimeout in 1000..60000 &&
               networkRetries in 0..20 &&
               adaptiveMaxBuffer in 500..10000 &&
               adaptiveMinBuffer in 100..5000 &&
               adaptiveLiveBuffer in 100..5000 &&
               avcodecThreads in 0..32 &&
               jitterBufferSize in 100..2000 &&
               multicastTtl in 1..255 &&
               bandwidthLimit in 0..100000 &&
               frameDropThreshold in 1..100 &&
               bufferWatermark in 10..95
    }
    
    /**
     * Returns optimized settings for specific use cases
     */
    companion object {
        fun lowLatency(): StreamingSettings {
            return StreamingSettings(
                networkCachingMs = 50,
                useTcp = false,
                useUdp = true,
                bufferSize = 256,
                maxLatency = 0,
                clockJitter = 0,
                clockSynchro = 0,
                rtspTimeout = 2000,
                httpTimeout = 2000,
                networkRetries = 1,
                useHardwareAcceleration = true,
                enableAdaptiveBuffering = false,
                adaptiveMaxBuffer = 500,
                adaptiveMinBuffer = 100,
                adaptiveLiveBuffer = 100,
                skipLoopFilter = true,
                skipFrame = false,
                skipIdct = false,
                lowLatencyMode = true,
                frameDropThreshold = 5,
                bufferWatermark = 20
            )
        }
        
        fun highStability(): StreamingSettings {
            return StreamingSettings(
                networkCachingMs = 1000,
                useTcp = true,
                useUdp = false,
                bufferSize = 4096,
                maxLatency = 500,
                clockJitter = 100,
                clockSynchro = 1,
                rtspTimeout = 15000,
                httpTimeout = 15000,
                networkRetries = 5,
                useHardwareAcceleration = true,
                enableAdaptiveBuffering = true,
                adaptiveMaxBuffer = 5000,
                adaptiveMinBuffer = 2000,
                adaptiveLiveBuffer = 3000,
                skipLoopFilter = false,
                skipFrame = false,
                skipIdct = false,
                lowLatencyMode = false,
                frameDropThreshold = 20,
                bufferWatermark = 80
            )
        }
        
        fun balanced(): StreamingSettings {
            return StreamingSettings(
                networkCachingMs = 200,
                useTcp = true,
                useUdp = false,
                bufferSize = 1024,
                maxLatency = 100,
                clockJitter = 50,
                clockSynchro = 0,
                rtspTimeout = 5000,
                httpTimeout = 5000,
                networkRetries = 3,
                useHardwareAcceleration = true,
                enableAdaptiveBuffering = true,
                adaptiveMaxBuffer = 2000,
                adaptiveMinBuffer = 500,
                adaptiveLiveBuffer = 1000,
                skipLoopFilter = true,
                skipFrame = false,
                skipIdct = false,
                lowLatencyMode = false,
                frameDropThreshold = 10,
                bufferWatermark = 50
            )
        }
        
        fun udpOptimized(): StreamingSettings {
            return StreamingSettings(
                networkCachingMs = 100,
                useTcp = false,
                useUdp = true,
                bufferSize = 512,
                maxLatency = 0,
                clockJitter = 200,
                clockSynchro = 0,
                rtspTimeout = 3000,
                httpTimeout = 3000,
                networkRetries = 2,
                useHardwareAcceleration = true,
                enableAdaptiveBuffering = false,
                adaptiveMaxBuffer = 1000,
                adaptiveMinBuffer = 200,
                adaptiveLiveBuffer = 500,
                skipLoopFilter = true,
                skipFrame = false,
                skipIdct = false,
                lowLatencyMode = true,
                frameDropThreshold = 3,
                bufferWatermark = 30,
                jitterBufferSize = 300,
                multicastTtl = 5
            )
        }
    }
}

@Serializable
data class CamerasState(
    val cameras: List<Camera> = emptyList()
)
