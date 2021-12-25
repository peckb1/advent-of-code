package me.peckb.aoc._2021.calendar.day25

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import me.peckb.aoc._2021.calendar.day25.Day25.Direction.EAST
import me.peckb.aoc._2021.calendar.day25.Day25.Direction.SOUTH
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day25 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val COROUTINES = 50
  }

  enum class Direction {
    EAST, SOUTH;

    fun next(): Direction {
      return when (this) {
        EAST -> SOUTH
        SOUTH -> EAST
      }
    }
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val eastFacingCucumbers = mutableListOf<SeaCucumber>()
    val southFacingCucumbers = mutableListOf<SeaCucumber>()

    val encodedWorld = input.toList()

    val world: MutableList<MutableList<SeaCucumber?>> = Array(encodedWorld.size) {
      Array<SeaCucumber?>(encodedWorld[0].length) {
        null
      }.toMutableList()
    }.toMutableList()

    encodedWorld.indices.forEach { y ->
      encodedWorld[y].forEachIndexed { x, c ->
        when (c) {
          'v' -> SeaCucumber(SOUTH, y, x).apply {
            southFacingCucumbers.add(this)
            world[y][x] = this
          }
          '>' -> SeaCucumber(EAST, y, x).apply {
            eastFacingCucumbers.add(this)
            world[y][x] = this
          }
        }
      }
    }

    var cucumbersStillMoving = true
    var direction = EAST
    var waitTime = 0

    while(cucumbersStillMoving) {
      var eastMoves = false
      var southMoves = true
      waitTime++
      runBlocking {
        val deferredMovements =
          when (direction) {
            EAST -> eastFacingCucumbers.chunked(COROUTINES).map {
              async {
                it.map { it.movement(world) }
              }
            }
            SOUTH -> southFacingCucumbers.chunked(COROUTINES).map {
              async {
                it.map { it.movement(world) }
              }
            }
          }
        val movements= deferredMovements.awaitAll()
        val movesMade = movements.flatMap { movementList ->
          movementList.mapNotNull {
            it?.invoke()
          }
        }
        eastMoves = movesMade.isNotEmpty()
        direction = direction.next()
      }
      runBlocking {
        val deferredMovements =
          when (direction) {
            EAST -> eastFacingCucumbers.chunked(COROUTINES).map {
              async {
                it.map { it.movement(world) }
              }
            }
            SOUTH -> southFacingCucumbers.chunked(COROUTINES).map {
              async {
                it.map { it.movement(world) }
              }
            }
          }
        val movements= deferredMovements.awaitAll()
        val movesMade = movements.flatMap { movementList ->
          movementList.mapNotNull {
            it?.invoke()
          }
        }
        southMoves = movesMade.isNotEmpty()
        direction = direction.next()
      }

      cucumbersStillMoving = eastMoves || southMoves
    }

    waitTime
  }

  data class SeaCucumber(val direction: Direction, var y: Int, var x: Int) {
    fun movement(world: MutableList<MutableList<SeaCucumber?>>): (() -> Unit)? {
      return when (direction) {
        EAST -> {
          val newX = (x + 1) % world[y].size
          if (world[y][newX] == null) {
            {
              world[y][newX] = this
              world[y][x] = null
              this.x = newX
              -1
            }
          } else { null }
        }
        SOUTH -> {
          val newY = (y + 1) % world.size
          if (world[newY][x] == null) {
            {
              world[newY][x] = this
              world[y][x] = null
              this.y = newY
              -1
            }
          } else { null }
        }
      }
    }

    override fun toString(): String {
      return when (direction) {
        EAST -> ">"
        SOUTH -> "v"
      }
    }
  }

  /*

v...>>.vv>
.vv>>.vv..
>>.>v>...v
>>v>>.>.v.
v>v.vv.v..
>.>>..v...
.vv..>.>v.
v.v..>>v.v
....v..v.>

....>.>v.>   ....>..vv>
v.v>.>v.v.   .vv>..vv..
>v>>..>v..   >..>.>...v
>>v>v>.>.v   >..>..>.v.
.>v.v...v.   v>..vv.v..
v>>.>vvv..   >.>...v...
..v...>>..   .vv..>.>..
vv...>>vv.   v.v..>...v
>.v.v..v.v   ....v..v.>



   */
}
