package com.securecam.dashboard.ui.components

import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.securecam.dashboard.data.StreamingSettings
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

@Composable
fun VlcPlayer(
    url: String,
    modifier: Modifier = Modifier,
    streamingSettings: StreamingSettings = StreamingSettings()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Create LibVLC instance with optimized options
    val libVlc = remember {
        val options = mutableListOf<String>()
        
        // Core LibVLC options for better performance
        options.add("--no-audio") // Disable audio for camera streams
        options.add("--no-video-title-show") // Hide video title
        options.add("--no-stats") // Disable statistics display
        
        // Network performance optimizations
        options.add("--network-caching=${streamingSettings.networkCachingMs}")
        options.add("--live-caching=${streamingSettings.networkCachingMs}")
        
        // Hardware acceleration
        if (streamingSettings.useHardwareAcceleration) {
            options.add("--avcodec-hw=any") // Enable hardware acceleration for any available codec
            options.add("--avcodec-threads=${streamingSettings.avcodecThreads}") // Thread count
        }
        
        // Protocol selection - CRITICAL FIX: Only one protocol at a time
        when {
            streamingSettings.useUdp -> {
                options.add("--rtsp-udp")
                options.add("--rtsp-tcp=0") // Explicitly disable TCP
            }
            else -> {
                options.add("--rtsp-tcp")
                options.add("--rtsp-udp=0") // Explicitly disable UDP
            }
        }
        
        // Advanced network optimizations
        options.add("--adaptive-maxbuffer=${streamingSettings.adaptiveMaxBuffer}")
        options.add("--adaptive-minbuffer=${streamingSettings.adaptiveMinBuffer}")
        options.add("--adaptive-live-buffer=${streamingSettings.adaptiveLiveBuffer}")
        
        // Clock synchronization for live streams
        options.add("--clock-jitter=${streamingSettings.clockJitter}")
        options.add("--clock-synchro=${streamingSettings.clockSynchro}")
        
        // Network timeout and retry settings
        options.add("--rtsp-timeout=${streamingSettings.rtspTimeout}")
        options.add("--http-timeout=${streamingSettings.httpTimeout}")
        options.add("--network-retries=${streamingSettings.networkRetries}")
        
        // Buffer management
        options.add("--sout-mux-caching=${streamingSettings.bufferSize}")
        options.add("--live-caching=${streamingSettings.maxLatency.takeIf { it > 0 } ?: streamingSettings.networkCachingMs}")
        
        // Performance optimizations
        if (streamingSettings.skipLoopFilter) {
            options.add("--avcodec-skiploopfilter=1") // Skip loop filter for better performance
        }
        if (streamingSettings.skipFrame) {
            options.add("--avcodec-skip-frame=1") // Skip frames if needed
        }
        if (streamingSettings.skipIdct) {
            options.add("--avcodec-skip-idct=1") // Skip IDCT if needed
        }
        
        // Memory management
        options.add("--avcodec-max-threads=${streamingSettings.avcodecThreads}")
        
        // Network interface optimizations
        if (streamingSettings.networkInterface.isNotBlank()) {
            options.add("--network-interface=${streamingSettings.networkInterface}")
        }
        
        // RTSP specific optimizations
        if (streamingSettings.useUdp) {
            // UDP specific optimizations
            options.add("--network-caching=${(streamingSettings.networkCachingMs * 0.5).toInt().coerceAtLeast(50)}")
            options.add("--live-caching=${(streamingSettings.networkCachingMs * 0.5).toInt().coerceAtLeast(50)}")
            options.add("--multicast-ttl=${streamingSettings.multicastTtl}")
            options.add("--jitter-buffer-size=${streamingSettings.jitterBufferSize}")
        } else {
            // TCP specific optimizations
            options.add("--network-caching=${streamingSettings.networkCachingMs}")
            options.add("--live-caching=${streamingSettings.networkCachingMs}")
        }
        
        // Adaptive buffering
        if (streamingSettings.enableAdaptiveBuffering) {
            options.add("--adaptive-maxbuffer=${streamingSettings.adaptiveMaxBuffer}")
            options.add("--adaptive-minbuffer=${streamingSettings.adaptiveMinBuffer}")
            options.add("--adaptive-live-buffer=${streamingSettings.adaptiveLiveBuffer}")
        }
        
        // Jitter buffer for UDP streams
        if (streamingSettings.useUdp) {
            options.add("--clock-jitter=${streamingSettings.clockJitter}")
            options.add("--clock-synchro=0") // Disable clock sync for UDP
        } else {
            options.add("--clock-jitter=${streamingSettings.clockJitter}")
            options.add("--clock-synchro=${streamingSettings.clockSynchro}")
        }
        
        // Low latency mode optimizations
        if (streamingSettings.lowLatencyMode) {
            options.add("--live-caching=0") // Minimal caching for ultra-low latency
            options.add("--network-caching=0") // Minimal network caching
            options.add("--clock-synchro=0") // Disable clock synchronization
            options.add("--avcodec-skiploopfilter=1") // Skip loop filter
        }
        
        // Bandwidth limiting
        if (streamingSettings.bandwidthLimit > 0) {
            options.add("--sout-mux-caching=${streamingSettings.bufferSize}")
            options.add("--live-caching=${streamingSettings.maxLatency.takeIf { it > 0 } ?: streamingSettings.networkCachingMs}")
        }
        
        // Custom user agent
        if (streamingSettings.rtspUserAgent.isNotBlank()) {
            options.add("--rtsp-user-agent=${streamingSettings.rtspUserAgent}")
        }
        
        // Video filters
        if (streamingSettings.videoFilter.isNotBlank()) {
            options.add("--video-filter=${streamingSettings.videoFilter}")
        }
        
        // Deinterlacing
        if (streamingSettings.deinterlaceMode.isNotBlank()) {
            options.add("--deinterlace=${streamingSettings.deinterlaceMode}")
        }
        
        // Statistics (if enabled)
        if (streamingSettings.enableStats) {
            options.add("--stats") // Enable statistics
        }

        LibVLC(context, options)
    }
    
    val mediaPlayer = remember { MediaPlayer(libVlc) }
    val videoLayout = remember { VLCVideoLayout(context) }

    AndroidView(
        factory = { _ ->
            videoLayout.apply {
                layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }.also {
                mediaPlayer.attachViews(it, null, false, false)
            }
        },
        modifier = modifier
    )

    LaunchedEffect(url, streamingSettings) {
        try {
            val media = Media(libVlc, Uri.parse(url))
            
            // Apply additional media-specific options that can't be set at LibVLC level
            try {
                // Network caching (can be overridden per media)
                media.addOption(":network-caching=${streamingSettings.networkCachingMs}")
                media.addOption(":live-caching=${streamingSettings.maxLatency.takeIf { it > 0 } ?: streamingSettings.networkCachingMs}")
                
                // Buffer settings
                if (streamingSettings.bufferSize > 0) {
                    media.addOption(":sout-mux-caching=${streamingSettings.bufferSize}")
                }
                
                // Latency control
                if (streamingSettings.maxLatency > 0) {
                    media.addOption(":live-caching=${streamingSettings.maxLatency}")
                    media.addOption(":clock-synchro=0")
                }
                
                // Custom options from user
                streamingSettings.customOptions.forEach { (key, value) ->
                    if (key.isNotBlank() && value.isNotBlank()) {
                        media.addOption(":$key=$value")
                    }
                }
                
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying media options: ${e.message}")
            }
            
            mediaPlayer.media = media
            media.release()
            mediaPlayer.play()
            
        } catch (e: Exception) {
            android.util.Log.e("VlcPlayer", "Error setting up media: ${e.message}")
            
            // Emergency fallback with minimal settings
            try {
                val fallbackMedia = Media(libVlc, Uri.parse(url))
                fallbackMedia.addOption(":rtsp-tcp")
                fallbackMedia.addOption(":network-caching=500")
                fallbackMedia.addOption(":live-caching=500")
                mediaPlayer.media = fallbackMedia
                fallbackMedia.release()
                mediaPlayer.play()
            } catch (fallbackError: Exception) {
                android.util.Log.e("VlcPlayer", "Fallback media setup also failed: ${fallbackError.message}")
            }
        }
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> if (!mediaPlayer.isPlaying) mediaPlayer.play()
                Lifecycle.Event.ON_PAUSE -> mediaPlayer.pause()
                Lifecycle.Event.ON_STOP -> mediaPlayer.stop()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            try {
                mediaPlayer.stop()
            } catch (_: Exception) {}
            try {
                mediaPlayer.detachViews()
            } catch (_: Exception) {}
            try {
                mediaPlayer.release()
            } catch (_: Exception) {}
            try {
                libVlc.release()
            } catch (_: Exception) {}
        }
    }
}
