package me.peckb.aoc._2022.calendar.day18

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::cubeLocation) { input ->
    val cubes = hashSetOf<CubeLocation>()
    input.forEach { cube ->
      cubes.add(cube)
    }

    cubes.sumOf { cube ->
      val (x, y, z) = cube
      val front = CubeLocation(x, y, z + 1)
      val back = CubeLocation(x, y, z - 1)
      val up = CubeLocation(x, y + 1, z)
      val down = CubeLocation(x, y - 1, z)
      val right = CubeLocation(x + 1, y, z)
      val left = CubeLocation(x - 1, y, z)

      listOf(
        cubes.contains(front), cubes.contains(back),
        cubes.contains(up), cubes.contains(down),
        cubes.contains(right), cubes.contains(left)
      ).count { !it }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::cubeLocation) { input ->
    val cubes = hashSetOf<CubeLocation>()
    input.forEach { cube ->
      cubes.add(cube.withCubes(cubes))
    }

    val cubeSurfaceArea = cubes.sumOf { cube ->
      val (x, y, z) = cube
      val front = CubeLocation(x, y, z + 1).withCubes(cube.cubes)
      val back = CubeLocation(x, y, z - 1).withCubes(cube.cubes)
      val up = CubeLocation(x, y + 1, z).withCubes(cube.cubes)
      val down = CubeLocation(x, y - 1, z).withCubes(cube.cubes)
      val right = CubeLocation(x + 1, y, z).withCubes(cube.cubes)
      val left = CubeLocation(x - 1, y, z).withCubes(cube.cubes)
      listOf(
        cubes.contains(front), cubes.contains(back),
        cubes.contains(up), cubes.contains(down),
        cubes.contains(right), cubes.contains(left)
      ).count { !it }
    }

    val airCubes = mutableSetOf<CubeLocation>()

    cubes.forEachIndexed { n, cube ->
      println(n)
      val (x, y, z) = cube
      val front = CubeLocation(x, y, z + 1).withCubes(cube.cubes)
      val back = CubeLocation(x, y, z - 1).withCubes(cube.cubes)
      val up = CubeLocation(x, y + 1, z).withCubes(cube.cubes)
      val down = CubeLocation(x, y - 1, z).withCubes(cube.cubes)
      val right = CubeLocation(x + 1, y, z).withCubes(cube.cubes)
      val left = CubeLocation(x - 1, y, z).withCubes(cube.cubes)

      val solver = CubeDijkstra()
      listOf(front, back, up, down, right, left)
        .filter { !cubes.contains(it) }
        .filter { airCube ->
          val innerAir = solver.solve(airCube).keys
          !innerAir.any { it.x == -1 || it.y == -1 || it.z == -1 } && !innerAir.any { it.x == 25 || it.y == 25 || it.z == 25 }
        }.forEach {
          airCubes.add(it)
        }
    }

    cubeSurfaceArea - airCubes.sumOf { airCube ->
      val (x, y, z) = airCube
      val front = CubeLocation(x, y, z + 1)
      val back = CubeLocation(x, y, z - 1)
      val up = CubeLocation(x, y + 1, z)
      val down = CubeLocation(x, y - 1, z)
      val right = CubeLocation(x + 1, y, z)
      val left = CubeLocation(x - 1, y, z)

      listOf(front, back, up, down, right, left).count {
        cubes.contains(it)
      }
    }
  }


  private fun cubeLocation(line: String) : CubeLocation {
    return line.split(",").map { it.toInt() }.let { (x, y, z) -> CubeLocation(x, y, z) }
  }

  data class CubeLocation(val x: Int, val y: Int, val z: Int) : GenericIntDijkstra.DijkstraNode<CubeLocation> {
    var cubes: HashSet<CubeLocation> = hashSetOf()

    fun withCubes(cubes: HashSet<CubeLocation>) = apply { this.cubes = cubes }

    override fun neighbors(): Map<CubeLocation, Int> {
      val front = CubeLocation(x, y, z + 1).withCubes(cubes)
      val back = CubeLocation(x, y, z - 1).withCubes(cubes)
      val up = CubeLocation(x, y + 1, z).withCubes(cubes)
      val down = CubeLocation(x, y - 1, z).withCubes(cubes)
      val right = CubeLocation(x + 1, y, z).withCubes(cubes)
      val left = CubeLocation(x - 1, y, z).withCubes(cubes)

      return listOf(front, back, up, down, right, left)
        .filter { it.x in (-1..25) && it.y in (-1..25) && it.z in (-1..25) }
        .filter { !cubes.contains(it) }.associateWith { 1 }
    }
  }

  class CubeDijkstra : GenericIntDijkstra<CubeLocation>()
}
