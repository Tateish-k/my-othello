package com.example.othellogame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.othellogame.ai.AIPlayer
import com.example.othellogame.models.*
import com.example.othellogame.ui.components.BoardGrid
import com.example.othellogame.ui.components.StyleSettings
import com.example.othellogame.ui.styles.GameStyles
import kotlinx.coroutines.delay

private val V_SPACING: Dp   = 24.dp   // 縦間隔
private val H_SPACING: Dp   = 16.dp   // 横間隔
private val DIFF_BTN_W: Dp  = 120.dp  // 難易度ボタン横幅
private val DIFF_BTN_H: Dp  = 56.dp   // 難易度ボタン高さ
private val BTN_HEIGHT: Dp  = 56.dp   // 汎用ボタン高さ
private val CELL_SIZE: Dp   = 60.dp   // 盤セルサイズ

@Composable
fun StartScreen(
    onStartGame: () -> Unit,
    gameStyle: GameStyle,
    onStyleSelected: (GameStyle) -> Unit,
    isVsAI: Boolean,
    onGameModeSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val style = GameStyles.getStyle(gameStyle)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(style.boardStyle.backgroundColor)
            .then(if (gameStyle == GameStyle.FUTURISTIC)
                Modifier.border(4.dp, style.boardStyle.buttonColor)
            else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(V_SPACING)
        ) {
            Text("オセロゲーム",
                style = MaterialTheme.typography.h3,
                color = style.boardStyle.textColor
            )

            StyleSettings(
                currentStyle = gameStyle,
                onStyleSelected = onStyleSelected,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(H_SPACING),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onGameModeSelected(true) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isVsAI) style.boardStyle.buttonColor else Color.Gray,
                        contentColor    = style.boardStyle.buttonTextColor
                    ),
                    modifier = Modifier.height(BTN_HEIGHT)
                ) { Text("AI 対戦", style = MaterialTheme.typography.h6) }

                Button(
                    onClick = { onGameModeSelected(false) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (!isVsAI) style.boardStyle.buttonColor else Color.Gray,
                        contentColor    = style.boardStyle.buttonTextColor
                    ),
                    modifier = Modifier.height(BTN_HEIGHT)
                ) { Text("対人戦", style = MaterialTheme.typography.h6) }
            }

            Button(
                onClick  = onStartGame,
                colors   = ButtonDefaults.buttonColors(
                    backgroundColor = style.boardStyle.buttonColor,
                    contentColor    = style.boardStyle.buttonTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(BTN_HEIGHT)
            ) { Text("ゲーム開始", style = MaterialTheme.typography.h6) }
        }
    }
}

@Composable
fun OthelloGameScreen(
    gameState: OthelloGameState,
    onCellClick: (Int, Int) -> Unit,
    onNewGame: () -> Unit,
    gameStyle: GameStyle,
    aiPlayer: AIPlayer,
    isAiEnabled: Boolean
) {
    val style = GameStyles.getStyle(gameStyle)
    LaunchedEffect(gameState.currentPlayer, isAiEnabled) {
        if (isAiEnabled && gameState.currentPlayer == Disc.WHITE && !gameState.gameOver) {
            delay(500) 
            val validMoves = gameState.validMoves
            if (validMoves.isNotEmpty()) {
                val bestMove = aiPlayer.findBestMove(gameState.board, validMoves)
                if (bestMove.first >= 0 && bestMove.second >= 0) {
                    onCellClick(bestMove.first, bestMove.second)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(style.boardStyle.backgroundColor)
            .padding(all = V_SPACING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(V_SPACING)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = H_SPACING),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = onNewGame,           
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = style.boardStyle.buttonColor,
                    contentColor    = style.boardStyle.buttonTextColor
                ),
                modifier = Modifier.height(BTN_HEIGHT)
            ) { Text("タイトルへ") }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = H_SPACING),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "黒: ${gameState.discCounts.first}",
                    style = MaterialTheme.typography.h6,
                    color = style.boardStyle.textColor
                )
            }

            Text(
                text = if (gameState.gameOver) {
                    gameState.winnerMessage
                } else {
                    "現在の手番: ${if (gameState.currentPlayer == Disc.BLACK) "黒" else "白"}"
                },
                style = MaterialTheme.typography.h6,
                color = style.boardStyle.textColor
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "白: ${gameState.discCounts.second}",
                    style = MaterialTheme.typography.h6,
                    color = style.boardStyle.textColor
                )
            }
        }

        Text(
            text = "有効手数: ${gameState.validMoves.size}",
            style = MaterialTheme.typography.subtitle1,
            color = style.boardStyle.textColor,
            modifier = Modifier.padding(bottom = V_SPACING)
        )

        Box(
            modifier = Modifier
                .size(CELL_SIZE * OthelloBoard.BOARD_SIZE)
                .background(style.boardStyle.backgroundColor)
                .border(2.dp, style.boardStyle.gridColor)
        ) {
            BoardGrid(
                board = gameState.board,
                cellSize = CELL_SIZE,
                onCellClick = onCellClick,
                discStyle = style.discStyle,
                boardStyle = style.boardStyle,
                validMoves = gameState.validMoves
            )
            if (isAiEnabled && gameState.currentPlayer == Disc.WHITE && !gameState.gameOver) {
                Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = style.boardStyle.buttonColor,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AIが考え中...",
                            style = MaterialTheme.typography.subtitle1,
                            color = style.boardStyle.textColor
                        )
                    }
                }
            }
        }

        if (gameState.gameOver) {
            Text(
                text = gameState.winnerMessage,
                style = MaterialTheme.typography.h4,
                color = style.boardStyle.textColor,
                modifier = Modifier.padding(vertical = V_SPACING)
            )
        }

        if (gameState.gameOver) {
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = style.boardStyle.buttonColor,
                    contentColor = style.boardStyle.buttonTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(BTN_HEIGHT)
            ) { Text("新しいゲーム", style = MaterialTheme.typography.h6) }
        }
    }
}

@Composable
fun DifficultyScreen(
    currentDifficulty: Int,
    onDifficultyChange: (Int) -> Unit,
    onStartGame: () -> Unit,
    gameStyle: GameStyle,
    modifier: Modifier = Modifier
) {
    val style = GameStyles.getStyle(gameStyle)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(style.boardStyle.backgroundColor)
            .padding(horizontal = V_SPACING),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(V_SPACING)
        ) {
            Text("難易度を選択してください",
                style = MaterialTheme.typography.h4,
                color = style.boardStyle.textColor
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(V_SPACING)
            ) {
                (1..6).chunked(2).forEach { rowLevels ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(H_SPACING, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowLevels.forEach { level ->
                            Button(
                                onClick = { onDifficultyChange(level) },
                                colors  = ButtonDefaults.buttonColors(
                                    backgroundColor = if (currentDifficulty == level)
                                        style.boardStyle.buttonColor else Color.Gray,
                                    contentColor   = style.boardStyle.buttonTextColor
                                ),
                                modifier = Modifier
                                    .width(DIFF_BTN_W)
                                    .height(DIFF_BTN_H)
                            ) {
                                Text(level.toString(), style = MaterialTheme.typography.h6)
                            }
                        }
                        if (rowLevels.size == 1) {
                            Spacer(
                                modifier = Modifier
                                    .width(DIFF_BTN_W)
                                    .height(DIFF_BTN_H)
                            )
                        }
                    }
                }
            }

            Button(
                onClick  = onStartGame,
                colors   = ButtonDefaults.buttonColors(
                    backgroundColor = style.boardStyle.buttonColor,
                    contentColor    = style.boardStyle.buttonTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(BTN_HEIGHT)
            ) { Text("ゲーム開始", style = MaterialTheme.typography.h6) }
        }
    }
} 