package me.peckb.aoc._2021.calendar.day04

import me.peckb.aoc._2021.calendar.day04.Board.BingoPosition
import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject


class Day04 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    val WHITESPACE_REGEX = "\\s+".toRegex()
    const val ACTION_LINES = 1
    const val SPACER_LINES = 1
    const val BOARD_SIZE = 5
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs({ it }) { input ->
    val data = input.toList()
    val numbers = generateNumbers(data.take(ACTION_LINES))
    val boards = generateBoards(data.drop(ACTION_LINES + SPACER_LINES))

    val (winningBoard, finalNumber) = numbers.firstNotNullOf { number ->
      boards.firstOrNull { it.markNumber(number).isWinner() }?.let { Pair(it, number) }
    }

    winningBoard.score() * finalNumber
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs({ it }) { input ->
    val data = input.toList()
    val numbers = generateNumbers(data.take(ACTION_LINES))
    var boards = generateBoards(data.drop(ACTION_LINES + SPACER_LINES))

    val (lastWinningBoard, finalNumber) = numbers.firstNotNullOf { number ->
      val (winningBoards, losingBoards) = boards.partition { it.markNumber(number).isWinner() }

      if (losingBoards.isEmpty()) {
        Pair(winningBoards.last(), number)
      } else {
        null.also { boards = losingBoards }
      }
    }

    lastWinningBoard.score() * finalNumber
  }

  private fun generateNumbers(numbersList: List<String>) = numbersList.flatMap { numberLine ->
    numberLine.split(",").map { it.toInt() }
  }

  private fun generateBoards(boardInput: List<String>) = boardInput
    .chunked(BOARD_SIZE + SPACER_LINES)
    .map { Board(it.mapNotNull(::toBingoPositions)) }

  private fun toBingoPositions(line: String) = line
    .trim()
    .takeUnless { it.isEmpty() }
    ?.split(WHITESPACE_REGEX)
    ?.map { BingoPosition(it.toInt()) }
}
