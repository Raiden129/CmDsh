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
            if (data.isNullOrBlank()) CamerasState() else json.decodeFromString(data)
        } catch (_: Exception) {
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
        val updated = _state.value.cameras.map { c ->
            if (c.id == id) c.copy(streamingSettings = streamingSettings) else c
        }
        val newState = CamerasState(updated)
        _state.value = newState
        persist(newState)
    }

    fun deleteCamera(id: String) {
        val updated = _state.value.cameras.filterNot { it.id == id }
        val newState = CamerasState(updated)
        _state.value = newState
        persist(newState)
    }

    companion object {
        private const val PREFS = "securecam_prefs"
        private const val KEY_CAMERAS = "cameras_json"
    }
}
