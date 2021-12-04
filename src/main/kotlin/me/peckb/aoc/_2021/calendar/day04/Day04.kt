package me.peckb.aoc._2021.calendar.day04

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject


class Day04 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).readAs({ it }) { input ->
    val data = input.toList()

    val moves = data[0].split(",").map { number -> number.toInt() }
    val boards = data.drop(2)
      .chunked(6)
      .map { board ->
        board.take(5).map { boardRow ->
          boardRow.trim().split("\\s+".toRegex()).map {
            BingoPosition(it.toInt())
          }
        }.toBoard()
      }


    val (winningBoard, finalNumber) = moves.firstNotNullOf { number ->
      boards.firstOrNull { board -> board.markNumber(number).isWinner() }
        ?.let { Pair(it, number) }
    }

    winningBoard.score() * finalNumber
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).readAs({ it }) { input ->
    val data = input.toList()

    val moves = data[0].split(",").map { number -> number.toInt() }
    var boards = data.drop(2)
      .chunked(6)
      .map { board ->
        board.take(5).map { boardRow ->
          boardRow.trim().split("\\s+".toRegex()).map {
            BingoPosition(it.toInt())
          }
        }.toBoard()
      }

    val (lastWinningBoard, finalNumber) = moves.firstNotNullOf { number ->
      val (winningBoards, losingBoards) = boards.partition { board ->
        board.markNumber(number)
        board.isWinner()
      }

      if (losingBoards.isEmpty()) {
        Pair(winningBoards.last(), number)
      } else {
        boards = losingBoards
        null
      }
    }

    lastWinningBoard.score() * finalNumber
  }

  data class BingoPosition(val number: Int, private var marked: Boolean = false) {
    fun mark() { marked = true }
    fun isMarked() = marked
  }

  private data class Board(val values: List<List<BingoPosition>>) {
    private val positionMap = mutableMapOf<Int, MutableList<BingoPosition>>()

    init {
      values.forEach { row ->
        row.forEach { item ->
          positionMap.computeIfAbsent(item.number) { mutableListOf() }
          positionMap[item.number]?.add(item)
        }
      }
    }

    fun markNumber(number: Int) = this.apply {
      positionMap[number]?.forEach { it.mark() }
    }

    fun isWinner(): Boolean {
      val rowBingo by lazy {
        values.any { row -> row.all { it.isMarked() } }
      }
      val columnBingo by lazy {
        values.indices.any { column -> values.all { row -> row[column].isMarked() } }
      }

      return rowBingo || columnBingo
    }

    fun score() = positionMap.values.fold(0) { total, bingoPositions ->
      total + bingoPositions.filter { !it.isMarked() }.sumOf { it.number }
    }
  }

  private fun List<List<BingoPosition>>.toBoard() = Board(this)
}

