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

    val libVlc = remember {
        val options = mutableListOf<String>()
        
        // Basic options
        options.add("--audio-time-stretch")
        
        // Protocol options based on settings
        if (streamingSettings.useTcp) {
            options.add("--rtsp-tcp")
        }
        if (streamingSettings.useUdp) {
            options.add("--rtsp-udp")
        }
        
        // Network performance options
        if (streamingSettings.enableAdaptiveBuffering) {
            options.add("--adaptive-maxbuffer")
        }
        
        // Hardware acceleration
        if (streamingSettings.useHardwareAcceleration) {
            options.add("--avcodec-hw")
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

    LaunchedEffect(url) {
        try {
            val media = Media(libVlc, Uri.parse(url))
            
            // Apply streaming settings
            media.addOption(":network-caching=${streamingSettings.networkCachingMs}")
            media.addOption(":clock-jitter=${streamingSettings.clockJitter}")
            media.addOption(":clock-synchro=${streamingSettings.clockSynchro}")
            media.addOption(":rtsp-timeout=${streamingSettings.rtspTimeout}")
            media.addOption(":http-timeout=${streamingSettings.httpTimeout}")
            media.addOption(":network-retries=${streamingSettings.networkRetries}")
            
            // Buffer settings
            if (streamingSettings.bufferSize > 0) {
                media.addOption(":sout-mux-caching=${streamingSettings.bufferSize}")
            }
            
            // Latency settings
            if (streamingSettings.maxLatency > 0) {
                media.addOption(":clock-synchro=0")
                media.addOption(":live-caching=${streamingSettings.maxLatency}")
            }
            
            // Custom options
            streamingSettings.customOptions.forEach { (key, value) ->
                media.addOption(":$key=$value")
            }
            
            // Additional network performance optimizations
            if (streamingSettings.enableAdaptiveBuffering) {
                media.addOption(":adaptive-maxbuffer=2000")
                media.addOption(":adaptive-minbuffer=500")
            }
            
            // Network interface selection for better performance
            media.addOption(":network-caching=${streamingSettings.networkCachingMs}")
            
            // RTSP specific optimizations
            if (streamingSettings.useTcp) {
                media.addOption(":rtsp-tcp")
            }
            
            // Reduce jitter for live streams
            if (streamingSettings.maxLatency > 0) {
                media.addOption(":live-caching=${streamingSettings.maxLatency}")
                media.addOption(":clock-synchro=0")
            }
            
            // Advanced network optimizations
            if (streamingSettings.networkRetries > 0) {
                media.addOption(":network-retries=${streamingSettings.networkRetries}")
            }
            
            // Buffer management for better performance
            if (streamingSettings.bufferSize > 0) {
                media.addOption(":sout-mux-caching=${streamingSettings.bufferSize}")
                media.addOption(":live-caching=${streamingSettings.bufferSize}")
            }
            
            // Protocol-specific timeouts
            media.addOption(":rtsp-timeout=${streamingSettings.rtspTimeout}")
            media.addOption(":http-timeout=${streamingSettings.httpTimeout}")
            
            // Clock synchronization for live streams
            media.addOption(":clock-jitter=${streamingSettings.clockJitter}")
            media.addOption(":clock-synchro=${streamingSettings.clockSynchro}")
            
            mediaPlayer.media = media
            media.release()
            mediaPlayer.play()
        } catch (_: Exception) {
            // ignore bad URL/stream errors, libVLC is robust but URL may be invalid
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
