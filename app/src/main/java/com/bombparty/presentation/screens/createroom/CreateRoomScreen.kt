package com.bombparty.presentation.screens.createroom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bombparty.domain.model.GameConfig
import com.bombparty.domain.model.GameLanguage
import com.bombparty.domain.model.SyllableDifficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomScreen(
    onNavigateBack: () -> Unit,
    onRoomCreated: (roomId: String, playerName: String, config: GameConfig) -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var language by remember { mutableStateOf(GameLanguage.SPANISH) }
    var difficulty by remember { mutableStateOf(SyllableDifficulty.BEGINNER) }
    var initialLives by remember { mutableStateOf(2) }
    var turnDuration by remember { mutableStateOf(8) }
    var maxPlayers by remember { mutableStateOf(8) }
    var isCreating by remember { mutableStateOf(false) }

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
            // Player Name
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Tu nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isCreating
            )

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
                        enabled = !isCreating
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
                        enabled = !isCreating
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
                        enabled = !isCreating && initialLives > 1
                    ) {
                        Text("-")
                    }
                    FilledTonalButton(
                        onClick = { if (initialLives < 5) initialLives++ },
                        enabled = !isCreating && initialLives < 5
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
                        enabled = !isCreating && turnDuration > 5
                    ) {
                        Text("-")
                    }
                    FilledTonalButton(
                        onClick = { if (turnDuration < 15) turnDuration++ },
                        enabled = !isCreating && turnDuration < 15
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
                        enabled = !isCreating && maxPlayers > 2
                    ) {
                        Text("-")
                    }
                    FilledTonalButton(
                        onClick = { if (maxPlayers < 16) maxPlayers++ },
                        enabled = !isCreating && maxPlayers < 16
                    ) {
                        Text("+")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Create Room Button
            Button(
                onClick = {
                    if (playerName.isNotBlank()) {
                        isCreating = true
                        val config = GameConfig(
                            language = language,
                            syllableDifficulty = difficulty,
                            initialLives = initialLives,
                            minTurnDuration = turnDuration,
                            maxPlayers = maxPlayers
                        )
                        // Generate a simple room ID for now (server will provide the real one)
                        onRoomCreated("", playerName, config)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = playerName.isNotBlank() && !isCreating
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Crear Sala",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}
