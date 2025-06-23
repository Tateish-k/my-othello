package com.example.othellogame

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.othellogame.ai.AIPlayer
import com.example.othellogame.models.*
import com.example.othellogame.ui.screens.OthelloGameScreen
import com.example.othellogame.ui.screens.StartScreen
import com.example.othellogame.ui.screens.DifficultyScreen

enum class Screen { START, DIFFICULTY, GAME }

fun main() = application {
    val windowState = rememberWindowState(width = 800.dp, height = 600.dp)
    var currentScreen    by remember { mutableStateOf(Screen.START) }
    var gameStyle        by remember { mutableStateOf(GameStyle.NORMAL) }
    var currentDifficulty by remember { mutableStateOf(4) }
    var isVsAI           by remember { mutableStateOf(true) }

    val aiPlayer = remember(currentDifficulty) { AIPlayer(searchDepth = currentDifficulty) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "オセロゲーム",
        state = windowState
    ) {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {

                when (currentScreen) {
                    Screen.START -> {
                        StartScreen(
                            onStartGame     = {
                                currentScreen = if (isVsAI) Screen.DIFFICULTY else Screen.GAME
                            },
                            gameStyle       = gameStyle,
                            onStyleSelected = { gameStyle = it },
                            isVsAI          = isVsAI,
                            onGameModeSelected = { selected -> isVsAI = selected }
                        )
                    }

                    Screen.DIFFICULTY -> {
                        DifficultyScreen(
                            currentDifficulty  = currentDifficulty,
                            onDifficultyChange = { level -> currentDifficulty = level },
                            onStartGame        = { currentScreen = Screen.GAME },
                            gameStyle          = gameStyle
                        )
                    }

                    Screen.GAME -> {
                        var gameState by remember {
                            val initialBoard = OthelloBoard().apply { reset() }
                            mutableStateOf(
                                OthelloGameState(
                                    board         = initialBoard,
                                    currentPlayer = Disc.BLACK,
                                    discCounts    = Pair(2, 2),
                                    validMoves    = initialBoard.getValidMoves(Disc.BLACK),
                                    gameOver      = false,
                                    winnerMessage = "",
                                    passCount     = 0
                                )
                            )
                        }

                        OthelloGameScreen(
                            gameState  = gameState,
                            onCellClick = { row, col ->
                                if (!gameState.gameOver &&
                                    gameState.board.isValidMove(row, col, gameState.currentPlayer)
                                ) {
                                    val newBoard = gameState.board.clone()
                                    newBoard.placeDisc(row, col, gameState.currentPlayer)
                                    val (blackCount, whiteCount) = newBoard.countDiscs()
                                    var nextPlayer   = gameState.currentPlayer.opposite
                                    var validMoves   = newBoard.getValidMoves(nextPlayer)
                                    var newPassCount = gameState.passCount
                                    while (validMoves.isEmpty()) {
                                        newPassCount += 1
                                        if (newPassCount >= 2) break         
                                        nextPlayer = nextPlayer.opposite       
                                        validMoves = newBoard.getValidMoves(nextPlayer)
                                    }

                                    val totalDiscs     = blackCount + whiteCount
                                    val isBoardFull    = totalDiscs == OthelloBoard.BOARD_SIZE * OthelloBoard.BOARD_SIZE
                                    val gameIsOver     = newPassCount >= 2 || isBoardFull
                                    val winnerMessage  = if (gameIsOver) {
                                        when {
                                            blackCount > whiteCount -> "黒の勝ち！"
                                            whiteCount > blackCount -> "白の勝ち！"
                                            else                    -> "引き分け！"
                                        }
                                    } else ""

                                    gameState = gameState.copy(
                                        board         = newBoard,
                                        currentPlayer = if (gameIsOver) gameState.currentPlayer else nextPlayer,
                                        discCounts    = Pair(blackCount, whiteCount),
                                        validMoves    = if (gameIsOver) emptyList() else validMoves,
                                        passCount     = newPassCount,
                                        gameOver      = gameIsOver,
            winnerMessage = winnerMessage
        )
    }
                            },
                            onNewGame  = { currentScreen = Screen.START },
                            gameStyle  = gameStyle,
                            aiPlayer   = aiPlayer,
                            isAiEnabled = isVsAI
                        )
                    }
                }
            }
        }
    }
}
