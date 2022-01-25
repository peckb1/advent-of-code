package me.peckb.aoc._2018.calendar.day17

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

class Day17 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::clay) { input ->
    val (underground, area) = scan(input)
    fillUnderground(underground, area)

    var water = 0
    underground.forEachIndexed { y, row ->
      if (y in (area.minY..area.maxY + 1)) {
        val trimmedRow = row.slice(area.minX-1..area.maxX + 1)
        trimmedRow.forEach { c -> if (c == '|' || c == '~') water++ }
      }
    }
    water
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::clay) { input ->
    val (underground, area) = scan(input)
    fillUnderground(underground, area)

    var water = 0
    underground.forEachIndexed { y, row ->
      if (y in (area.minY..area.maxY + 1)) {
        val trimmedRow = row.slice(area.minX-1..area.maxX + 1)
        trimmedRow.forEach { c -> if (c == '~') water++ }
      }
    }
    water
  }

  private fun fillUnderground(underground: Array<Array<Char>>, area: Area) {
    val downspouts = mutableListOf(Source(0, 500))

    while(downspouts.isNotEmpty()) {
      val downspout = downspouts.first()
      val bottom = fallDown(downspout, area, underground)
      val stoppingValue = underground[bottom][downspout.x]

      if (stoppingValue != '.') {
        when (stoppingValue) {
          '|' -> { } // we found the overflow of someone else's water we only need to fall, and can just ignore this downspout
          '~' -> upFillAndOverflow(bottom, underground, downspout, downspouts) // we found someone else's water, upFill and overflow
          '#' -> { // we found clay, first ones here, upFill and overflow
            // We have three clay landing scenarios
            // * the edge of a bucket
            // * an inner part of an outer bucket
            // * the bottom of a bucket
            if (underground[bottom][downspout.x - 1] == '.' && underground[bottom][downspout.x + 1] == '.') {
              handleBucketEdge(bottom, underground, downspout, downspouts)
            } else {
              val (leftDropOff, rightDropOff) = checkForWalls(bottom - 1, underground, downspout)
              if (leftDropOff != null || rightDropOff != null) {
                handleDropOffs(leftDropOff, rightDropOff, bottom - 1, underground, downspout, downspouts)
              } else {
                upFillAndOverflow(bottom - 1, underground, downspout, downspouts)
              }
            }
          }
          else -> {
            throw IllegalStateException("Unknown stopping value $stoppingValue")
          }
        }
      } else {
        // the stopping point was the end of the map, just add one more water line
        underground[bottom][downspout.x] = '|'
      }
      // remove the current downspout
      downspouts.remove(downspout)
    }
  }

  private fun handleDropOffs(leftDropOff: Int?, rightDropOff: Int?, bottom: Int, underground: Array<Array<Char>>, downspout: Source, downspouts: MutableList<Source>) {
    fun fillToDropOff(dropOff: Int, direction: IntRange) {
      direction.forEach { x -> underground[bottom][x] = '|' }
      downspouts.add(Source(bottom, dropOff))
    }

    leftDropOff?.let { fillToDropOff(it + 1, it..downspout.x) }
    rightDropOff?.let { fillToDropOff(it - 1, downspout.x..it) }
  }

  private fun checkForWalls(bottom: Int, underground: Array<Array<Char>>, downspout: Source): Pair<Int?, Int?> {
    fun checkForWall(advance: (Int, Int) -> Int): Int? {
      var edge = downspout.x
      var dropOff: Int? = null
      while(underground[bottom][edge] != '#') {
        if (underground[bottom + 1][edge] == '.') dropOff = edge
        edge = advance(edge, 1)
      }
      return dropOff
    }

    val dropOffBeforeLeft = checkForWall(Int::minus)
    val dropOffBeforeRight = checkForWall(Int::plus)

    return dropOffBeforeLeft to dropOffBeforeRight
  }

  private fun handleBucketEdge(bottom: Int, underground: Array<Array<Char>>, downspout: Source, downspouts: MutableList<Source>) {
    fun handleEdge(advance: (Int, Int) -> Int) {
      val next = advance(downspout.x, 1)
      if (underground[bottom][next] == '.') {
        underground[bottom - 1][next] = '|'
        downspouts.add(Source(bottom - 1, next))
      }
    }
    handleEdge(Int::plus)
    handleEdge(Int::minus)
  }

  private fun fallDown(downspout: Source, area: Area, underground: Array<Array<Char>>): Int {
    var bottom = area.maxY
    var current = downspout.y + 1
    while(current < bottom) {
      val nextSpace = underground[current][downspout.x]
      if (nextSpace == '~' || nextSpace == '|' || nextSpace == '#') {
        bottom = current
      } else {
        underground[current][downspout.x] = '|'
        current++
      }
    }
    return bottom
  }

  private fun upFillAndOverflow(
    floor: Int,
    underground: Array<Array<Char>>,
    downspout: Source,
    downspouts: MutableList<Source>
  ) {
    // first upfill
    val (bottom, minLeft, maxRight) = upFill(floor, downspout, underground)
    underground[bottom][downspout.x] = '|'

    // and then deal with the overflow
    val overflowLeft = downspout.x - 1
    val overflowRight = downspout.x + 1

    if (underground[bottom][overflowLeft] == '|' || underground[bottom][overflowRight] == '|') {
      mergeOverflows(overflowLeft, overflowRight, bottom, underground, downspouts, downspout, minLeft, maxRight)
    } else {
      overflow(overflowLeft, overflowRight, bottom, underground, downspouts, minLeft, maxRight)
    }
  }

  private fun overflow(
    left: Int,
    right: Int,
    bottom: Int,
    underground: Array<Array<Char>>,
    downspouts: MutableList<Source>,
    minLeft: Int,
    maxRight: Int
  ) {
    var overflowLeft = left
    var overflowRight = right

    while(overflowLeft >= minLeft - 1 && underground[bottom][overflowLeft] != '#') {
      underground[bottom][overflowLeft] = '|'
      overflowLeft--
    }
    if (overflowLeft < minLeft - 1) downspouts.add(Source(bottom, overflowLeft + 1))

    while(overflowRight <= maxRight + 1 && underground[bottom][overflowRight] != '#') {
      underground[bottom][overflowRight] = '|'
      overflowRight++
    }
    if (overflowRight > maxRight + 1) downspouts.add(Source(bottom, overflowRight - 1))
  }

  private fun mergeOverflows(
    left: Int,
    right: Int,
    bottom: Int,
    underground: Array<Array<Char>>,
    downspouts: MutableList<Source>,
    downspout: Source,
    minLeft: Int,
    maxRight: Int
  ) {
    var overflowLeft = left
    var overflowRight = right
    var foundLeftWall = false
    var foundRightWall = false

    if (underground[bottom][overflowLeft] == '.') {
      while(overflowLeft >= minLeft - 1 && underground[bottom][overflowLeft] != '#') {
        underground[bottom][overflowLeft] = '|'
        overflowLeft--
      }
      if (overflowLeft < minLeft - 1) {
        downspouts.add(Source(bottom, overflowLeft + 1))
      } else {
        foundLeftWall = true
      }
    } else {
      var foundWallOrdownspoutOrEdge = false
      while(!foundWallOrdownspoutOrEdge) {
        overflowLeft--
        if (underground[bottom][overflowLeft] == '#') {
          // we found an overflow wall
          foundWallOrdownspoutOrEdge = true
          foundLeftWall = true
        } else if (downspouts.contains(Source(bottom, overflowLeft))) {
          foundWallOrdownspoutOrEdge = true
        } else if (underground[bottom + 1][overflowLeft] == '.') {
          foundWallOrdownspoutOrEdge = true
        }
      }
    }

    if (underground[bottom][overflowRight] == '.') {
      while(overflowRight <= maxRight + 1 && underground[bottom][overflowRight] != '#') {
        underground[bottom][overflowRight] = '|'
        overflowRight++
      }
      if (overflowRight > maxRight + 1) {
        downspouts.add(Source(bottom, overflowRight - 1))
      } else {
        foundRightWall = true
      }
    } else {
      var foundWallOrdownspoutOrEdge = false
      while(!foundWallOrdownspoutOrEdge) {
        overflowRight++
        if (underground[bottom][overflowRight] == '#') {
          // we found an overflow wall
          foundWallOrdownspoutOrEdge = true
          foundRightWall = true
        } else if (downspouts.contains(Source(bottom, overflowRight))) {
          foundWallOrdownspoutOrEdge = true
        } else if (underground[bottom + 1][overflowRight] == '.') {
          foundWallOrdownspoutOrEdge = true
        }
      }
    }

    if (foundRightWall && foundLeftWall) {
      (overflowLeft + 1 until overflowRight).forEach { x ->
        underground[bottom][x] = '~'
      }
      downspouts.add(Source(bottom - 2, downspout.x))
    }
  }

  private fun upFill(floor: Int, downspout: Source, underground: Array<Array<Char>>): Triple<Int, Int, Int> {
    var bottom = floor
    var minLeft = 0
    var maxRight = 2000

    var overflowing = false
    while(!overflowing) {
      var leftWall = downspout.x
      var rightWall = downspout.x
      while(leftWall >= minLeft && underground[bottom][leftWall] != '#') { leftWall-- }
      while(rightWall <= maxRight && underground[bottom][rightWall] != '#') { rightWall++ }

      if (leftWall >= minLeft) { minLeft = leftWall } else { overflowing = true }
      if (rightWall <= maxRight) { maxRight = rightWall } else { overflowing = true }

      if (!overflowing) {
        (leftWall + 1 until rightWall).forEach { underground[bottom][it] = '~' }
        bottom--
      }
    }

    return Triple(bottom, minLeft, maxRight)
  }

  private fun scan(input: Sequence<Clay>): Pair<Array<Array<Char>>, Area> {
    val underground = Array(2000) { Array (2000) { '.' } }.apply {
      this[0][500] = '+'
    }

    var minX = 2000
    var minY = 2000
    var maxX = 0
    var maxY = 0
    input.forEach {
      it.yRange.forEach { y ->
        it.xRange.forEach { x ->
          minX = min(x, minX)
          maxX = max(x, maxX)
          minY = min(y, minY)
          maxY = max(y, maxY)
          underground[y][x] = '#'
        }
      }
    }

    return underground to Area(minY, maxY, minX, maxX)
  }

  private fun clay(line: String): Clay {
    // x=495, y=2..7
    // y=7, x=495..501
    val (single, range) = line.split(", ")
    val (identifier, value) = single.split("=")
    val (first, last) = range.substringAfter("=").split("..")

    return if (identifier == "x") {
      val x = value.toInt()
      Clay(x..x, first.toInt()..last.toInt())
    } else {
      val y = value.toInt()
      Clay(first.toInt()..last.toInt(), y..y)
    }
  }

  @Suppress("unused")
  private fun print(water: Array<Array<Char>>, area: Area) {
    water.forEachIndexed { y, row ->
      if (y in (area.minY - 1..area.maxY + 1)) {
        val trimmedRow = row.slice(area.minX-1..area.maxX + 1)
        println(trimmedRow.joinToString(""))
      }
    }
  }

  data class Clay(val xRange: IntRange, val yRange: IntRange)

  data class Area(val minY: Int, val maxY: Int, val minX: Int, val maxX: Int)

  data class Source(val y: Int, val x: Int)
}
