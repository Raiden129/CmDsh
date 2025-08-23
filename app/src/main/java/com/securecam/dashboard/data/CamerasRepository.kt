package com.securecam.dashboard.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CamerasRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    private val _state = MutableStateFlow(load())
    val state: StateFlow<CamerasState> = _state.asStateFlow()

    private fun load(): CamerasState {
        val data = prefs.getString(KEY_CAMERAS, null)
        return try {
            if (data.isNullOrBlank()) CamerasState() else {
                val loadedState = json.decodeFromString<CamerasState>(data)
                // Validate all streaming settings to prevent crashes from corrupted data
                val validatedCameras = loadedState.cameras.map { camera ->
                    camera.copy(streamingSettings = camera.streamingSettings.validate())
                }
                CamerasState(validatedCameras)
            }
        } catch (e: Exception) {
            android.util.Log.e("CamerasRepository", "Error loading camera data: ${e.message}")
            // Return default state if loading fails
            CamerasState()
        }
    }

    private fun persist(state: CamerasState) {
        prefs.edit().putString(KEY_CAMERAS, json.encodeToString(state)).apply()
    }

    fun addCamera(name: String, rtspUrl: String) {
        val trimmed = Camera(name = name.trim(), rtspUrl = rtspUrl.trim())
        val newList = _state.value.cameras + trimmed
        val newState = CamerasState(newList)
        _state.value = newState
        persist(newState)
    }

    fun updateCamera(id: String, name: String, rtspUrl: String) {
        val updated = _state.value.cameras.map { c ->
            if (c.id == id) c.copy(name = name.trim(), rtspUrl = rtspUrl.trim()) else c
        }
        val newState = CamerasState(updated)
        _state.value = newState
        persist(newState)
    }
    
    fun updateCameraStreamingSettings(id: String, streamingSettings: StreamingSettings) {
        try {
            android.util.Log.d("CamerasRepository", "Updating streaming settings for camera $id")
            
            // Use the built-in validation to ensure safe settings
            val validatedSettings = streamingSettings.validate()
            
            android.util.Log.d("CamerasRepository", "Validated settings: TCP=${validatedSettings.useTcp}, UDP=${validatedSettings.useUdp}")
            
            val updated = _state.value.cameras.map { c ->
                if (c.id == id) c.copy(streamingSettings = validatedSettings) else c
            }
            val newState = CamerasState(updated)
            _state.value = newState
            persist(newState)
            
            android.util.Log.d("CamerasRepository", "Successfully updated streaming settings for camera $id")
        } catch (e: Exception) {
            // Log error and fallback to safe default
            android.util.Log.e("CamerasRepository", "Error updating streaming settings: ${e.message}")
            // Fallback to default settings
            val fallbackSettings = StreamingSettings()
            val updated = _state.value.cameras.map { c ->
                if (c.id == id) c.copy(streamingSettings = fallbackSettings) else c
            }
            val newState = CamerasState(updated)
            _state.value = newState
            persist(newState)
            
            android.util.Log.d("CamerasRepository", "Applied fallback settings for camera $id")
        }
    }

    fun deleteCamera(id: String) {
        val updated = _state.value.cameras.filterNot { it.id == id }
        val newState = CamerasState(updated)
        _state.value = newState
        persist(newState)
    }
    
    /**
     * Emergency recovery function to reset all streaming settings to defaults
     * Use this if the app crashes due to corrupted streaming settings
     */
    fun resetAllStreamingSettings() {
        try {
            android.util.Log.w("CamerasRepository", "Resetting all streaming settings to defaults")
            val resetCameras = _state.value.cameras.map { camera ->
                camera.copy(streamingSettings = StreamingSettings())
            }
            val newState = CamerasState(resetCameras)
            _state.value = newState
            persist(newState)
            android.util.Log.d("CamerasRepository", "Successfully reset all streaming settings")
        } catch (e: Exception) {
            android.util.Log.e("CamerasRepository", "Error resetting streaming settings: ${e.message}")
            // If even the reset fails, try to clear all data
            try {
                _state.value = CamerasState()
                persist(CamerasState())
                android.util.Log.d("CamerasRepository", "Cleared all camera data as fallback")
            } catch (clearError: Exception) {
                android.util.Log.e("CamerasRepository", "Failed to clear data: ${clearError.message}")
            }
        }
    }

    companion object {
        private const val PREFS = "securecam_prefs"
        private const val KEY_CAMERAS = "cameras_json"
    }
}
