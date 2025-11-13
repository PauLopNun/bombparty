package com.bombparty.presentation.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bombparty.R
import com.bombparty.domain.model.Player
import com.bombparty.presentation.theme.BombRed
import com.bombparty.utils.Config
import com.bombparty.presentation.theme.BombYellow
import com.bombparty.presentation.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    roomId: String,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val gameState = uiState.gameState

    // Don't connect again if already connected (shared ViewModel from CreateRoom/JoinRoom)
    LaunchedEffect(uiState.isConnected) {
        if (!uiState.isConnected) {
            // La URL del servidor se configura en utils/Config.kt
            // Cambia Config.IS_PRODUCTION = true cuando despliegues a producción
            viewModel.connectToServer(Config.SERVER_URL)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Room: $roomId",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
        if (gameState == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Players list
                PlayersRow(
                    players = gameState.players,
                    currentPlayerId = gameState.currentPlayer?.id
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bomb component
                BombComponent(
                    syllable = gameState.bombState?.currentSyllable ?: "",
                    timeRemaining = gameState.bombState?.timeRemaining ?: 0f,
                    maxTime = gameState.bombState?.maxTime ?: 30f
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bonus letters
                if (gameState.currentBonusLetters.isNotEmpty()) {
                    BonusLettersRow(
                        bonusLetters = gameState.currentBonusLetters,
                        currentPlayer = gameState.currentPlayer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                // Word input
                WordInput(
                    word = uiState.currentWord,
                    onWordChange = viewModel::updateWord,
                    onSubmit = {
                        gameState.currentPlayer?.let { player ->
                            viewModel.submitWord(roomId, player.id)
                        }
                    },
                    enabled = gameState.currentPlayer?.isCurrentTurn == true
                )

                // Message display
                uiState.lastMessage?.let { message ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun PlayersRow(
    players: List<Player>,
    currentPlayerId: String?
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(players) { player ->
            PlayerCard(
                player = player,
                isCurrentTurn = player.id == currentPlayerId
            )
        }
    }
}

@Composable
fun PlayerCard(
    player: Player,
    isCurrentTurn: Boolean
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !player.isAlive -> Color.Gray
                isCurrentTurn -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentTurn) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Player initial
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCurrentTurn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Player name
            Text(
                text = player.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            // Lives
            Text(
                text = "❤️ ${player.lives}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BombComponent(
    syllable: String,
    timeRemaining: Float,
    maxTime: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                timeRemaining < 3f -> BombRed
                timeRemaining < 7f -> Color(0xFFFF9800)
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "💣",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = syllable.uppercase(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = timeRemaining / maxTime,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = String.format("%.1fs", timeRemaining),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BonusLettersRow(
    bonusLetters: Set<Char>,
    currentPlayer: Player?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = stringResource(R.string.bonus_letters),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bonusLetters.forEach { letter ->
                    val used = currentPlayer?.bonusLettersUsed?.get(letter.toString()) ?: 0
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (used > 0) SuccessGreen else BombYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WordInput(
    word: String,
    onWordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = word,
            onValueChange = onWordChange,
            modifier = Modifier.weight(1f),
            label = { Text(stringResource(R.string.type_word)) },
            enabled = enabled,
            singleLine = true
        )

        IconButton(
            onClick = onSubmit,
            enabled = enabled && word.isNotBlank(),
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (enabled && word.isNotBlank())
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Submit",
                tint = if (enabled && word.isNotBlank())
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
