package me.peckb.aoc._2016.calendar.day17

import me.peckb.aoc.MD5
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode

class Day17 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
  private val md5: MD5
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val start = Location(0, 0, "").withData(input, md5)
    val end = Location(3, 3, "IGNORED")

    val comparator = Comparator<Location> { location1, location2 ->
      when (val xComp = location1.x.compareTo(location2.x)) {
        0 -> location1.y.compareTo(location2.y)
        else -> xComp
      }
    }

    val paths = VaultDijkstra().solve(start, end, comparator)
    paths.filter { (l, _) -> l.x == 3 && l.y == 3 }
      .minByOrNull { it.value }
      ?.key?.path
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val start = Location(0, 0, "").withData(input, md5)
    val paths = VaultDijkstra().solve(start)
    paths.filter { (l, _) -> l.x == 3 && l.y == 3 }
      .maxByOrNull { it.value }
      ?.key?.path?.length
  }

  data class Location(val y: Int, val x: Int, val path: String) : DijkstraNode<Location> {
    private lateinit var passcode: String
    private lateinit var md5: MD5

    override fun neighbors(): Map<Location, Int> {
      val moves = mutableMapOf<Location, Int>()
      if (x == 3 && y == 3) return moves

      val hash = md5.hash("$passcode$path").take(4)
      val u = hash[0]
      val d = hash[1]
      val l = hash[2]
      val r = hash[3]

      if (u in 'b'..'f' && y > 0) moves[Location(y - 1, x, "${path}U").withData(passcode, md5)] = 1
      if (d in 'b'..'f' && y < 3) moves[Location(y + 1, x, "${path}D").withData(passcode, md5)] = 1
      if (l in 'b'..'f' && x > 0) moves[Location(y, x - 1, "${path}L").withData(passcode, md5)] = 1
      if (r in 'b'..'f' && x < 3) moves[Location(y, x + 1, "${path}R").withData(passcode, md5)] = 1

      return moves
    }

    fun withData(passcode: String, md5: MD5) = apply {
      this.passcode = passcode
      this.md5 = md5
    }
  }

  class VaultDijkstra : GenericIntDijkstra<Location>()
}
