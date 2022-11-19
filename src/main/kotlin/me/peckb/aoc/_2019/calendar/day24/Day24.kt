package me.peckb.aoc._2019.calendar.day24

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.pow

class Day24 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    var map = Array(5) { Array (5) { Area(false) } }

    input.forEachIndexed { y, row ->
      row.forEachIndexed { x, c ->
        when (c) {
          '#' -> map[y][x] = Area(true)
          '.' -> map[y][x] = Area(false)
        }
      }
    }
    map.forEachIndexed { y, row ->
      row.forEachIndexed { x, area ->
        if ((y - 1) in (0 until 5)) map[y - 1][x].neighbors.add(area)
        if ((y + 1) in (0 until 5)) map[y + 1][x].neighbors.add(area)
        if ((x - 1) in (0 until 5)) map[y][x - 1].neighbors.add(area)
        if ((x + 1) in (0 until 5)) map[y][x + 1].neighbors.add(area)
      }
    }

    val mapSnapshots = mutableSetOf<String>().also { it.add(map.encode()) }

    var done = false

    while(!done) {
      val newMap = Array(5) { Array (5) { Area(false) } }
      map.forEachIndexed { y, row ->
        row.forEachIndexed { x, area ->
          if ((y - 1) in (0 until 5)) newMap[y - 1][x].neighbors.add(newMap[y][x])
          if ((y + 1) in (0 until 5)) newMap[y + 1][x].neighbors.add(newMap[y][x])
          if ((x - 1) in (0 until 5)) newMap[y][x - 1].neighbors.add(newMap[y][x])
          if ((x + 1) in (0 until 5)) newMap[y][x + 1].neighbors.add(newMap[y][x])

          newMap[y][x].infested = area.nextInfestedState()
        }
      }
      val encodedMap = newMap.encode()
      if (mapSnapshots.contains(encodedMap)) {
        done = true
      } else {
        mapSnapshots.add(newMap.encode())
      }
      map = newMap
    }

    map.encode().toBiodiversityRating()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val min = -100
    val max = 100

    val maps = (min .. max).associateWith { depth ->
      Array(5) { Array<RecursiveArea?> (5) { RecursiveArea(depth, false) } }.also {
        it[2][2] = null
      }
    }

    // set our initial map
    input.forEachIndexed { y, row ->
      row.forEachIndexed { x, c ->
        when (c) {
          '#' -> maps[0]?.also { it[y][x]?.infested = true }
          '.' -> maps[0]?.also { it[y][x]?.infested = false }
        }
      }
    }

    // setup neighbors
    // note: OUT -> UP
    //       IN  -> DOWN
    maps.forEach { (depth, map) ->
      map.forEachIndexed { y, recursiveAreas ->
        recursiveAreas.forEachIndexed inner@ { x, recursiveArea ->
          if (recursiveArea == null) return@inner

          val upperMap by lazy { maps[depth + 1]!! }
          val lowerMap by lazy { maps[depth - 1]!! }

          // upper connections
          val eight by lazy { upperMap[1][2]!! }
          val twelve by lazy { upperMap[2][1]!! }
          val fourteen by lazy { upperMap[2][3]!! }
          val eighteen by lazy { upperMap[3][2]!! }

          // same depth connections
          val myA = map[0][0]!!
          val myB = map[0][1]!!
          val myC = map[0][2]!!
          val myD = map[0][3]!!
          val myE = map[0][4]!!
          val myF = map[1][0]!!
          val myG = map[1][1]!!
          val myH = map[1][2]!!
          val myI = map[1][3]!!
          val myJ = map[1][4]!!
          val myK = map[2][0]!!
          val myL = map[2][1]!!
          val myN = map[2][3]!!
          val myO = map[2][4]!!
          val myP = map[3][0]!!
          val myQ = map[3][1]!!
          val myR = map[3][2]!!
          val myS = map[3][3]!!
          val myT = map[3][4]!!
          val myU = map[4][0]!!
          val myV = map[4][1]!!
          val myW = map[4][2]!!
          val myX = map[4][3]!!
          val myY = map[4][4]!!

          // lower connections
          val innerA by lazy { lowerMap[0][0]!! }
          val innerB by lazy { lowerMap[0][1]!! }
          val innerC by lazy { lowerMap[0][2]!! }
          val innerD by lazy { lowerMap[0][3]!! }
          val innerE by lazy { lowerMap[0][4]!! }
          val innerF by lazy { lowerMap[1][0]!! }
          val innerJ by lazy { lowerMap[1][4]!! }
          val innerK by lazy { lowerMap[2][0]!! }
          val innerO by lazy { lowerMap[2][4]!! }
          val innerP by lazy { lowerMap[3][0]!! }
          val innerT by lazy { lowerMap[3][4]!! }
          val innerU by lazy { lowerMap[4][0]!! }
          val innerV by lazy { lowerMap[4][1]!! }
          val innerW by lazy { lowerMap[4][2]!! }
          val innerX by lazy { lowerMap[4][3]!! }
          val innerY by lazy { lowerMap[4][4]!! }

          when (y to x) {
            0 to 0 -> { // A
              recursiveArea.neighbors.add(myB)
              recursiveArea.neighbors.add(myF)
              if (depth != max) {
                recursiveArea.neighbors.add(twelve)
                recursiveArea.neighbors.add(eight)
              }
            }
            0 to 1 -> { // B
              recursiveArea.neighbors.add(myA)
              recursiveArea.neighbors.add(myC)
              recursiveArea.neighbors.add(myG)
              if (depth != max) {
                recursiveArea.neighbors.add(eight)
              }
            }
            0 to 2 -> { // C
              recursiveArea.neighbors.add(myB)
              recursiveArea.neighbors.add(myH)
              recursiveArea.neighbors.add(myD)
              if (depth != max) {
                recursiveArea.neighbors.add(eight)
              }
            }
            0 to 3 -> { // D
              recursiveArea.neighbors.add(myC)
              recursiveArea.neighbors.add(myE)
              recursiveArea.neighbors.add(myI)
              if (depth != max) {
                recursiveArea.neighbors.add(eight)
              }
            }
            0 to 4 -> { // E
              recursiveArea.neighbors.add(myD)
              recursiveArea.neighbors.add(myJ)
              if (depth != max) {
                recursiveArea.neighbors.add(eight)
                recursiveArea.neighbors.add(fourteen)
              }
            }
            1 to 0 -> { // F
              recursiveArea.neighbors.add(myA)
              recursiveArea.neighbors.add(myG)
              recursiveArea.neighbors.add(myK)
              if (depth != max) {
                recursiveArea.neighbors.add(twelve)
              }
            }
            1 to 1 -> { // G
              recursiveArea.neighbors.add(myB)
              recursiveArea.neighbors.add(myF)
              recursiveArea.neighbors.add(myH)
              recursiveArea.neighbors.add(myL)
            }
            1 to 2 -> { // H
              recursiveArea.neighbors.add(myG)
              recursiveArea.neighbors.add(myC)
              recursiveArea.neighbors.add(myI)
              if (depth != min) {
                recursiveArea.neighbors.add(innerA)
                recursiveArea.neighbors.add(innerB)
                recursiveArea.neighbors.add(innerC)
                recursiveArea.neighbors.add(innerD)
                recursiveArea.neighbors.add(innerE)
              }
            }
            1 to 3 -> { // I
              recursiveArea.neighbors.add(myH)
              recursiveArea.neighbors.add(myD)
              recursiveArea.neighbors.add(myJ)
              recursiveArea.neighbors.add(myN)
            }
            1 to 4 -> { // J
              recursiveArea.neighbors.add(myE)
              recursiveArea.neighbors.add(myI)
              recursiveArea.neighbors.add(myO)
              if (depth != max) {
                recursiveArea.neighbors.add(fourteen)
              }
            }
            2 to 0 -> { // K
              recursiveArea.neighbors.add(myF)
              recursiveArea.neighbors.add(myL)
              recursiveArea.neighbors.add(myP)
              if (depth != max) {
                recursiveArea.neighbors.add(twelve)
              }
            }
            2 to 1 -> { // L
              recursiveArea.neighbors.add(myG)
              recursiveArea.neighbors.add(myK)
              recursiveArea.neighbors.add(myQ)
              if (depth != min) {
                recursiveArea.neighbors.add(innerA)
                recursiveArea.neighbors.add(innerF)
                recursiveArea.neighbors.add(innerK)
                recursiveArea.neighbors.add(innerP)
                recursiveArea.neighbors.add(innerU)
              }
            }
            2 to 3 -> { // N
              recursiveArea.neighbors.add(myI)
              recursiveArea.neighbors.add(myO)
              recursiveArea.neighbors.add(myS)
              if (depth != min) {
                recursiveArea.neighbors.add(innerE)
                recursiveArea.neighbors.add(innerJ)
                recursiveArea.neighbors.add(innerO)
                recursiveArea.neighbors.add(innerT)
                recursiveArea.neighbors.add(innerY)
              }
            }
            2 to 4 -> { // O
              recursiveArea.neighbors.add(myJ)
              recursiveArea.neighbors.add(myN)
              recursiveArea.neighbors.add(myT)
              if (depth != max) {
                recursiveArea.neighbors.add(fourteen)
              }
            }
            3 to 0 -> { // P
              recursiveArea.neighbors.add(myK)
              recursiveArea.neighbors.add(myQ)
              recursiveArea.neighbors.add(myU)
              if (depth != max) {
                recursiveArea.neighbors.add(twelve)
              }
            }
            3 to 1 -> { // Q
              recursiveArea.neighbors.add(myL)
              recursiveArea.neighbors.add(myP)
              recursiveArea.neighbors.add(myR)
              recursiveArea.neighbors.add(myV)
            }
            3 to 2 -> { // R
              recursiveArea.neighbors.add(myQ)
              recursiveArea.neighbors.add(myS)
              recursiveArea.neighbors.add(myW)
              if (depth != min) {
                recursiveArea.neighbors.add(innerU)
                recursiveArea.neighbors.add(innerV)
                recursiveArea.neighbors.add(innerW)
                recursiveArea.neighbors.add(innerX)
                recursiveArea.neighbors.add(innerY)
              }
            }
            3 to 3 -> { // S
              recursiveArea.neighbors.add(myN)
              recursiveArea.neighbors.add(myR)
              recursiveArea.neighbors.add(myT)
              recursiveArea.neighbors.add(myX)
            }
            3 to 4 -> { // T
              recursiveArea.neighbors.add(myO)
              recursiveArea.neighbors.add(myS)
              recursiveArea.neighbors.add(myY)
              if (depth != max) {
                recursiveArea.neighbors.add(fourteen)
              }
            }
            4 to 0 -> { // U
              recursiveArea.neighbors.add(myP)
              recursiveArea.neighbors.add(myV)
              if (depth != max) {
                recursiveArea.neighbors.add(twelve)
                recursiveArea.neighbors.add(eighteen)
              }
            }
            4 to 1 -> { // V
              recursiveArea.neighbors.add(myU)
              recursiveArea.neighbors.add(myQ)
              recursiveArea.neighbors.add(myW)
              if (depth != max) {
                recursiveArea.neighbors.add(eighteen)
              }
            }
            4 to 2 -> { // W
              recursiveArea.neighbors.add(myV)
              recursiveArea.neighbors.add(myR)
              recursiveArea.neighbors.add(myX)
              if (depth != max) {
                recursiveArea.neighbors.add(eighteen)
              }
            }
            4 to 3 -> { // X
              recursiveArea.neighbors.add(myW)
              recursiveArea.neighbors.add(myS)
              recursiveArea.neighbors.add(myY)
              if (depth != max) {
                recursiveArea.neighbors.add(eighteen)
              }
            }
            4 to 4 -> { // Y
              recursiveArea.neighbors.add(myX)
              recursiveArea.neighbors.add(myT)
              if (depth != max) {
                recursiveArea.neighbors.add(fourteen)
                recursiveArea.neighbors.add(eighteen)
              }
            }
            else -> throw IllegalArgumentException("Unknown area $recursiveArea")
          }
        }
      }
    }

    repeat(200) { repeatCount ->
      val areasToShift = mutableListOf<RecursiveArea>()
      (min .. max).forEach { currentDepth ->
        maps[currentDepth]!!.forEachIndexed { y, row ->
          row.forEachIndexed { x, area ->
            area?.also {
              areasToShift.add(it)
              it.generateNextMutation()
            }
          }
        }
      }
      areasToShift.forEach { it.mutate() }
    }


    var bugCount = 0
    maps.forEach { (_, depth) ->
      depth.forEach { row ->
        row.forEach { area ->
          area?.infested?.also { if (it) bugCount ++ }
        }
      }
    }

    bugCount
  }

  class RecursiveArea(
    val depth: Int,
    var infested: Boolean,
    val neighbors: MutableList<RecursiveArea> = mutableListOf()
  ) {
    private var nextMutation: Boolean = false

    fun mutate() { infested = nextMutation }

    fun generateNextMutation() {
      nextMutation = when (infested) {
        true -> neighbors.count { it.infested } == 1
        false -> neighbors.count { it.infested } in (1..2)
      }
    }
  }

  class Area(var infested: Boolean, val neighbors: MutableList<Area> = mutableListOf()) {
    fun nextInfestedState() = when (infested) {
      true -> neighbors.count { it.infested } == 1
      false -> neighbors.count { it.infested } in (1..2)
    }
  }

  private fun Array<Array<Area>>.encode(): String {
    return this.joinToString("") { row ->
      row.joinToString("") { area ->
        if (area.infested) "1" else "0"
      }
    }
  }

  private fun String.toBiodiversityRating(): Long {
    return foldIndexed(0L) { index, acc, c ->
      if (c == '0') acc else acc + 2.0.pow(index.toDouble()).toLong()
    }
  }
}
