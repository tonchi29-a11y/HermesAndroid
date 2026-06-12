package com.hermes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hermes.app.data.preferences.AppPreferences
import com.hermes.app.ui.theme.StatusGreen
import com.hermes.app.ui.theme.StatusRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    prefs: AppPreferences,
    isConnected: Boolean,
    onCheckConnection: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    var host by remember { mutableStateOf("127.0.0.1") }
    var port by remember { mutableStateOf("8642") }
    var apiKey by remember { mutableStateOf("") }
    var showApiKey by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Load current values
    LaunchedEffect(Unit) {
        host = prefs.host.first()
        port = prefs.port.first().toString()
        apiKey = prefs.apiKey.first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
        ) {
            // Connection status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Connection", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (isConnected) "Connected to Hermes" else "Not connected",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isConnected) StatusGreen else StatusRed,
                        )
                    }
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (isConnected) StatusGreen.copy(alpha = 0.2f) else StatusRed.copy(alpha = 0.2f),
                    ) {
                        Text(
                            if (isConnected) "ONLINE" else "OFFLINE",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isConnected) StatusGreen else StatusRed,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Server config
            Text("Server", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = host,
                onValueChange = { host = it },
                label = { Text("Host") },
                placeholder = { Text("127.0.0.1") },
                leadingIcon = { Icon(Icons.Outlined.Dns, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = port,
                onValueChange = { port = it.filter { c -> c.isDigit() } },
                label = { Text("Port") },
                placeholder = { Text("8642") },
                leadingIcon = { Icon(Icons.Outlined.Tag, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key (optional)") },
                placeholder = { Text("sk-...") },
                leadingIcon = { Icon(Icons.Outlined.Key, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showApiKey = !showApiKey }) {
                        Icon(
                            if (showApiKey) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (showApiKey) "Hide" else "Show",
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
            )

            Spacer(Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        isChecking = true
                        onCheckConnection()
                        isChecking = false
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isChecking,
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Test Connection")
                }

                Button(
                    onClick = {
                        prefs.saveHost(host)
                        prefs.savePort(port.toIntOrNull() ?: 8642)
                        prefs.saveApiKey(apiKey)
                        isSaved = true
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Outlined.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save")
                }
            }

            // Saved confirmation
            if (isSaved) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Settings saved! Restart the app or go back to reconnect.",
                    style = MaterialTheme.typography.bodySmall,
                    color = StatusGreen,
                )
            }

            Spacer(Modifier.height(32.dp))

            // Info
            Text("How to use", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "1. Start Hermes in Termux with API server enabled:",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            "API_SERVER_ENABLED=true hermes gateway run",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "2. Set host to 127.0.0.1 and port 8642",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        "3. If you set an API key, add API_SERVER_KEY=... too",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "4. Tap Test Connection, then go back and chat!",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // App info
            Text(
                "Hermes AI v1.0.0 — Unofficial Android client for Hermes Agent",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            )
        }
    }
}
