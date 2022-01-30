package me.peckb.aoc._2018.calendar.day22

import me.peckb.aoc._2018.calendar.day22.Day22.Equipment.CLIMBING_GEAR
import me.peckb.aoc._2018.calendar.day22.Day22.Equipment.NOTHING
import me.peckb.aoc._2018.calendar.day22.Day22.Equipment.TORCH
import me.peckb.aoc._2018.calendar.day22.Day22.Region
import me.peckb.aoc._2018.calendar.day22.Day22.RegionType.NARROW
import me.peckb.aoc._2018.calendar.day22.Day22.RegionType.ROCKY
import me.peckb.aoc._2018.calendar.day22.Day22.RegionType.WET
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode

typealias Caves = Array<Array<Region>>

class Day22 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (depthString, targetString) = input.take(2).toList()
    val depth = depthString.substringAfter("depth: ").toLong()
    val (targetX, targetY) = targetString.substringAfter("target: ").split(",").map(String::toInt)

    var totalRiskToTarget = 0
    val caveSystem = Array(targetY + 1) { Array(targetX + 1) { NONE } }
    repeat(targetY + 1) { y ->
      repeat(targetX + 1) { x ->
        val geologicIndex = createGeologicIndex(caveSystem, y, x, targetX, targetY)
        val region = Region(y, x, TORCH).withValues(depth, geologicIndex)
        caveSystem[y][x] = region
        totalRiskToTarget += region.regionType.risk
      }
    }

    totalRiskToTarget
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (depthString, targetString) = input.take(2).toList()
    val depth = depthString.substringAfter("depth: ").toLong()
    val (targetX, targetY) = targetString.substringAfter("target: ").split(",").map(String::toInt)
    val caveX = targetX + 1
    val caveY = targetY + 1
    val buffer = 50

    val caveSystem = Array(caveY + buffer) { Array(caveX + buffer) { NONE } }
    repeat(caveY + buffer) { y ->
      repeat(caveX + buffer) { x ->
        val geologicIndex = createGeologicIndex(caveSystem, y, x, targetX, targetY)
        val region = Region(y, x).withValues(depth, geologicIndex)
        caveSystem[y][x] = region
      }
    }

    val source = caveSystem[0][0].withCaves(caveSystem).withEquipment(TORCH)
    val pathfinder = CaveDijkstra()
    val paths = pathfinder.solve(source)

    val routesAtTarget = paths.filter { it.key.x == targetX && it.key.y == targetY }

    routesAtTarget.minOf { (region, cost) ->
      when (region.equipment) {
        TORCH -> cost
        CLIMBING_GEAR -> cost + 7
        NOTHING -> cost + 7
        null -> throw IllegalStateException("We should have been given an equipment")
      }
    }
  }

  private fun createGeologicIndex(caves: Caves, y: Int, x: Int, targetX: Int, targetY: Int): Long {
    return if ((y == 0 && x == 0) || (y == targetY && x == targetX)) {
      0L
    } else if (y == 0) {
      x * 16807L
    } else if (x == 0) {
      y * 48271L
    } else {
      val left = caves[y][x-1]
      val above = caves[y-1][x]
      left.erosionLevel * above.erosionLevel
    }
  }

  class CaveDijkstra : GenericIntDijkstra<Region>()

  enum class RegionType(val risk: Int, val allowedEquipment: Set<Equipment>, private val representation: String) {
    ROCKY(0, setOf(CLIMBING_GEAR, TORCH), "."),
    WET(1, setOf(CLIMBING_GEAR, NOTHING), "="),
    NARROW(2, setOf(TORCH, NOTHING), "|");
  }

  enum class Equipment {
    TORCH, CLIMBING_GEAR, NOTHING
  }

  data class Region(val y: Int, val x: Int, var equipment: Equipment? = null) : DijkstraNode<Region> {
    private var depth: Long = 0
    private var geologicIndex: Long = 0

    private lateinit var caves: Caves

    val erosionLevel: Long by lazy { (depth + geologicIndex) % EROSION_MODULO }

    val regionType: RegionType by lazy {
      when (erosionLevel % REGION_TYPE_MODULE) {
        0L -> ROCKY
        1L -> WET
        2L -> NARROW
        else -> throw IllegalStateException("We broke math.")
      }
    }

    fun withValues(depth: Long, geologicIndex: Long) = apply {
      this.depth = depth
      this.geologicIndex = geologicIndex
    }

    fun withCaves(caves: Caves) = apply { this.caves = caves }

    fun withEquipment(equipment: Equipment) = apply { this.equipment = equipment }

    override fun neighbors(): Map<Region, Int> {
      val u = neighbor(y - 1, x)
      val r = neighbor(y, x + 1)
      val d = neighbor(y + 1, x)
      val l = neighbor(y, x - 1)

      val paths = listOfNotNull(u, r, d, l).flatMap { neighbor ->
        if (neighbor.regionType.allowedEquipment.contains(equipment)) {
          listOf(neighbor.clone(equipment, caves) to MOVE_TIME)
        } else {
          regionType.allowedEquipment.intersect(neighbor.regionType.allowedEquipment).map { eq ->
            neighbor.clone(eq, caves) to (REEQUIP_TIME + MOVE_TIME)
          }
        }
      }

      return paths.toMap()
    }

    private fun clone(newEquipment: Equipment?, caves: Caves) = copy(equipment = newEquipment)
      .withCaves(caves)
      .withValues(depth, geologicIndex)

    private fun neighbor(y: Int, x: Int): Region? {
      return if (y in caves.indices && x in caves[y].indices) caves[y][x] else null
    }
  }

  companion object {
    private const val EROSION_MODULO = 20183L
    private const val REGION_TYPE_MODULE = 3L
    private const val MOVE_TIME = 1
    private const val REEQUIP_TIME = 7

    private val NONE = Region(-1, -1)
  }
}
