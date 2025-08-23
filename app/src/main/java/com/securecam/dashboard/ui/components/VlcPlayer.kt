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
        
        // Protocol options - ensure only one is selected to avoid conflicts
        when {
            streamingSettings.useTcp -> options.add("--rtsp-tcp")
            streamingSettings.useUdp -> options.add("--rtsp-udp")
            else -> options.add("--rtsp-tcp") // Default to TCP if neither is explicitly set
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
            
            // Use the built-in validation to ensure safe settings
            val validatedSettings = streamingSettings.validate()
            
            // Apply streaming settings with validation
            try {
                media.addOption(":network-caching=${validatedSettings.networkCachingMs}")
                media.addOption(":clock-jitter=${validatedSettings.clockJitter}")
                media.addOption(":clock-synchro=${validatedSettings.clockSynchro}")
                media.addOption(":rtsp-timeout=${validatedSettings.rtspTimeout}")
                media.addOption(":http-timeout=${validatedSettings.httpTimeout}")
                media.addOption(":network-retries=${validatedSettings.networkRetries}")
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying some streaming options: ${e.message}")
            }
            
            // Buffer settings with validation
            try {
                if (validatedSettings.bufferSize > 0) {
                    media.addOption(":sout-mux-caching=${validatedSettings.bufferSize}")
                }
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying buffer settings: ${e.message}")
            }
            
            // Latency settings with validation
            try {
                if (validatedSettings.maxLatency > 0) {
                    media.addOption(":clock-synchro=0")
                    media.addOption(":live-caching=${validatedSettings.maxLatency}")
                }
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying latency settings: ${e.message}")
            }
            
            // Custom options with validation
            try {
                validatedSettings.customOptions.forEach { (key, value) ->
                    if (key.isNotBlank() && value.isNotBlank()) {
                        media.addOption(":$key=$value")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying custom options: ${e.message}")
            }
            
            // Additional network performance optimizations
            try {
                if (validatedSettings.enableAdaptiveBuffering) {
                    media.addOption(":adaptive-maxbuffer=2000")
                    media.addOption(":adaptive-minbuffer=500")
                }
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying adaptive buffering: ${e.message}")
            }
            
            // Network interface selection for better performance
            try {
                media.addOption(":network-caching=${validatedSettings.networkCachingMs}")
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying network caching: ${e.message}")
            }
            
            // RTSP specific optimizations - only add if not already set in LibVLC options
            // Note: Protocol is already set in LibVLC initialization above
            
            // Reduce jitter for live streams
            try {
                if (validatedSettings.maxLatency > 0) {
                    media.addOption(":live-caching=${validatedSettings.maxLatency}")
                    media.addOption(":clock-synchro=0")
                }
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying jitter settings: ${e.message}")
            }
            
            // Advanced network optimizations
            try {
                if (validatedSettings.networkRetries > 0) {
                    media.addOption(":network-retries=${validatedSettings.networkRetries}")
                }
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying network retry settings: ${e.message}")
            }
            
            // Buffer management for better performance
            try {
                if (validatedSettings.bufferSize > 0) {
                    media.addOption(":sout-mux-caching=${validatedSettings.bufferSize}")
                    media.addOption(":live-caching=${validatedSettings.bufferSize}")
                }
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying buffer management: ${e.message}")
            }
            
            // Protocol-specific timeouts
            try {
                media.addOption(":rtsp-timeout=${validatedSettings.rtspTimeout}")
                media.addOption(":http-timeout=${validatedSettings.httpTimeout}")
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying timeout settings: ${e.message}")
            }
            
            // Clock synchronization for live streams
            try {
                media.addOption(":clock-jitter=${validatedSettings.clockJitter}")
                media.addOption(":clock-synchro=${validatedSettings.clockSynchro}")
            } catch (e: Exception) {
                android.util.Log.w("VlcPlayer", "Error applying clock settings: ${e.message}")
            }
            
            mediaPlayer.media = media
            media.release()
            mediaPlayer.play()
        } catch (e: Exception) {
            android.util.Log.e("VlcPlayer", "Error setting up media: ${e.message}")
            // Try to play with minimal settings if there's an error
            try {
                val fallbackMedia = Media(libVlc, Uri.parse(url))
                fallbackMedia.addOption(":rtsp-tcp")
                fallbackMedia.addOption(":network-caching=500")
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
