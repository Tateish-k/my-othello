package com.example.othellogame.models

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

enum class Disc {
    BLACK, WHITE, EMPTY;
    val opposite: Disc
        get() = when (this) {
            BLACK -> WHITE
            WHITE -> BLACK
            EMPTY -> EMPTY
        }
}

data class OthelloGameState(
    val board: OthelloBoard,
    val currentPlayer: Disc,
    val discCounts: Pair<Int, Int>,  // (black, white)
    val validMoves: List<Pair<Int, Int>>,
    val gameOver: Boolean,
    val winnerMessage: String,
    val passCount: Int
)

enum class GameStyle {
    NORMAL,
    FUTURISTIC,
    JAPANESE_MODERN
}

data class BoardStyle(
    val backgroundColor: Color,
    val gridColor: Color,
    val gridGlow: Boolean = false,
    val gridGlowColor: Color = Color.Transparent,
    val textColor: Color = Color.Black,
    val buttonColor: Color = Color(0xFF27AE60),
    val buttonTextColor: Color = Color.White,
    val validMoveColor: Color = Color(0x40000000)
)

data class DiscStyle(
    val blackColor: Color,
    val whiteColor: Color,
    val blackRingColor: Color = Color.Transparent,
    val whiteRingColor: Color = Color.Transparent,
    val isTransparent: Boolean = false,
    val shape: DiscShape = DiscShape.CIRCLE
)

enum class DiscShape {
    CIRCLE,
    HEXAGON
}

data class GameStyleData(
    val boardStyle: BoardStyle,
    val discStyle: DiscStyle
) 