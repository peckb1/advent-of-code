package me.peckb.aoc._2020.calendar.day20

import me.peckb.aoc._2020.calendar.day20.Edge.*

class Tile(val id: Int, var data: List<String>) {
  var edgeValues: Map<Edge, List<String>> = createEdgeValues(data)

  private fun createEdgeValues(data: List<String>): MutableMap<Edge, List<String>> {
    val result = mutableMapOf<Edge, List<String>>()

    result[NORTH] = listOf(data[0], data[0].reversed())
    result[SOUTH] = listOf(data[9], data[9].reversed())

    result[WEST] = data.map { it[0] }.let {
      val key = it.joinToString("")
      listOf(key, key.reversed())
    }

    result[EAST] = data.map { it[9] }.let {
      val key = it.joinToString("")
      listOf(key, key.reversed())
    }

    return result
  }

  fun rotateClockwise() = reset {
    data.indices.map { xIndex ->
      ((data.size - 1) downTo 0).joinToString("") { yIndex ->
        data[yIndex][xIndex].toString()
      }
    }
  }

  fun rotateCounterClockwise() = reset {
    ((data.size - 1) downTo 0).map { xIndex ->
      data.indices.joinToString("") { yIndex ->
        data[yIndex][xIndex].toString()
      }
    }
  }

  fun rotate180() = reset { data.map { it.reversed() }.reversed() }

  fun flipHorizontal() = reset { data.map { it.reversed() } }

  fun flipVertical() = reset { data.reversed() }

  override fun toString(): String {
    return "$id\n${data.joinToString("\n")}"
  }

  private fun reset(dataReset: () -> List<String>) = apply {
    data = dataReset()
    edgeValues = createEdgeValues(data)
  }

  fun trim() {
    data = data.drop(1).dropLast(1)
      .map { it.drop(1).dropLast(1) }
  }
}
