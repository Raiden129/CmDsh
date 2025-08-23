package com.securecam.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.securecam.dashboard.data.Camera

@Composable
fun AdminScreen(
    cameras: List<Camera>,
    onAdd: (String, String) -> Unit,
    onUpdate: (String, String, String) -> Unit,
    onDelete: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    var editing: Camera? by remember { mutableStateOf(null) }
    var editName by remember { mutableStateOf("") }
    var editUrl by remember { mutableStateOf("") }

    if (editing != null) {
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text("Edit Camera") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Camera Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editUrl,
                        onValueChange = { editUrl = it },
                        label = { Text("RTSP URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                FilledTonalButton(onClick = {
                    editing?.let { onUpdate(it.id, editName, editUrl) }
                    editing = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editing = null }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ElevatedCard(shape = MaterialTheme.shapes.large) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Add Camera", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Camera Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("RTSP URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = {
                                if (name.isNotBlank() && url.isNotBlank()) {
                                    onAdd(name.trim(), url.trim())
                                    name = ""
                                    url = ""
                                }
                            },
                            enabled = name.isNotBlank() && url.isNotBlank()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Add Camera")
                        }
                    }
                }
            }
        }

        item { Divider() }

        item {
            Text("Saved Cameras", style = MaterialTheme.typography.titleLarge)
        }

        items(cameras, key = { it.id }) { cam ->
            ElevatedCard(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(cam.name, style = MaterialTheme.typography.titleMedium)
                    Text(cam.rtspUrl, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = {
                            editing = cam
                            editName = cam.name
                            editUrl = cam.rtspUrl
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Edit")
                        }
                        OutlinedButton(onClick = { onDelete(cam.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraRow(cam: Camera, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(cam.name, style = MaterialTheme.typography.titleMedium)
            Text(cam.rtspUrl, style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Edit") }
                OutlinedButton(onClick = onDelete, colors = ButtonDefaults.outlinedButtonColors()) { Text("Delete") }
            }
        }
    }
}
