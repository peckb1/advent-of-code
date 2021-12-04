package me.peckb.aoc._2021.calendar.day04

internal data class Board(val values: List<List<BingoPosition>>) {
  data class BingoPosition(val number: Int, private var marked: Boolean = false) {
    fun mark() { marked = true }
    fun isMarked() = marked
  }

  private val positionMap = mutableMapOf<Int, BingoPosition>()

  init {
    values.forEach { row ->
      row.forEach { item ->
        positionMap.computeIfAbsent(item.number) { item }
      }
    }
  }

  fun markNumber(number: Int) = apply { positionMap[number]?.mark() }

  fun isWinner(): Boolean {
    val rowBingo by lazy {
      values.any { row -> row.all { it.isMarked() } }
    }
    val columnBingo by lazy {
      values.indices.any { column -> values.all { row -> row[column].isMarked() } }
    }

    return rowBingo || columnBingo
  }

  fun score() = positionMap.values.fold(0) { total, bingoPosition ->
    total + (bingoPosition.number.takeIf { !bingoPosition.isMarked() } ?: 0)
  }
}
