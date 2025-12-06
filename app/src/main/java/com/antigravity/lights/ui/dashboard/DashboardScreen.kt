package com.antigravity.lights.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.antigravity.lights.ui.components.DeviceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToPattern: (String) -> Unit, // deviceId
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Lights") },
                actions = {
                    IconButton(onClick = { viewModel.scanForDevices() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Scan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Check for new devices */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            when (val s = state) {
                is DashboardUiState.Loading -> {
                    Text("Loading...")
                }
                is DashboardUiState.Success -> {
                    if (s.knownDevices.isNotEmpty()) {
                        Text(
                            "My Devices",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(s.knownDevices) { device ->
                                DeviceItem(
                                    device = device,
                                    onToggle = { viewModel.toggleDevice(it) },
                                    onClick = { onNavigateToPattern(it.id) }
                                )
                            }
                        }
                    }
                    
                    if (s.discoveredDevices.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Discovered",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(s.discoveredDevices) { device ->
                                DeviceItem(
                                    device = device,
                                    onToggle = { /* Can't toggle untracked device */ },
                                    onClick = { viewModel.addDevice(device) } // Click to add
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
