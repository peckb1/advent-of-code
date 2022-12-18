package me.peckb.aoc._2022.calendar.day18

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::cubeLocation) { input ->
    val cubes = hashSetOf<CubeLocation>()
    input.forEach { cube -> cubes.add(cube) }
    cubes.sumOf { cube -> cube.neighborCubes().count { !cubes.contains(it) } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::cubeLocation) { input ->
    val cubes = hashSetOf<CubeLocation>()

    var min = Int.MAX_VALUE
    var max = Int.MIN_VALUE
    input.forEach { cube ->
      cubes.add(cube)
      min = minOf(min, cube.x, cube.y, cube.z)
      max = maxOf(max, cube.x, cube.y, cube.z)
    }
    // add buffer
    min--
    max++

    val pocketCubes = mutableSetOf<CubeLocation>()
    val outerAir = mutableSetOf<CubeLocation>()
    val solver = CubeDijkstra()

    cubes.forEach { cube ->
      cube.neighborCubes()
        .map { it.withCubes(cubes).withBounds(min, max) }
        .filter { !cubes.contains(it) }
        .filter { !outerAir.contains(it) }
        .filter { !pocketCubes.contains(it) }
        .forEach { unseenAirCube ->
          val airNeighbors = solver.solve(unseenAirCube).keys
          val outsideAir = airNeighbors.any { it.touchesTheEdge() }
          if (outsideAir) {
            outerAir.addAll(airNeighbors)
          } else {
            pocketCubes.addAll(airNeighbors)
          }
        }
    }

    outerAir.sumOf { airCube ->
      airCube.neighborCubes().count { cubes.contains(it) }
    }
  }

  private fun cubeLocation(line: String) : CubeLocation {
    return line.split(",").map { it.toInt() }.let { (x, y, z) -> CubeLocation(x, y, z) }
  }

  data class CubeLocation(val x: Int, val y: Int, val z: Int) : GenericIntDijkstra.DijkstraNode<CubeLocation> {
    private var min: Int = Int.MAX_VALUE
    private var max: Int = Int.MIN_VALUE
    private lateinit var cubes: HashSet<CubeLocation>

    fun withCubes(cubes: HashSet<CubeLocation>) = apply { this.cubes = cubes }

    fun withBounds(min: Int, max: Int) = apply {
      this.min = min
      this.max = max
    }

    override fun neighbors(): Map<CubeLocation, Int> {
      return neighborCubes()
        .map { it.withCubes(cubes).withBounds(min, max) }
        .filter { it.x in (min..max) && it.y in (min..max) && it.z in (min..max) }
        .filter { !cubes.contains(it) }.associateWith { 1 }
    }

    fun neighborCubes(): List<CubeLocation> {
      val front = CubeLocation(x, y, z + 1)
      val back = CubeLocation(x, y, z - 1)
      val up = CubeLocation(x, y + 1, z)
      val down = CubeLocation(x, y - 1, z)
      val right = CubeLocation(x + 1, y, z)
      val left = CubeLocation(x - 1, y, z)

      return listOf(front, back, up, down, right, left)
    }

    fun touchesTheEdge(): Boolean {
      return x == min || y == min || z == min || x == max || y == max || z == max
    }
  }

  class CubeDijkstra : GenericIntDijkstra<CubeLocation>()
}
