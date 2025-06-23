package com.example.othellogame.ai

import com.example.othellogame.models.Disc
import com.example.othellogame.models.OthelloBoard
import com.example.othellogame.models.GameStyleData

class AIPlayer(private val searchDepth: Int) {
    // 評価テーブル
    private val evaluationTable = arrayOf(
        intArrayOf(2714,  147,   69,  -18,  -18,   69,  147, 2714),
        intArrayOf( 147, -577, -186, -153, -153, -186, -577,  147),
        intArrayOf(  69, -186, -379, -122, -122, -379, -186,   69),
        intArrayOf( -18, -153, -122, -169, -169, -122, -153,  -18),
        intArrayOf( -18, -153, -122, -169, -169, -122, -153,  -18),
        intArrayOf(  69, -186, -379, -122, -122, -379, -186,   69),
        intArrayOf( 147, -577, -186, -153, -153, -186, -577,  147),
        intArrayOf(2714,  147,   69,  -18,  -18,   69,  147, 2714)
    )

    // 危険な位置（角の隣）
    private val dangerPositions = setOf(
        Pair(0, 1), Pair(1, 0), Pair(1, 1),
        Pair(0, 6), Pair(1, 6), Pair(1, 7),
        Pair(6, 0), Pair(6, 1), Pair(7, 1),
        Pair(6, 6), Pair(6, 7), Pair(7, 6)
    )

    fun findBestMove(board: OthelloBoard, validMoves: List<Pair<Int, Int>>): Pair<Int, Int> {
        if (validMoves.isEmpty()) return Pair(-1, -1)
        val orderedMoves = validMoves.sortedByDescending { (r, c) ->
            val tmp = board.clone()
            tmp.placeDisc(r, c, Disc.WHITE)
            evaluateBoard(tmp)                         
        }

        var bestScore = Int.MIN_VALUE
        var bestMove  = orderedMoves.first()

        for ((row, col) in orderedMoves) {
            val newBoard = board.clone()
            newBoard.placeDisc(row, col, Disc.WHITE)

            val score = alphaBeta(
                board      = newBoard,
                depth      = searchDepth - 1,
                alpha      = Int.MIN_VALUE,
                beta       = Int.MAX_VALUE,
                isMaximizing = false              
            )

            if (score > bestScore) {
                bestScore = score
                bestMove  = Pair(row, col)
            }
        }
        return bestMove
    }

    private fun alphaBeta(
        board: OthelloBoard,
        depth: Int,
        alpha: Int,
        beta: Int,
        isMaximizing: Boolean
    ): Int {
        if (depth == 0) return evaluateBoard(board)
        val currentPlayer = if (isMaximizing) Disc.WHITE else Disc.BLACK
        val moves = board.getValidMoves(currentPlayer)
        if (moves.isEmpty()) return evaluateBoard(board)
        var a = alpha
        var b = beta
        val orderedMoves = moves.sortedByDescending { (r, c) ->
            val tmp = board.clone()
            tmp.placeDisc(r, c, currentPlayer)
            val score = evaluateBoard(tmp)
            if (isMaximizing) score else -score     
        }
        if (isMaximizing) {
            var value = Int.MIN_VALUE
            for ((r, c) in orderedMoves) {
                val newBoard = board.clone()
                newBoard.placeDisc(r, c, currentPlayer)
                value = maxOf(
                    value,
                    alphaBeta(newBoard, depth - 1, a, b, false)
                )
                a = maxOf(a, value)
                if (a >= b) break               
            }
            return value
        } else {
            var value = Int.MAX_VALUE
            for ((r, c) in orderedMoves) {
                val newBoard = board.clone()
                newBoard.placeDisc(r, c, currentPlayer)
                value = minOf(
                    value,
                    alphaBeta(newBoard, depth - 1, a, b, true)
                )
                b = minOf(b, value)
                if (a >= b) break              
            }
            return value
        }
    }

    private fun evaluateBoard(board: OthelloBoard): Int {
        var score = 0
        val (blackCount, whiteCount) = board.countDiscs()
        score += (whiteCount - blackCount) * 10
        for (i in 0 until 8) {
            for (j in 0 until 8) {
                when (board.getDisc(i, j)) {
                    Disc.WHITE -> score += evaluationTable[i][j]
                    Disc.BLACK -> score -= evaluationTable[i][j]
                    else -> {}
                }
            }
        }

        for ((row, col) in dangerPositions) {
            when (board.getDisc(row, col)) {
                Disc.WHITE -> score -= 20
                Disc.BLACK -> score += 20
                else -> {}
            }
        }

        return score
    }
}