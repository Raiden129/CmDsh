# SecureCam Dashboard

A professional security camera monitoring application with advanced streaming settings and network performance optimization.

## Features

### 🎥 Multi-Camera Support
- Support for up to 6 cameras simultaneously
- Adaptive grid layouts for different camera counts
- Individual camera configuration and management

### 🌐 Advanced Streaming Settings
Each camera can have its own optimized streaming configuration:

#### Network Performance Settings
- **Network Caching**: Configurable buffer size (100ms - 1000ms)
- **Protocol Selection**: TCP (stable) vs UDP (low latency)
- **Buffer Management**: Customizable buffer sizes for optimal performance
- **Latency Control**: Maximum latency settings for live streams
- **Timeout Configuration**: RTSP and HTTP connection timeouts
- **Retry Logic**: Network retry attempts for failed connections

#### Advanced Options
- **Hardware Acceleration**: Enable/disable hardware video decoding
- **Adaptive Buffering**: Dynamic buffer adjustment based on network conditions
- **Clock Synchronization**: Jitter and synchronization control
- **Custom Options**: Additional libVLC parameters for advanced users

### 📊 Network Diagnostics
Built-in network testing tool that:
- Measures network latency, bandwidth, packet loss, and jitter
- Recommends optimal streaming settings based on network conditions
- Provides preset configurations for common scenarios

### 🚀 Preset Configurations
Quick setup with optimized presets:

#### Low Latency
- Network caching: 100ms
- TCP protocol
- Small buffer size (512KB)
- Aggressive retry settings
- Ideal for: Live monitoring, security applications

#### High Stability
- Network caching: 500ms
- TCP protocol
- Large buffer size (2048KB)
- Adaptive buffering enabled
- Ideal for: Poor network conditions, recording applications

#### UDP Fast
- Network caching: 200ms
- UDP protocol
- Medium buffer size (1024KB)
- Balanced settings
- Ideal for: Local networks, low-latency requirements

## Usage

### Adding a Camera
1. Navigate to Admin screen
2. Enter camera name and RTSP URL
3. Click "Add Camera"

### Configuring Streaming Settings
1. In Admin screen, find your camera
2. Click "Streaming Settings"
3. Adjust settings manually or use presets
4. Click "Save" to apply

### Network Diagnostics
1. In Admin screen, click "Test Network"
2. Wait for network analysis to complete
3. Review results and recommendations
4. Apply recommended settings to your cameras

### Best Practices

#### For Stable Networks
- Use TCP protocol
- Higher network caching (300-500ms)
- Larger buffer sizes (1024-2048KB)
- Enable adaptive buffering

#### For Unstable Networks
- Use TCP protocol
- Higher network caching (500-1000ms)
- Larger buffer sizes (2048KB+)
- Enable adaptive buffering
- Increase timeout values

#### For Low Latency Requirements
- Use UDP protocol (if network supports it)
- Lower network caching (100-200ms)
- Smaller buffer sizes (512-1024KB)
- Disable adaptive buffering

## Technical Details

### libVLC Integration
The app uses libVLC 3.6.0 with optimized parameters for:
- RTSP streaming
- Network performance
- Hardware acceleration
- Adaptive buffering

### Network Optimization Features
- **Connection Pooling**: Efficient connection management
- **Automatic Retry**: Smart retry logic for failed connections
- **Buffer Optimization**: Dynamic buffer sizing based on network conditions
- **Protocol Fallback**: Automatic fallback between TCP/UDP

### Performance Monitoring
- Real-time network condition assessment
- Automatic settings adjustment recommendations
- Performance metrics display

## Requirements

- Android 6.0+ (API 23+)
- Network connectivity for camera streams
- Sufficient RAM for multiple video streams

## Building

```bash
./gradlew assembleDebug
```

## License

This project is proprietary software. All rights reserved.

## Support

For technical support or feature requests, please contact the development team.