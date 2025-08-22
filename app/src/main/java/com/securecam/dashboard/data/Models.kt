package com.securecam.dashboard.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Camera(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val rtspUrl: String
)

@Serializable
data class CamerasState(
    val cameras: List<Camera> = emptyList()
)
