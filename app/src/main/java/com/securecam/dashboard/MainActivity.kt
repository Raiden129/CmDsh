package com.securecam.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding // ✅ add this
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.securecam.dashboard.data.CamerasViewModel
import com.securecam.dashboard.ui.screens.AdminScreen
import com.securecam.dashboard.ui.screens.HomeScreen
import com.securecam.dashboard.ui.screens.PanelScreen
import com.securecam.dashboard.ui.theme.SecureCamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            SecureCamTheme(darkTheme = true) {
                App()
            }
        }
    }
}

sealed class Dest(val route: String, val label: String) {
    data object Home : Dest("home", "Home")
    data object Panel : Dest("panel", "Panel")
    data object Admin : Dest("admin", "Admin")
}

@Composable
private fun App(vm: CamerasViewModel = viewModel()) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val showBottomBar = currentRoute != Dest.Panel.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(Dest.Home, Dest.Panel, Dest.Admin)
                    items.forEach { dest ->
                        val selected = currentRoute == dest.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                when (dest) {
                                    Dest.Home -> Icon(Icons.Default.Home, contentDescription = dest.label)
                                    Dest.Panel -> Icon(Icons.Default.GridView, contentDescription = dest.label)
                                    Dest.Admin -> Icon(Icons.Default.AdminPanelSettings, contentDescription = dest.label)
                                }
                            },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)   // ✅ works now
        ) {
            composable(Dest.Home.route) {
                val state by vm.state.collectAsStateWithLifecycle()
                HomeScreen(
                    cameras = state.cameras,
                    onOpenAdmin = { navController.navigate(Dest.Admin.route) }
                )
            }
            composable(Dest.Panel.route) {
                val state by vm.state.collectAsStateWithLifecycle()
                PanelScreen(cameras = state.cameras)
            }
            composable(Dest.Admin.route) {
                val state by vm.state.collectAsStateWithLifecycle()
                AdminScreen(
                    cameras = state.cameras,
                    onAdd = { name, url -> vm.addCamera(name, url) },
                    onUpdate = { id, name, url -> vm.updateCamera(id, name, url) },
                    onDelete = { id -> vm.deleteCamera(id) }
                )
            }
        }
    }
}
