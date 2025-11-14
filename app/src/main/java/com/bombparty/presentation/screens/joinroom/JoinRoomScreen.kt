package com.bombparty.presentation.screens.joinroom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bombparty.domain.model.Avatar
import com.bombparty.domain.model.AvatarOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinRoomScreen(
    onNavigateBack: () -> Unit,
    onJoinRoom: (roomId: String, playerName: String, avatar: String) -> Unit,
    savedPlayerName: String = "",
    savedAvatar: String = "😀"
) {
    var roomId by remember { mutableStateOf("") }
    var playerName by remember { mutableStateOf(savedPlayerName) }
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

            Spacer(modifier = Modifier.height(24.dp))

            // Show current profile
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar preview
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = savedAvatar,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (playerName.isNotBlank()) playerName else "Sin nombre",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Edita tu perfil desde el menú principal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Join Button
            Button(
                onClick = {
                    if (roomId.isNotBlank()) {
                        isJoining = true
                        onJoinRoom(roomId.trim(), playerName.ifBlank { "Jugador" }, savedAvatar)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = roomId.isNotBlank() && !isJoining
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
