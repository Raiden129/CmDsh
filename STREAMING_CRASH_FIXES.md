# Streaming Settings Crash Fixes

## Problem Description
The app was crashing when changing streaming settings, particularly when switching between TCP and UDP protocols. The crashes were caused by:

1. **Protocol Conflicts**: Both `--rtsp-tcp` and `--rtsp-udp` options were being applied simultaneously
2. **Invalid Settings**: No validation of streaming settings values
3. **Missing Error Handling**: Crashes when applying invalid VLC options
4. **Data Corruption**: Corrupted streaming settings could cause app launch failures

## Fixes Implemented

### 1. Protocol Conflict Resolution
- **File**: `VlcPlayer.kt`
- **Fix**: Ensured only one protocol option is applied at a time
- **Before**: Both TCP and UDP options could be active simultaneously
- **After**: Exclusive protocol selection with fallback to TCP

### 2. Streaming Settings Validation
- **File**: `Models.kt`
- **Fix**: Added `validate()` and `isValid()` functions to `StreamingSettings`
- **Features**:
  - Ensures at least one protocol is selected
  - Validates all numeric values within safe ranges
  - Prevents invalid configurations from being saved

### 3. Enhanced Error Handling
- **File**: `VlcPlayer.kt`
- **Fix**: Added comprehensive try-catch blocks around VLC option application
- **Features**:
  - Individual error handling for each setting category
  - Fallback to safe defaults if specific options fail
  - Graceful degradation instead of crashes

### 4. Data Persistence Safety
- **File**: `CamerasRepository.kt`
- **Fix**: Added validation when loading saved settings
- **Features**:
  - Automatic validation of all streaming settings on app launch
  - Fallback to default settings if corruption is detected
  - Prevents app crashes from corrupted saved data

### 5. UI Validation
- **File**: `StreamingSettingsDialog.kt`
- **Fix**: Added real-time validation in the settings dialog
- **Features**:
  - Save button only enabled with valid settings
  - Visual warnings for invalid configurations
  - Prevents saving invalid settings

### 6. Emergency Recovery System
- **Files**: `CamerasRepository.kt`, `CamerasViewModel.kt`, `AdminScreen.kt`
- **Fix**: Added recovery mechanism for corrupted settings
- **Features**:
  - Emergency reset button in Admin screen
  - Resets all cameras to default streaming settings
  - Complete data clearing as last resort

## Safe Value Ranges

| Setting | Min | Max | Default |
|---------|-----|-----|---------|
| Network Caching | 100ms | 10000ms | 200ms |
| Buffer Size | 256KB | 8192KB | 1024KB |
| Max Latency | 0ms | 5000ms | 0ms |
| Clock Jitter | 0 | 1000 | 0 |
| Clock Synchro | 0 | 1 | 0 |
| RTSP Timeout | 1000ms | 30000ms | 5000ms |
| HTTP Timeout | 1000ms | 30000ms | 5000ms |
| Network Retries | 0 | 10 | 3 |

## Usage Instructions

### Normal Operation
1. Open Admin screen
2. Click "Streaming Settings" for any camera
3. Select desired protocol (TCP or UDP)
4. Adjust other settings as needed
5. Click "Save" (only enabled for valid configurations)

### Emergency Recovery
If the app crashes due to streaming settings:
1. Restart the app
2. Go to Admin screen
3. Scroll to "Emergency Recovery" section
4. Click "Reset All Streaming Settings"
5. All cameras will return to default TCP settings

### Protocol Selection
- **TCP**: More stable, recommended for most networks
- **UDP**: Lower latency, requires good network conditions
- **Note**: Only one protocol can be active at a time

## Technical Details

### Validation Flow
1. User changes settings in dialog
2. Real-time validation in UI
3. Settings validated before saving
4. Repository applies validation on save
5. VLC player validates before applying
6. Fallback to safe defaults if validation fails

### Error Recovery
1. Individual VLC option failures are logged and skipped
2. Corrupted settings are automatically corrected on load
3. Emergency reset available for severe corruption
4. Complete data clearing as final fallback

### Logging
- Debug logs for successful operations
- Warning logs for non-critical failures
- Error logs for critical failures
- All streaming settings changes are logged

## Testing Recommendations

1. **Protocol Switching**: Test TCP ↔ UDP transitions
2. **Invalid Values**: Try extreme values to test validation
3. **Network Conditions**: Test under poor network conditions
4. **App Restart**: Verify settings persist correctly
5. **Recovery**: Test emergency reset functionality

## Future Improvements

1. **Network Testing**: Add network quality detection
2. **Auto-Optimization**: Suggest optimal settings based on conditions
3. **Profile Management**: Save/load setting profiles
4. **Performance Metrics**: Monitor streaming performance
5. **Advanced Validation**: Network-specific validation rules