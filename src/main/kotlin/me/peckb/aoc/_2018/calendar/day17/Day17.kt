package me.peckb.aoc._2018.calendar.day17

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

class Day17 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::clay) { input ->
    val (underground, area) = scan(input)

    // print(underground, area); println()

    val downspouts = mutableListOf(Source(0, 500))

    while(downspouts.isNotEmpty()) {
      val downSpout = downspouts.first()
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

      // print(underground, area); println()

      val stoppingValue = underground[bottom][downSpout.x]
      if (stoppingValue != '.') {
        if (stoppingValue == '~') {
          // we found someone else's water, but we should upfill and overflow
          upfillAndOverflow(bottom, underground, downSpout, downspouts, area)
        } else if (stoppingValue == '|') {
          // we found the overflow of someone elses water, so we only need to fall, and can just
          // ignore this downspout
        } else if (stoppingValue == '#') {
          // we found clay, first ones here, upfill and overflow
          if (underground[bottom][downSpout.x - 1] == '.' && underground[bottom][downSpout.x + 1] == '.') {
            if (underground[bottom][downSpout.x - 1] == '.') {
              underground[bottom - 1][downSpout.x - 1] = '|'
              downspouts.add(Source(bottom - 1, downSpout.x - 1))
            }
            if (underground[bottom][downSpout.x + 1] == '.') {
              underground[bottom - 1][downSpout.x + 1] = '|'
              downspouts.add(Source(bottom - 1, downSpout.x + 1))
            }
          } else {
            bottom--

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
            if (dropOffBeforeLeft != null || dropOffBeforeRight != null) {
              (left + 1 .. downSpout.x).forEach { x -> underground[bottom][x] = '|' }
              dropOffBeforeLeft?.let { downspouts.add(Source(bottom, it + 1)) }
              (downSpout.x until right).forEach { x -> underground[bottom][x] = '|' }
              dropOffBeforeRight?.let { downspouts.add(Source(bottom, it - 1)) }
            } else {
              upfillAndOverflow(bottom, underground, downSpout, downspouts, area)
            }
          }
        } else {
          throw IllegalStateException("Unknown stopping value $stoppingValue")
        }
      } else {
        underground[current][downSpout.x] = '|'
      }
      downspouts.remove(downSpout)
    }

    var water = 0
    var wavy = 0
    underground.forEachIndexed { y, row ->
      if (y in (area.minY..area.maxY + 1)) {
        val trimmedRow = row.slice(area.minX-1..area.maxX + 1)
        trimmedRow.forEach { c ->
          if (c == '|') {
            water++
          } else if (c == '~') {
            water++; wavy++
          }
        }
      }
    }

    // print(underground, area); println()

    // 49430 too low
    // 50842 too high
    // 50838
    water to wavy
  }

  private fun upfillAndOverflow(
    floor: Int,
    underground: Array<Array<Char>>,
    downSpout: Source,
    downspouts: MutableList<Source>,
    area: Area
  ) {
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
        // print(underground, area); println()
      }
    }

    // we are now overflowing!
    underground[bottom][downSpout.x] = '|'

    var overflowLeft = downSpout.x - 1
    var overflowRight = downSpout.x + 1
    if (underground[bottom][overflowLeft] == '|' || underground[bottom][overflowRight] == '|') {
      var addedLeftDownspout = false
      var addedRightDownspout = false
      var foundLeftWall = false
      var foundRightWall = false

      if (underground[bottom][overflowLeft] == '.') {
        while(overflowLeft >= minLeft - 1 && underground[bottom][overflowLeft] != '#') {
          underground[bottom][overflowLeft] = '|'
          overflowLeft--
        }
        if (overflowLeft < minLeft - 1) {
          downspouts.add(Source(bottom, overflowLeft + 1))
          addedLeftDownspout = true
        } else {
          foundLeftWall = true
        }
      } else {
        var foundWallOrDownspoutOrEdge = false
        while(!foundWallOrDownspoutOrEdge) {
          overflowLeft--
          // if (overflowLeft == -1) {
          //   print(underground, area); println(downSpout)
          // }
          if (underground[bottom][overflowLeft] == '#') {
            // we found an overflow wall
            foundWallOrDownspoutOrEdge = true
            foundLeftWall = true
          } else if (downspouts.contains(Source(bottom, overflowLeft))) {
            foundWallOrDownspoutOrEdge = true
          } else if (underground[bottom + 1][overflowLeft] == '.') {
            // underground[bottom + 1][overflowLeft] = '*'
            // print(underground, area); println(downSpout)
            foundWallOrDownspoutOrEdge = true
            -1
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
          addedRightDownspout = true
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
            // underground[bottom + 1][overflowRight] = '*'
            // print(underground, area); println(downSpout)
            foundWallOrDownspoutOrEdge = true
            -1
          }
        }
      }

      if (foundRightWall && foundLeftWall) {
        (overflowLeft + 1 until overflowRight).forEach { x ->
          underground[bottom][x] = '~'
        }
        // print(underground, area); println()
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

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::clay) { input ->
    -1
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
