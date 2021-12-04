package me.peckb.aoc._2021.calendar.day04

internal data class Board(val values: List<List<BingoPosition>>) {
  data class BingoPosition(val number: Int, private var marked: Boolean = false) {
    fun mark() { marked = true }
    fun isMarked() = marked
  }

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
