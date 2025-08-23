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
 * Alternative VLC player with software decoding for troubleshooting
 * Use this if hardware decoding causes issues on specific Android TV models
 */
@Composable
fun VlcPlayerSoftware(
    url: String,
    modifier: Modifier = Modifier,
    networkCachingMs: Int = 200
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val isTV = uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION

    val libVlc = remember {
        val options = arrayListOf(
            "--audio-time-stretch",
            "--rtsp-tcp",
            "--no-avcodec-hw",              // Force software decoding
            "--avcodec-codec=any",          // Use any available codec
            "--no-mediacodec-dr",           // Disable direct rendering
            "--no-omxil-dr",                // Disable OMX direct rendering
            "--intf=dummy",                 // No interface
            "--extraintf=",                 // No extra interface
            "--no-video-title-show",        // Don't show video title
            "--no-snapshot-preview",        // Disable snapshot preview
            "--no-medialib",                // Disable media library
            "--no-stats",                   // Disable statistics
            "--android-display-chroma=RV32" // Force RGB32
        )
        
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
            
            // More conservative buffering for software decoding
            val caching = if (isTV) {
                maxOf(networkCachingMs * 5, 2000) // Even more buffering for software decoding
            } else {
                networkCachingMs * 2
            }
            
            media.addOption(":network-caching=$caching")
            media.addOption(":live-caching=$caching")
            media.addOption(":rtsp-caching=$caching")
            media.addOption(":clock-jitter=200")        // Allow more jitter for software decoding
            media.addOption(":clock-synchro=1")
            media.addOption(":avcodec-threads=2")       // Limit threads for stability
            media.addOption(":no-drop-late-frames")
            media.addOption(":no-skip-frames")
            
            mediaPlayer.media = media
            media.release()
            mediaPlayer.play()
        } catch (_: Exception) {
            // ignore bad URL/stream errors
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