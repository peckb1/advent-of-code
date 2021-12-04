package me.peckb.aoc._2021.calendar.day04

import me.peckb.aoc._2021.calendar.day04.Board.BingoPosition
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject


class Day04 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    val WHITESPACE_REGEX = "\\s+".toRegex()
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs({ it }) {
    val data = it.toList()
    val moves = generateMoves(data.take(1).first())
    val boards = generateBoards(data.drop(2))

    val (winningBoard, finalNumber) = moves.firstNotNullOf { number ->
      boards.firstOrNull { board -> board.markNumber(number).isWinner() }
        ?.let { board -> Pair(board, number) }
    }

    winningBoard.score() * finalNumber
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs({ it }) {
    val data = it.toList()
    val moves = generateMoves(data.take(1).first())
    var boards = generateBoards(data.drop(2))

    val (lastWinningBoard, finalNumber) = moves.firstNotNullOf { number ->
      val (winningBoards, losingBoards) = boards.partition { board -> board.markNumber(number).isWinner() }

      if (losingBoards.isEmpty()) {
        Pair(winningBoards.last(), number)
      } else {
        null.also { boards = losingBoards }
      }
    }

    lastWinningBoard.score() * finalNumber
  }

  private fun generateMoves(moveList: String) = moveList.split(",").map { it.toInt() }

  private fun generateBoards(boardInput: List<String>) = boardInput
    .chunked(6)
    .map {
      Board(it.take(5).map { row -> row.toBingoPositions() })
    }

  private fun String.toBingoPositions() = this
    .trim()
    .split(WHITESPACE_REGEX)
    .map { BingoPosition(it.toInt()) }
}
