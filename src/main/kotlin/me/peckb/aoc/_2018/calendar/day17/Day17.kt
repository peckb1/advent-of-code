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
      val downSpout = downspouts.first()
      var bottom = fallDown(downSpout, area, underground)
      val stoppingValue = underground[bottom][downSpout.x]

      if (stoppingValue != '.') {
        if (stoppingValue == '|') { // we found the overflow of someone else's water
          // we only need to fall, and can just ignore this downspout
        } else if (stoppingValue == '~') { // we found someone else's water, upFill and overflow
          upFillAndOverflow(bottom, underground, downSpout, downspouts)
        } else if (stoppingValue == '#') { // we found clay, first ones here, upFill and overflow
          // We have three clay landing spaces
          if (underground[bottom][downSpout.x - 1] == '.' && underground[bottom][downSpout.x + 1] == '.') {
            // * the edge of a bucket
            handleBucketEdge(bottom, underground, downSpout, downspouts)
          } else {
            bottom--

            // * an inner part of an outer bucket
            val (dropOffBeforeLeftWall, dropOffBeforeRightWall) = checkForWalls(bottom, underground, downSpout)

            if (dropOffBeforeLeftWall != null || dropOffBeforeRightWall != null) {
              dropOffBeforeLeftWall?.let {
                (it .. downSpout.x).forEach { x -> underground[bottom][x] = '|' }
                downspouts.add(Source(bottom, it + 1))
              }
              dropOffBeforeRightWall?.let {
                (downSpout.x .. it).forEach { x -> underground[bottom][x] = '|' }
                downspouts.add(Source(bottom, it - 1)) 
              }
            } else {
              // the bottom of a bucket
              upFillAndOverflow(bottom, underground, downSpout, downspouts)
            }
          }
        } else {
          throw IllegalStateException("Unknown stopping value $stoppingValue")
        }
      } else {
        underground[bottom][downSpout.x] = '|'
      }
      downspouts.remove(downSpout)
    }
  }

  private fun checkForWalls(bottom: Int, underground: Array<Array<Char>>, downSpout: Source): Pair<Int?, Int?> {
    var left = downSpout.x
    var right = downSpout.x
    var dropOffBeforeLeft: Int? = null
    var dropOffBeforeRight: Int? = null

    while(underground[bottom][left] != '#') {
      if (underground[bottom + 1][left] == '.') dropOffBeforeLeft = left
      left--
    }
    while(underground[bottom][right] != '#') {
      if (underground[bottom + 1][right] == '.') dropOffBeforeRight = right
      right++
    }

    return dropOffBeforeLeft to dropOffBeforeRight
  }

  private fun handleBucketEdge(
    bottom: Int,
    underground: Array<Array<Char>>,
    downSpout: Source,
    downspouts: MutableList<Source>
  ) {
    if (underground[bottom][downSpout.x - 1] == '.') {
      underground[bottom - 1][downSpout.x - 1] = '|'
      downspouts.add(Source(bottom - 1, downSpout.x - 1))
    }
    if (underground[bottom][downSpout.x + 1] == '.') {
      underground[bottom - 1][downSpout.x + 1] = '|'
      downspouts.add(Source(bottom - 1, downSpout.x + 1))
    }
  }

  private fun fallDown(downSpout: Source, area: Area, underground: Array<Array<Char>>): Int {
    var bottom = area.maxY
    var current = downSpout.y + 1
    while(current < bottom) {
      val nextSpace = underground[current][downSpout.x]
      if (nextSpace == '~' || nextSpace == '|' || nextSpace == '#') {
        bottom = current
      } else {
        underground[current][downSpout.x] = '|'
        current++
      }
    }
    return bottom
  }

  private fun upFillAndOverflow(
    floor: Int,
    underground: Array<Array<Char>>,
    downSpout: Source,
    downspouts: MutableList<Source>
  ) {
    val (bottom, minLeft, maxRight) = upFill(floor, downSpout, underground)

    // we are now overflowing!
    underground[bottom][downSpout.x] = '|'

    var overflowLeft = downSpout.x - 1
    var overflowRight = downSpout.x + 1
    if (underground[bottom][overflowLeft] == '|' || underground[bottom][overflowRight] == '|') {
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
        var foundWallOrDownspoutOrEdge = false
        while(!foundWallOrDownspoutOrEdge) {
          overflowLeft--
          if (underground[bottom][overflowLeft] == '#') {
            // we found an overflow wall
            foundWallOrDownspoutOrEdge = true
            foundLeftWall = true
          } else if (downspouts.contains(Source(bottom, overflowLeft))) {
            foundWallOrDownspoutOrEdge = true
          } else if (underground[bottom + 1][overflowLeft] == '.') {
            foundWallOrDownspoutOrEdge = true
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
        var foundWallOrDownspoutOrEdge = false
        while(!foundWallOrDownspoutOrEdge) {
          overflowRight++
          if (underground[bottom][overflowRight] == '#') {
            // we found an overflow wall
            foundWallOrDownspoutOrEdge = true
            foundRightWall = true
          } else if (downspouts.contains(Source(bottom, overflowRight))) {
            foundWallOrDownspoutOrEdge = true
          } else if (underground[bottom + 1][overflowRight] == '.') {
            foundWallOrDownspoutOrEdge = true
          }
        }
      }

      if (foundRightWall && foundLeftWall) {
        (overflowLeft + 1 until overflowRight).forEach { x ->
          underground[bottom][x] = '~'
        }
        downspouts.add(Source(bottom - 2, downSpout.x))
      }
    } else {
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
  }

  private fun upFill(floor: Int, downSpout: Source, underground: Array<Array<Char>>): Triple<Int, Int, Int> {
    var bottom = floor
    var minLeft = 0
    var maxRight = 2000

    var overflowing = false
    while(!overflowing) {
      var leftWall = downSpout.x
      var rightWall = downSpout.x
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
