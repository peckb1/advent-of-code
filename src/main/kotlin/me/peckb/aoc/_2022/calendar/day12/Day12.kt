package me.peckb.aoc._2022.calendar.day12

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra

class Day12 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    lateinit var start: Cliff
    lateinit var end: Cliff

    val cliffs: MutableList<MutableList<Cliff>> = mutableListOf()

    input.forEachIndexed { y, row ->
      val cliffRow = mutableListOf<Cliff>()
      row.forEachIndexed { x, c ->
        when (c) {
          'S' -> Cliff('a', x, y).also { start = it }
          'E' -> Cliff('z', x, y).also { end = it }
          else -> Cliff(c, x, y)
        }.also {
          it.withCliffs(cliffs)
          cliffRow.add(it)
        }
      }
      cliffs.add(cliffRow)
    }

    CliffDijkstra.solve(start, end)[end]
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val starts = mutableSetOf<Cliff>()
    lateinit var end: Cliff

    val cliffs: MutableList<MutableList<Cliff>> = mutableListOf()
    input.forEachIndexed { y, row ->
      val cliffRow = mutableListOf<Cliff>()
      row.forEachIndexed { x, c ->
        val cliff = if (c == 'S' || c == 'a') {
          Cliff('a', x, y).withCliffs(cliffs).also { starts.add(it) }
        } else if (c == 'E') {
          Cliff('z', x, y).withCliffs(cliffs).also { end = it }
        } else {
          Cliff(c, x, y).withCliffs(cliffs)
        }
        cliffRow.add(cliff)
      }
      cliffs.add(cliffRow)
    }

    starts.minOf { start -> CliffDijkstra.solve(start, end)[end] ?: Int.MAX_VALUE }
  }

  data class Cliff(val char: Char, val x: Int, val y: Int) : GenericIntDijkstra.DijkstraNode<Cliff> {
    private lateinit var cliffs: List<List<Cliff>>
    private val height = char.code

    override fun neighbors(): Map<Cliff, Int> {
      val u = if (y - 1 >= 0) cliffs[y - 1][x] else null
      val d = if (y + 1 < cliffs.size) cliffs[y + 1][x] else null
      val l = if (x - 1 >= 0) cliffs[y][x - 1] else null
      val r = if (x + 1 < cliffs[y].size) cliffs[y][x + 1] else null

      return listOfNotNull(u, d, l, r).filter { it.height - height <= 1 }.associateWith { 1 }
    }

    fun withCliffs(cliffs: MutableList<MutableList<Cliff>>) = apply {
      this.cliffs = cliffs
    }
  }

  object CliffDijkstra : GenericIntDijkstra<Cliff>()

}
