# Android TV Streaming Optimization Guide

## Problem Analysis

Your Android TV device with **Mali G31 MP2 GPU** and **Cortex A55 x4 CPU** is experiencing video artifacts and stuttering while streaming camera feeds, even though the same streams work perfectly on your tablet.

### Root Causes Identified

1. **Hardware Limitations**:
   - Mali G31 MP2: Entry-level GPU with limited video decoding capabilities
   - Cortex A55: Low-power CPU cores optimized for efficiency, not performance
   - Limited memory bandwidth and processing power for multiple simultaneous streams

2. **Software Configuration**:
   - Default VLC settings were not optimized for Android TV hardware
   - No hardware acceleration explicitly enabled
   - Generic caching and buffering settings

## Implemented Optimizations

### 1. Hardware Acceleration
- **MediaCodec Hardware Decoding**: Enabled `--avcodec-hw=mediacodec_dr`
- **Zero-Copy Optimization**: Added `--mediacodec-zero-copy` for Mali GPU
- **Display Optimization**: Set `--vout=android_display` with `--android-display-chroma=RV32`

### 2. Performance Tuning
- **Frame Dropping**: Enabled `--drop-late-frames` and `--skip-frames=2`
- **CPU Thread Optimization**: Set `--avcodec-threads=2` for Cortex A55 quad-core
- **Memory Reduction**: Disabled stats, OSD, and subtitle processing

### 3. Network & Buffering
- **Adaptive Caching**: Increased to 1000ms minimum for Android TV
- **Clock Synchronization**: Relaxed timing constraints for stability
- **RTSP Buffer**: Increased frame buffer size to 500KB

### 4. Stream Limiting
- **Concurrent Streams**: Limited to 4 cameras max on Android TV (vs 6 on other devices)
- **Quality Reduction**: Added frame and IDCT skipping for Mali G31 MP2

## Usage

The optimizations are automatically applied when running on Android TV. The `AdaptiveVlcPlayer` component detects the device type and applies appropriate settings:

```kotlin
// Automatically optimizes for Android TV
AdaptiveVlcPlayer(
    url = camera.rtspUrl,
    modifier = Modifier.fillMaxSize(),
    networkCachingMs = 1000  // Higher caching for multiple streams
)
```

## Troubleshooting Steps

### 1. Check Logs
Enable debug logging to monitor performance:
```bash
adb logcat | grep VlcPlayer
```

Look for:
- "Configuring VLC for Android TV with Mali G31 MP2 optimizations"
- "Applied Android TV specific optimizations"
- Any error messages about hardware acceleration failure

### 2. Camera Stream Analysis
- **Resolution**: Lower camera resolution to 720p if using 1080p
- **Bitrate**: Reduce camera bitrate to 2-4 Mbps for Mali G31 MP2
- **Codec**: Prefer H.264 over H.265 for better hardware support
- **Frame Rate**: Limit to 15-20 FPS for smoother playback

### 3. Network Optimization
- **Wired Connection**: Use Ethernet instead of WiFi when possible
- **Bandwidth**: Ensure sufficient bandwidth (8-16 Mbps for 4 cameras)
- **Router QoS**: Prioritize video streaming traffic

### 4. System Optimization
- **Background Apps**: Close unnecessary applications
- **RAM**: Ensure adequate free memory (>1GB available)
- **Storage**: Free up storage space for better performance
- **Thermal**: Ensure proper ventilation to prevent thermal throttling

## Performance Monitoring

### Key Metrics to Watch
1. **Frame Drops**: Should be minimal with optimizations
2. **CPU Usage**: Should stay below 80% with 4 concurrent streams
3. **Memory Usage**: Monitor for memory leaks
4. **Network Latency**: Keep under 100ms for smooth streaming

### Fallback Options
If optimizations don't resolve all issues:

1. **Reduce Stream Count**: Display 2-3 cameras instead of 4
2. **Lower Quality**: Use 480p streams for Android TV
3. **Alternate Players**: Consider ExoPlayer with MediaCodec
4. **Cycling Display**: Rotate through cameras instead of showing all

## Hardware Specifications

**Your Android TV Configuration**:
- **GPU**: Mali G31 MP2 (Entry-level, limited video decode units)
- **CPU**: Cortex A55 x4 (1.8-2.0 GHz typical)
- **Memory**: Likely 2-4GB RAM
- **Video Decode**: H.264 up to 1080p@30fps, limited H.265 support

**Recommended Stream Settings**:
- **Resolution**: 1280x720 or lower
- **Bitrate**: 2-4 Mbps per stream
- **Codec**: H.264 (avoid H.265/HEVC)
- **Frame Rate**: 15-20 FPS
- **Max Concurrent**: 4 streams

## Testing the Optimizations

1. **Deploy the Updated App**: Build and install the optimized version
2. **Monitor Performance**: Use adb logcat to check optimization logs
3. **Test Different Scenarios**:
   - Single camera (should be smooth)
   - 2 cameras (should be stable)
   - 4 cameras (may have occasional stutters on complex scenes)
4. **Compare with Tablet**: Performance gap should be significantly reduced

The optimizations should provide substantial improvement for your Mali G31 MP2 / Cortex A55 Android TV device while maintaining smooth performance on your tablet.