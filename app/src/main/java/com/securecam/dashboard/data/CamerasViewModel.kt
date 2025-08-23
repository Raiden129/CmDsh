package com.securecam.dashboard.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CamerasViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CamerasRepository(app.applicationContext)
    val state: StateFlow<CamerasState> = repo.state

    fun addCamera(name: String, url: String) {
        viewModelScope.launch { repo.addCamera(name, url) }
    }

    fun updateCamera(id: String, name: String, url: String) {
        viewModelScope.launch { repo.updateCamera(id, name, url) }
    }
    
    fun updateCameraStreamingSettings(id: String, streamingSettings: StreamingSettings) {
        viewModelScope.launch { repo.updateCameraStreamingSettings(id, streamingSettings) }
    }

    fun deleteCamera(id: String) {
        viewModelScope.launch { repo.deleteCamera(id) }
    }
    
    /**
     * Emergency recovery function to reset all streaming settings
     * Call this if the app crashes due to corrupted streaming settings
     */
    fun resetAllStreamingSettings() {
        viewModelScope.launch { repo.resetAllStreamingSettings() }
    }
}
