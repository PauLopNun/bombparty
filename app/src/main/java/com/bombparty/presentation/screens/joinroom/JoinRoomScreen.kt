package com.bombparty.presentation.screens.joinroom

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinRoomScreen(
    onNavigateBack: () -> Unit,
    onJoinRoom: (roomId: String, playerName: String) -> Unit
) {
    var roomId by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf("") }
    var isJoining by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Unirse a Sala",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Instructions
            Text(
                text = "💣",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ingresa el código de la sala a la que quieres unirte",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Room ID Input
            OutlinedTextField(
                value = roomId,
                onValueChange = { roomId = it.uppercase() },
                label = { Text("Código de Sala") },
                placeholder = { Text("Ej: ABC123") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isJoining
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Player Name Input
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Tu nombre") },
                placeholder = { Text("Nombre de jugador") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isJoining
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Join Button
            Button(
                onClick = {
                    if (roomId.isNotBlank() && playerName.isNotBlank()) {
                        isJoining = true
                        onJoinRoom(roomId.trim(), playerName.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = roomId.isNotBlank() && playerName.isNotBlank() && !isJoining
            ) {
                if (isJoining) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Unirse",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel Button
            TextButton(
                onClick = onNavigateBack,
                enabled = !isJoining
            ) {
                Text("Cancelar")
            }
        }
    }
}
