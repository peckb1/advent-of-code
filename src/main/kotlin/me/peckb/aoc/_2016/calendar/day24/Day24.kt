package me.peckb.aoc._2016.calendar.day24

import me.peckb.aoc._2016.calendar.day24.Day24.DuctType.OPEN
import me.peckb.aoc._2016.calendar.day24.Day24.DuctType.WALL
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode

class Day24 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (idByDuct, ductById) = parseInput(input)
    val routes = generateRoutes(idByDuct)

    val locationIds = idByDuct.values.toTypedArray()
    val permutations = generatePermutations(locationIds, 0, locationIds.size - 1)
      .filter { it[0] == 0 }
      .map { it.toList() }

    findMinCost(routes, ductById, permutations)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (idByDuct, ductById) = parseInput(input)
    val routes = generateRoutes(idByDuct)

    val locationIds = idByDuct.values.toTypedArray()
    val permutations = generatePermutations(locationIds, 0, locationIds.size - 1)
      .filter { it[0] == 0 }
      .map { it.toList().plus(0) }

    findMinCost(routes, ductById, permutations)
  }

  private fun parseInput(input: Sequence<String>): Pair<Map<Duct, Int>, Map<Int, Duct>> {
    val HVACLayout = mutableListOf<List<Duct>>()

    val idByDuct = mutableMapOf<Duct, Int>()
    val ductById = mutableMapOf<Int, Duct>()

    input.forEachIndexed { y, row ->
      HVACLayout.add(row.mapIndexed { x, c ->
        val type = if (c == '#') WALL else OPEN
        Duct(y, x, type).also {
          if (c.isDigit()) {
            idByDuct[it] = Character.getNumericValue(c)
            ductById[Character.getNumericValue(c)] = it
          }
        }
      }.toList())
    }
    HVACLayout.forEach { row -> row.forEach { it.withHVACLayout(HVACLayout) } }

    return idByDuct to ductById
  }

  private fun generateRoutes(idByDuct: Map<Duct, Int>): Map<Duct, Map<Duct, Int>> {
    val solver = HVACDijkstra()

    return idByDuct.mapValues { (start, _) ->
      idByDuct.map { (end, _) ->
        if (end == start) start to 0 else {
          solver.solve(start, end)
            .filter { it.key.x == end.x && it.key.y == end.y }
            .minByOrNull { it.value }
            ?.let { it.key to it.value }!!
        }
      }.toMap()
    }
  }

  private fun findMinCost(
    routes: Map<Duct, Map<Duct, Int>>,
    ductById: Map<Int, Duct>,
    permutations: List<List<Int>>
  ): Int {
    return permutations.minOf { permutation ->
      permutation.toList().windowed(2).sumOf {
        val source = ductById[it.first()]!!
        val destination = ductById[it.last()]!!
        routes[source]!![destination]!!
      }
    }
  }

  class HVACDijkstra : GenericIntDijkstra<Duct>()

  enum class DuctType { WALL, OPEN }

  data class Duct(val y: Int, val x: Int, val ductType: DuctType) : DijkstraNode<Duct> {
    private lateinit var layout: List<List<Duct>>

    override fun neighbors(): Map<Duct, Int> {
      val moves = mutableListOf<Duct>()

      layout[y][x-1].also { if(it.ductType == OPEN) moves.add(it) }
      layout[y+1][x].also { if(it.ductType == OPEN) moves.add(it) }
      layout[y][x+1].also { if(it.ductType == OPEN) moves.add(it) }
      layout[y-1][x].also { if(it.ductType == OPEN) moves.add(it) }

      return moves.associateWith { 1 }
    }

    fun withHVACLayout(layout: List<List<Duct>>) = apply { this.layout = layout }
  }

  // this is the third time we've done this:  extract out permutation generation
  private fun generatePermutations(data: Array<Int>, l: Int, r: Int): MutableList<Array<Int>> {
    val permutations = mutableListOf<Array<Int>>()

    if (l == r) {
      permutations.add(data.clone())
    } else {
      (l..r).map { i ->
        swap(data, l, i)
        permutations.addAll(generatePermutations(data, l + 1, r))
        swap(data, l, i)
      }
    }

    return permutations
  }

  private fun swap(data: Array<Int>, i: Int, j: Int) {
    val t = data[i]
    data[i] = data[j]
    data[j] = t
  }
}
