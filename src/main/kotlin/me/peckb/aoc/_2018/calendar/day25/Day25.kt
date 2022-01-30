package me.peckb.aoc._2018.calendar.day25

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.abs

class Day25 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day25) { input ->
    val data = input.toList().toMutableList()
    val constellations = mutableListOf<List<SpaceTime>>()

    while(data.isNotEmpty()) {
      val constellation = mutableListOf<SpaceTime>()
      val nodesToExplore = mutableListOf(data.first())
      while(nodesToExplore.isNotEmpty()) {
        val start = nodesToExplore.first()
        data.remove(start)
        nodesToExplore.remove(start)
        constellation.add(start)

        nodesToExplore.addAll(data.filter { start.closeTo(it) })
      }
      constellations.add(constellation)
    }

    constellations.size
  }

  data class SpaceTime(val x: Int, val y: Int, val z: Int, val t: Int) {
    fun closeTo(them: SpaceTime): Boolean {
      return abs(x - them.x) + abs(y - them.y) + abs(z - them.z) + abs(t - them.t) <= 3
    }
  }

  private fun day25(line: String): SpaceTime {
    // 0,0,0,3
    val (x, y, z, t) = line.trim().split(",").map(String::toInt)
    return SpaceTime(x, y, z, t)
  }
}
