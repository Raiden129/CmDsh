package com.securecam.dashboard.ui.components

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
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

private const val TAG = "VlcPlayer"

/**
 * Utility function to detect if the device is an Android TV
 */
fun Context.isAndroidTv(): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
}

/**
 * Get device-specific VLC options optimized for hardware capabilities
 */
private fun getOptimizedVlcOptions(context: Context, isAndroidTv: Boolean): ArrayList<String> {
    val options = arrayListOf("--audio-time-stretch", "--rtsp-tcp")
    
    if (isAndroidTv) {
        Log.d(TAG, "Configuring VLC for Android TV with Mali G31 MP2 optimizations")
        
        options.addAll(listOf(
            // Hardware acceleration for Mali GPU
            "--aout=opensles",
            "--vout=android_display", 
            "--android-display-chroma=RV32",
            
            // Performance optimizations for low-end hardware
            "--avcodec-hw=mediacodec_dr",
            "--mediacodec-dr",
            "--mediacodec-zero-copy",
            
            // Reduce CPU load on Cortex A55
            "--no-audio-time-stretch",
            "--drop-late-frames",
            "--skip-frames=2",
            
            // Memory optimizations
            "--no-stats",
            "--no-osd",
            "--no-spu",
            
            // Thread optimizations for Cortex A55 (quad-core)
            "--avcodec-threads=2",
            "--no-plugins-cache",
            
            // Additional stability options
            "--rtsp-frame-buffer-size=500000",
            "--network-synchronisation"
        ))
    }
    
    return options
}

/**
 * Device-aware VLC Player that automatically optimizes for Android TV
 */
@Composable
fun AdaptiveVlcPlayer(
    url: String,
    modifier: Modifier = Modifier,
    networkCachingMs: Int = 200
) {
    val context = LocalContext.current
    val isAndroidTv = remember { context.isAndroidTv() }
    
    VlcPlayer(
        url = url,
        modifier = modifier,
        networkCachingMs = networkCachingMs,
        isAndroidTv = isAndroidTv
    )
}

@Composable
fun VlcPlayer(
    url: String,
    modifier: Modifier = Modifier,
    networkCachingMs: Int = 200,
    isAndroidTv: Boolean = false
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val libVlc = remember {
        try {
            val options = getOptimizedVlcOptions(context, isAndroidTv)
            Log.d(TAG, "Initializing LibVLC with options: $options")
            LibVLC(context, options)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize LibVLC with optimized options, falling back to basic", e)
            // Fallback to basic configuration
            LibVLC(context, arrayListOf("--rtsp-tcp"))
        }
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
            Log.d(TAG, "Starting playback for URL: $url (Android TV: $isAndroidTv)")
            val media = Media(libVlc, Uri.parse(url))
            
            // Adaptive caching based on device type
            val caching = if (isAndroidTv) {
                // Longer caching for Android TV to handle hardware limitations
                maxOf(networkCachingMs, 1000)
            } else {
                networkCachingMs
            }
            
            Log.d(TAG, "Using network caching: ${caching}ms")
            media.addOption(":network-caching=${caching}")
            
            if (isAndroidTv) {
                // Additional Android TV optimizations
                media.addOption(":clock-jitter=500")
                media.addOption(":clock-synchro=1")
                media.addOption(":live-caching=${caching}")
                media.addOption(":file-caching=${caching}")
                media.addOption(":disc-caching=${caching}")
                
                // Reduce quality for stability on Mali G31 MP2
                media.addOption(":avcodec-skip-frame=1")
                media.addOption(":avcodec-skip-idct=1")
                
                Log.d(TAG, "Applied Android TV specific optimizations")
            } else {
                // Original low-latency settings for tablets/phones
                media.addOption(":clock-jitter=0")
                media.addOption(":clock-synchro=0")
            }
            
            mediaPlayer.media = media
            media.release()
            mediaPlayer.play()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start playback for URL: $url", e)
            // ignore bad URL/stream errors, libVLC is robust but URL may be invalid
        }
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (!mediaPlayer.isPlaying) {
                        Log.d(TAG, "Resuming playback")
                        mediaPlayer.play()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "Pausing playback")
                    mediaPlayer.pause()
                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d(TAG, "Stopping playback")
                    mediaPlayer.stop()
                }
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
