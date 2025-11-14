package com.bombparty.presentation.screens.createroom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bombparty.domain.model.Avatar
import com.bombparty.domain.model.AvatarOptions
import com.bombparty.domain.model.GameConfig
import com.bombparty.domain.model.GameLanguage
import com.bombparty.domain.model.SyllableDifficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomScreen(
    onNavigateBack: () -> Unit,
    onRoomCreated: (roomId: String, playerName: String, avatar: String, config: GameConfig) -> Unit,
    isLoading: Boolean = false,
    isConnected: Boolean = false,
    error: String? = null
) {
    var playerName by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(AvatarOptions.HAPPY) }
    var language by remember { mutableStateOf(GameLanguage.SPANISH) }
    var difficulty by remember { mutableStateOf(SyllableDifficulty.BEGINNER) }
    var initialLives by remember { mutableStateOf(2) }
    var turnDuration by remember { mutableStateOf(8) }
    var maxPlayers by remember { mutableStateOf(8) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Crear Sala",
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Connection status
            if (!isConnected && !isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "⚠️ Conectando al servidor...",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Error message
            error?.let { errorMsg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "❌ $errorMsg",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Player Name
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Tu nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar Selection
            Text(
                text = "Elige tu avatar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                items(AvatarOptions.ALL) { avatar ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(
                                if (selectedAvatar == avatar)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (selectedAvatar == avatar) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable(enabled = !isLoading) { selectedAvatar = avatar },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatar.emoji,
                            style = MaterialTheme.typography.displaySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Language Selection
            Text(
                text = "Idioma",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameLanguage.entries.forEach { lang ->
                    FilterChip(
                        selected = language == lang,
                        onClick = { language = lang },
                        label = {
                            Text(
                                when (lang) {
                                    GameLanguage.SPANISH -> "Español"
                                    GameLanguage.ENGLISH -> "English"
                                    GameLanguage.BOTH -> "Ambos"
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Difficulty Selection
            Text(
                text = "Dificultad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SyllableDifficulty.entries.filter { it != SyllableDifficulty.CUSTOM }.forEach { diff ->
                    FilterChip(
                        selected = difficulty == diff,
                        onClick = { difficulty = diff },
                        label = {
                            Text(
                                when (diff) {
                                    SyllableDifficulty.BEGINNER -> "Principiante"
                                    SyllableDifficulty.INTERMEDIATE -> "Intermedio"
                                    SyllableDifficulty.ADVANCED -> "Avanzado"
                                    else -> ""
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Game Settings
            Text(
                text = "Configuración del juego",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Initial Lives
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Vidas iniciales: $initialLives")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { if (initialLives > 1) initialLives-- },
                        enabled = !isLoading && initialLives > 1
                    ) {
                        Text("-")
                    }
                    FilledTonalButton(
                        onClick = { if (initialLives < 5) initialLives++ },
                        enabled = !isLoading && initialLives < 5
                    ) {
                        Text("+")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Turn Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Duración turno: ${turnDuration}s")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { if (turnDuration > 5) turnDuration-- },
                        enabled = !isLoading && turnDuration > 5
                    ) {
                        Text("-")
                    }
                    FilledTonalButton(
                        onClick = { if (turnDuration < 15) turnDuration++ },
                        enabled = !isLoading && turnDuration < 15
                    ) {
                        Text("+")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Max Players
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Jugadores máx: $maxPlayers")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { if (maxPlayers > 2) maxPlayers-- },
                        enabled = !isLoading && maxPlayers > 2
                    ) {
                        Text("-")
                    }
                    FilledTonalButton(
                        onClick = { if (maxPlayers < 16) maxPlayers++ },
                        enabled = !isLoading && maxPlayers < 16
                    ) {
                        Text("+")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Create Room Button
            Button(
                onClick = {
                    if (playerName.isNotBlank() && isConnected) {
                        val config = GameConfig(
                            language = language,
                            syllableDifficulty = difficulty,
                            initialLives = initialLives,
                            minTurnDuration = turnDuration,
                            maxPlayers = maxPlayers
                        )
                        onRoomCreated("", playerName, selectedAvatar.emoji, config)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = playerName.isNotBlank() && isConnected && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (isConnected) "Crear Sala" else "Conectando...",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}
