package com.example.othellogame.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class OthelloBoard {
    private val grid: MutableState<Array<Array<Disc>>> = mutableStateOf(Array(8) { Array(8) { Disc.EMPTY } })

    companion object {
        const val BOARD_SIZE = 8
    }
    val size: Int
        get() = BOARD_SIZE

    fun reset() {
        val newGrid = Array(BOARD_SIZE) { Array(BOARD_SIZE) { Disc.EMPTY } }
        // 初期配置
        newGrid[3][3] = Disc.WHITE
        newGrid[3][4] = Disc.BLACK
        newGrid[4][3] = Disc.BLACK
        newGrid[4][4] = Disc.WHITE
        grid.value = newGrid
    }

    fun getDisc(row: Int, col: Int): Disc = grid.value[row][col]

    fun placeDisc(row: Int, col: Int, disc: Disc) {
        if (!isValidMove(row, col, disc)) return
        val newGrid = grid.value.map { it.clone() }.toTypedArray()
        newGrid[row][col] = disc
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                flipDiscs(newGrid, row, col, dr, dc, disc)
            }
        }
        grid.value = newGrid
    }

    private fun flipDiscs(grid: Array<Array<Disc>>, row: Int, col: Int, dr: Int, dc: Int, disc: Disc) {
        var r = row + dr
        var c = col + dc
        val discsToFlip = mutableListOf<Pair<Int, Int>>()
        while (r in 0..7 && c in 0..7) {
            when (grid[r][c]) {
                disc -> {
                    for ((flipRow, flipCol) in discsToFlip) {
                        grid[flipRow][flipCol] = disc
                    }
                    break
                }
                disc.opposite -> {
                    discsToFlip.add(Pair(r, c))
                }
                else -> break
            }
            r += dr
            c += dc
        }
    }

    fun isValidMove(row: Int, col: Int, disc: Disc): Boolean {
        if (row !in 0..7 || col !in 0..7 || grid.value[row][col] != Disc.EMPTY) {
            return false
        }
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                if (canFlip(row, col, dr, dc, disc)) {
                    return true
                }
            }
        }
        return false
    }

    private fun canFlip(row: Int, col: Int, dr: Int, dc: Int, disc: Disc): Boolean {
        var r = row + dr
        var c = col + dc
        var foundOpponent = false
        while (r in 0..7 && c in 0..7) {
            when (grid.value[r][c]) {
                disc -> return foundOpponent
                disc.opposite -> foundOpponent = true
                else -> return false
            }
            r += dr
            c += dc
        }
        return false
    }

    fun getValidMoves(disc: Disc): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        for (row in 0..7) {
            for (col in 0..7) {
                if (isValidMove(row, col, disc)) {
                    moves.add(Pair(row, col))
                }
            }
        }
        return moves
    }

    fun countDiscs(): Pair<Int, Int> {
        var blackCount = 0
        var whiteCount = 0
        for (row in 0..7) {
            for (col in 0..7) {
                when (grid.value[row][col]) {
                    Disc.BLACK -> blackCount++
                    Disc.WHITE -> whiteCount++
                    else -> {}
                }
            }
        }
        return Pair(blackCount, whiteCount)
    }

    fun clone(): OthelloBoard {
        val newBoard = OthelloBoard()
        newBoard.grid.value = grid.value.map { it.clone() }.toTypedArray()
        return newBoard
    }
} 