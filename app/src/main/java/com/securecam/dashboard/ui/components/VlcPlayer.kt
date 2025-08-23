package com.securecam.dashboard.ui.components

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

/**
 * Detects if the app is running on Android TV
 */
private fun Context.isAndroidTV(): Boolean {
    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}

@Composable
fun VlcPlayer(
    url: String,
    modifier: Modifier = Modifier,
    networkCachingMs: Int = 200
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val isTV = remember { context.isAndroidTV() }

    val libVlc = remember {
        val options = arrayListOf(
            "--audio-time-stretch",
            "--rtsp-tcp"
        )
        
        // Add Android TV specific optimizations
        if (isTV) {
            options.addAll(listOf(
                "--intf=dummy",                    // No interface
                "--extraintf=",                    // No extra interface
                "--no-video-title-show",           // Don't show video title
                "--no-snapshot-preview",           // Disable snapshot preview
                "--android-display-chroma=RV32",   // Force RGB32 for better TV compatibility
                "--no-medialib",                   // Disable media library
                "--no-stats",                      // Disable statistics
                "--avcodec-skiploopfilter=4",      // Skip loop filter for performance
                "--avcodec-skipframe=0",           // Don't skip frames
                "--avcodec-skip-idct=0",           // Don't skip IDCT
                "--no-avcodec-hurry-up"            // Don't hurry decoding
            ))
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
            
            // Adaptive caching based on device type
            val caching = if (isTV) {
                // More aggressive buffering for TV to reduce stuttering
                maxOf(networkCachingMs * 3, 1000) // At least 1 second cache for TV
            } else {
                networkCachingMs // Original low latency for tablets
            }
            
            media.addOption(":network-caching=$caching")
            
            if (isTV) {
                // TV-optimized streaming options
                media.addOption(":live-caching=$caching")
                media.addOption(":disc-caching=$caching")
                media.addOption(":file-caching=$caching")
                media.addOption(":network-caching-timeout=5000")
                media.addOption(":rtsp-caching=$caching")
                media.addOption(":sout-mux-caching=$caching")
                // Allow some jitter for stability on TV hardware
                media.addOption(":clock-jitter=100")
                media.addOption(":clock-synchro=1")
                // Buffer management for smoother playback
                media.addOption(":avcodec-threads=0")  // Auto-detect CPU cores
                media.addOption(":no-drop-late-frames")
                media.addOption(":no-skip-frames")
            } else {
                // Original low-latency settings for tablets
                media.addOption(":clock-jitter=0")
                media.addOption(":clock-synchro=0")
            }
            
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
