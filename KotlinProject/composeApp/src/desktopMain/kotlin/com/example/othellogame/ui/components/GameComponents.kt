package com.example.othellogame.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import com.example.othellogame.models.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin
import com.example.othellogame.ui.styles.GameStyles

@Composable
fun DiscPiece(
    disc: Disc,
    discStyle: DiscStyle,
    modifier: Modifier = Modifier
) {
    var shownDisc by remember { mutableStateOf(disc) }
    val rotationY = remember { Animatable(0f) }
    LaunchedEffect(disc) {
        if (disc != shownDisc) {
            rotationY.snapTo(0f)
            rotationY.animateTo(
                targetValue = 180f,
                animationSpec = tween(durationMillis = 200)
            ) {
                if (value >= 90f && shownDisc != disc) shownDisc = disc
            }
            rotationY.snapTo(0f) 
        }
    }
    Canvas(
        modifier = modifier.graphicsLayer {
            this.rotationY = rotationY.value     
            cameraDistance = 16 * density        
        }
    ) {
        val radius = size.minDimension * 0.4f
        val center = Offset(size.width / 2, size.height / 2)
        if (discStyle.shape == DiscShape.HEXAGON) {
            val path = Path().apply {
                for (j in 0 until 6) {
                    val angle = Math.toRadians((60 * j - 30).toDouble())
                    val x = center.x + cos(angle) * radius
                    val y = center.y + sin(angle) * radius
                    if (j == 0) moveTo(x.toFloat(), y.toFloat())
                    else lineTo(x.toFloat(), y.toFloat())
                }
                close()
            }
            drawPath(
                path  = path,
                color = if (disc == Disc.BLACK) discStyle.blackColor else discStyle.whiteColor
            )
        } else {
            drawCircle(
                color = if (disc == Disc.BLACK) discStyle.blackColor else discStyle.whiteColor,
                radius = radius,
                center = center
            )
        }
        if (disc == Disc.BLACK && discStyle.blackRingColor != Color.Transparent) {
            drawCircle(
                color = discStyle.blackRingColor.copy(alpha = 0.3f),
                radius = radius + 6f,
                center = center,
                style = Stroke(width = 6f)
            )
            drawCircle(
                color = discStyle.blackRingColor,
                radius = radius + 2f,
                center = center,
                style = Stroke(width = 2f)
            )
        } else if (disc == Disc.WHITE && discStyle.whiteRingColor != Color.Transparent) {
            drawCircle(
                color = discStyle.whiteRingColor.copy(alpha = 0.3f),
                radius = radius + 6f,
                center = center,
                style = Stroke(width = 6f)
            )
            drawCircle(
                color = discStyle.whiteRingColor,
                radius = radius + 2f,
                center = center,
                style = Stroke(width = 2f)
            )
        }
    }
}

@Composable
fun ValidMoveIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension * 0.2f
        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(
            color = color,
            radius = radius,
            center = center
        )
    }
}

@Composable
fun BoardGrid(
    board: OthelloBoard,
    cellSize: Dp,
    onCellClick: (Int, Int) -> Unit,
    discStyle: DiscStyle,
    boardStyle: BoardStyle,
    validMoves: List<Pair<Int, Int>> = emptyList()
) {
    val density = LocalDensity.current
    val cellSizePx = with(density) { cellSize.toPx() }

    Canvas(
        modifier = Modifier
            .size(
                width  = cellSize * board.size,
                height = cellSize * board.size
            )
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val x = (offset.x / cellSizePx).toInt()
                    val y = (offset.y / cellSizePx).toInt()
                    if (x in 0 until board.size && y in 0 until board.size) {
                        onCellClick(y, x)
                    }
                }
            }
    ) {
        if (boardStyle.gridGlow) {
            for (i in 0..board.size) {
                drawLine(
                    color       = boardStyle.gridGlowColor.copy(alpha = 0.3f),
                    start       = Offset(i * cellSizePx, 0f),
                    end         = Offset(i * cellSizePx, size.height),
                    strokeWidth = 6f,
                    cap         = StrokeCap.Round
                )
                drawLine(
                    color       = boardStyle.gridGlowColor.copy(alpha = 0.3f),
                    start       = Offset(0f, i * cellSizePx),
                    end         = Offset(size.width, i * cellSizePx),
                    strokeWidth = 6f,
                    cap         = StrokeCap.Round
                )
            }
        }
        for (i in 0..board.size) {
            drawLine(
                color = boardStyle.gridColor,
                start = Offset(i * cellSizePx, 0f),
                end = Offset(i * cellSizePx, size.height),
                strokeWidth = 2f
            )
            drawLine(
                color = boardStyle.gridColor,
                start = Offset(0f, i * cellSizePx),
                end = Offset(size.width, i * cellSizePx),
                strokeWidth = 2f
            )
        }
    }

    for ((row, col) in validMoves) {
        ValidMoveIndicator(
            color = boardStyle.validMoveColor,
            modifier = Modifier
                .size(width = cellSize, height = cellSize)
                .offset(
                    x = cellSize * col,
                    y = cellSize * row
                )
        )
    }

    for (row in 0 until board.size) {
        for (col in 0 until board.size) {
            val disc = board.getDisc(row, col)
            if (disc != Disc.EMPTY) {
                DiscPiece(
                    disc = disc,
                    discStyle = discStyle,
                    modifier = Modifier
                        .size(width = cellSize, height = cellSize)
                        .offset(
                            x = cellSize * col,
                            y = cellSize * row
                        )
                )
            }
        }
    }
}

@Composable
fun StyleSettings(
    currentStyle: GameStyle,
    onStyleSelected: (GameStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    val style = GameStyles.getStyle(currentStyle)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "スタイル設定",
            style = MaterialTheme.typography.h6,
            color = style.boardStyle.buttonColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // ノーマルスタイル
            Button(
                onClick = { onStyleSelected(GameStyle.NORMAL) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentStyle == GameStyle.NORMAL) 
                        Color(0xFF26A65B) else Color.Gray
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ノーマル", color = Color.White)
                    Text("クラシックなデザイン", 
                        style = MaterialTheme.typography.caption,
                        color = Color.White
                    )
                }
            }

            // フューチャリスティックスタイル
            Button(
                onClick = { onStyleSelected(GameStyle.FUTURISTIC) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentStyle == GameStyle.FUTURISTIC) 
                        Color(0xFF555555) else Color.Gray
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("フューチャリスティック", color = Color.White)
                    Text("近未来的なデザイン", 
                        style = MaterialTheme.typography.caption,
                        color = Color.White
                    )
                }
            }

            // 和モダンスタイル
            Button(
                onClick = { onStyleSelected(GameStyle.JAPANESE_MODERN) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (currentStyle == GameStyle.JAPANESE_MODERN) 
                        Color(0xFF5D4037) else Color.Gray
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("和モダン", color = Color.White)
                    Text("伝統的な和風デザイン", 
                        style = MaterialTheme.typography.caption,
                        color = Color.White
                    )
                }
            }
        }
    }
} 