package me.peckb.aoc._2023.calendar.day10

import me.peckb.aoc._2023.calendar.day10.Day10.DirectionCameFrom.*
import me.peckb.aoc._2023.calendar.day10.Day10.LandType.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.LinkedList
import java.util.Queue

class Day10 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (area, startLocationRow, startLocationCol) = generateArea(input)
    val locationDistances: Map<Location, Int> = findOurPipes(area, startLocationRow, startLocationCol)

    locationDistances.values.maxOf { it }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (area, startLocationRow, startLocationCol) = generateArea(input)
    val locationDistances: Map<Location, Int> = findOurPipes(area, startLocationRow, startLocationCol).also {
      clearExtraneousPipe(area, it)
    }

    things(area, startLocationCol, startLocationRow)
    things2(area)

    area.sumOf { row ->
      row.count { it == INSIDE }
    }
  }

  private fun generateArea(input: Sequence<String>): Triple<MutableList<MutableList<LandType>>, Int, Int> {
    val area = mutableListOf<MutableList<LandType>>()

    var startLocationRow = -1
    var startLocationCol = -1
    input.forEachIndexed { rowIndex, row ->
      val pipeRow = mutableListOf<LandType>()
      row.forEachIndexed { colIndex, c ->
        LandType.fromSymbol(c).also { pipe ->
          pipeRow.add(pipe)
          if (pipe == LandType.START) {
            startLocationRow = rowIndex
            startLocationCol = colIndex
          }
        }
      }
      area.add(pipeRow)
    }

    mutateStartPipeLocation(area, startLocationRow, startLocationCol)

    return Triple(area, startLocationRow, startLocationCol)
  }

  private fun mutateStartPipeLocation(
    area: MutableList<MutableList<LandType>>,
    startLocationRow: Int,
    startLocationCol: Int
  ) {
    val south = startLocationRow < area.size - 1 && listOf(
      NS_PIPE, NW_PIPE, NE_PIPE
    ).contains(area[startLocationRow + 1][startLocationCol])
    val north = startLocationRow > 0 && listOf(
      NS_PIPE, SW_PIPE, SE_PIPE
    ).contains(area[startLocationRow - 1][startLocationCol])
    val west = startLocationCol > 0 && listOf(
      EW_PIPE, SE_PIPE, NE_PIPE
    ).contains(area[startLocationRow][startLocationCol - 1])
    val east = startLocationCol < area[startLocationRow].size - 1 && listOf(
      EW_PIPE, SW_PIPE, NW_PIPE
    ).contains(area[startLocationRow][startLocationCol + 1])

    if (south && north) {
      area[startLocationRow][startLocationCol] = NS_PIPE
    } else if (south && east) {
      area[startLocationRow][startLocationCol] = SE_PIPE
    } else if (south && west) {
      area[startLocationRow][startLocationCol] = SW_PIPE
    } else if (north && east) {
      area[startLocationRow][startLocationCol] = NE_PIPE
    } else if (north && west) {
      area[startLocationRow][startLocationCol] = NW_PIPE
    } else if (east && west) {
      area[startLocationRow][startLocationCol] = EW_PIPE
    } else {
      throw IllegalStateException("Unknown Starting Pipe Type")
    }
  }

  private fun findOurPipes(
    area: MutableList<MutableList<LandType>>,
    startLocationRow: Int,
    startLocationCol: Int
  ): Map<Location, Int> {
    val stepsToTake: Queue<StepData> = LinkedList()
    val startLocation = Location(startLocationRow, startLocationCol)
    val locationDistances = mutableMapOf<Location, Int>().apply {
      this[Location(startLocationRow, startLocationCol)] = 0
    }

    val travelNorth by lazy { StepData(directionCameFrom = S, startLocation.goNorth()) }
    val travelSouth by lazy { StepData(directionCameFrom = N, startLocation.goSouth()) }
    val travelEast by lazy { StepData(directionCameFrom = W, startLocation.goEast()) }
    val travelWest by lazy { StepData(directionCameFrom = E, startLocation.goWest()) }

    // Create the seed directions to travel
    when (area[startLocationRow][startLocationCol]) {
      NS_PIPE -> {
        stepsToTake.add(travelSouth)
        stepsToTake.add(travelNorth)
      }
      EW_PIPE -> {
        stepsToTake.add(travelEast)
        stepsToTake.add(travelWest)
      }
      NE_PIPE -> {
        stepsToTake.add(travelSouth)
        stepsToTake.add(travelEast)
      }
      NW_PIPE -> {
        stepsToTake.add(travelSouth)
        stepsToTake.add(travelWest)
      }
      SW_PIPE -> {
        stepsToTake.add(travelWest)
        stepsToTake.add(travelNorth)
      }
      SE_PIPE -> {
        stepsToTake.add(travelNorth)
        stepsToTake.add(travelEast)
      }
      else -> throw IllegalStateException("Unknown Start Pipe")
    }

    // while we have another direction to explore - go explore the pipe!
    while (stepsToTake.isNotEmpty()) {
      val (directionCameFrom, location, stepCount, _) = stepsToTake.remove()

      if (!locationDistances.containsKey(location)) {
        locationDistances[location] = stepCount

        when (area[location.row][location.col]) {
          NS_PIPE -> {
            if (directionCameFrom == N) {
              stepsToTake.add(StepData(N, location.goSouth(), stepCount + 1))
            } else if (directionCameFrom == S) {
              stepsToTake.add(StepData(S, location.goNorth(), stepCount + 1))
            }
          }
          EW_PIPE -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(W, location.goEast(), stepCount + 1))
            } else if (directionCameFrom == E) {
              stepsToTake.add(StepData(E, location.goWest(), stepCount + 1))
            }
          }
          NE_PIPE -> {
            if (directionCameFrom == E) {
              stepsToTake.add(StepData(S, location.goNorth(), stepCount + 1))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(W, location.goEast(), stepCount + 1))
            }
          }
          NW_PIPE -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(S, location.goNorth(), stepCount + 1))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(E, location.goWest(), stepCount + 1))
            }
          }
          SW_PIPE -> {
            if (directionCameFrom == S) {
              stepsToTake.add(StepData(E, location.goWest(), stepCount + 1))
            } else if (directionCameFrom == W) {
              stepsToTake.add(StepData(N, location.goSouth(), stepCount + 1))
            }
          }
          SE_PIPE -> {
            if (directionCameFrom == E) {
              stepsToTake.add(StepData(N, location.goSouth(), stepCount + 1))
            } else if (directionCameFrom == S) {
              stepsToTake.add(StepData(W, location.goEast(), stepCount + 1))
            }
          }
          else -> throw IllegalStateException("Unknown Start Pipe")
        }
      }
    }

    return locationDistances
  }

  private fun things2(area: MutableList<MutableList<LandType>>) {
    area.forEachIndexed { r, row ->
      row.forEachIndexed { c, pipe ->
        if (pipe == GROUND) {
          if (r > 0 && area[r-1][c] == INSIDE) {
            area[r][c] = INSIDE
          }
          if (r < area.size - 1 && area[r+1][c] == INSIDE) {
            area[r][c] = INSIDE
          }
          if (c > 0 && area[r][c-1] == INSIDE) {
            area[r][c] = INSIDE
          }
          if (c < area[r].size - 1 && area[r][c+1] == INSIDE) {
            area[r][c] = INSIDE
          }
        }
      }
    }
  }

  private fun clearExtraneousPipe(area: MutableList<MutableList<LandType>>, locationMap: Map<Location, Int>) {
    area.forEachIndexed { rowIndex , row ->
      row.indices.forEach { colIndex ->
        if (!locationMap.containsKey(Location(rowIndex, colIndex))) {
          area[rowIndex][colIndex] = GROUND
        }
      }
    }
  }

  fun things(area: MutableList<MutableList<LandType>>, startLocationCol: Int, startLocationRow: Int) {
    val stepsToTake: Queue<StepData> = LinkedList()
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      NS_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
      }
      EW_PIPE -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.N)))
      }
      NE_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.NE)))
      }
      NW_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.NW)))
      }
      SW_PIPE -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SW)))
      }
      SE_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.SE)))
      }
      else -> throw IllegalStateException("Unknown Start Pipe")
    }

    locationDistances[Location(startLocationRow, startLocationCol)] = 0

    while (stepsToTake.isNotEmpty()) {
      val (directionCameFrom, location, stepCount, insideDirections) = stepsToTake.remove()

      if (!locationDistances.containsKey(location)) {

        locationDistances[location] = stepCount

        val myInsides: List<FloodFillDirection>
        when (area[location.row][location.col]) {
          NS_PIPE -> {
            myInsides = if (insideDirections.any { listOf(FloodFillDirection.E, FloodFillDirection.NE, FloodFillDirection.SE).contains(it) }) {
              listOf(FloodFillDirection.E)
            } else if (insideDirections.any { listOf(FloodFillDirection.W, FloodFillDirection.NW, FloodFillDirection.SW).contains(it) }) {
              listOf(FloodFillDirection.W)
            } else {
              emptyList()
            }
            if (directionCameFrom == N) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == S) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, myInsides))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          EW_PIPE -> {
            myInsides = if (insideDirections.any { listOf(FloodFillDirection.N, FloodFillDirection.NE, FloodFillDirection.NW).contains(it) }) {
              listOf(FloodFillDirection.N)
            } else if (insideDirections.any { listOf(FloodFillDirection.S, FloodFillDirection.SW, FloodFillDirection.SE).contains(it) }) {
              listOf(FloodFillDirection.S)
            } else {
              emptyList()
            }
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, myInsides))
            } else if (directionCameFrom == E) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, myInsides))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          NE_PIPE -> {
            if (directionCameFrom == E) {
              myInsides = if (insideDirections.any { listOf(FloodFillDirection.N, FloodFillDirection.NE, FloodFillDirection.NW).contains(it) }) {
                listOf(FloodFillDirection.NE)
              } else if (insideDirections.any { listOf(FloodFillDirection.S, FloodFillDirection.SE, FloodFillDirection.SW).contains(it) }) {
                listOf(FloodFillDirection.S, FloodFillDirection.SW, FloodFillDirection.W)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == N) {
              myInsides = if(insideDirections.any { listOf(FloodFillDirection.W, FloodFillDirection.SW, FloodFillDirection.NW).contains(it) }) {
                listOf(FloodFillDirection.W, FloodFillDirection.SW, FloodFillDirection.S)
              } else if (insideDirections.any { listOf(FloodFillDirection.E, FloodFillDirection.NE, FloodFillDirection.SE).contains(it) }) {
                listOf(FloodFillDirection.NE)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, myInsides))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          NW_PIPE -> {
            if (directionCameFrom == W) {
              myInsides = if(insideDirections.any { listOf(FloodFillDirection.N, FloodFillDirection.NE, FloodFillDirection.NW).contains(it) }) {
                listOf(FloodFillDirection.NW)
              } else if (insideDirections.any { listOf(FloodFillDirection.S, FloodFillDirection.SW, FloodFillDirection.SE).contains(it) }) {
                listOf(FloodFillDirection.S, FloodFillDirection.SE, FloodFillDirection.E)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == N) {
              myInsides = if(insideDirections.any { listOf(FloodFillDirection.W, FloodFillDirection.NW, FloodFillDirection.SW).contains(it) }) {
                listOf(FloodFillDirection.NW)
              } else if (insideDirections.any { listOf(FloodFillDirection.E, FloodFillDirection.NE, FloodFillDirection.SE).contains(it) }) {
                listOf(FloodFillDirection.S, FloodFillDirection.SE, FloodFillDirection.E)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, myInsides))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          SW_PIPE -> {
            if (directionCameFrom == S) {
              myInsides = if(insideDirections.any { listOf(FloodFillDirection.E, FloodFillDirection.NE, FloodFillDirection.SE).contains(it) }) {
                listOf(FloodFillDirection.E, FloodFillDirection.NE, FloodFillDirection.N)
              } else if (insideDirections.any { listOf(FloodFillDirection.W, FloodFillDirection.NW, FloodFillDirection.SW).contains(it) }) {
                listOf(FloodFillDirection.SW)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, myInsides))
            } else if (directionCameFrom == W) {
              myInsides = if(insideDirections.any { listOf(FloodFillDirection.N, FloodFillDirection.NE, FloodFillDirection.NW).contains(it) }) {
                listOf(FloodFillDirection.N, FloodFillDirection.NE, FloodFillDirection.E)
              } else if (insideDirections.any { listOf(FloodFillDirection.S, FloodFillDirection.SW, FloodFillDirection.SE).contains(it) }) {
                listOf(FloodFillDirection.SW)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, myInsides))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          SE_PIPE -> {
            if (directionCameFrom == E) {
              myInsides = if(insideDirections.any { listOf(FloodFillDirection.N, FloodFillDirection.NE, FloodFillDirection.NW).contains(it) }) {
                listOf(FloodFillDirection.N, FloodFillDirection.NW, FloodFillDirection.W)
              } else if (insideDirections.any { listOf(FloodFillDirection.S, FloodFillDirection.SE, FloodFillDirection.SW).contains(it) }) {
                listOf(FloodFillDirection.SE)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == S) {
              myInsides = if(insideDirections.any { listOf(FloodFillDirection.W, FloodFillDirection.NW, FloodFillDirection.SW).contains(it) }) {
                listOf(FloodFillDirection.W, FloodFillDirection.NW, FloodFillDirection.N)
              } else if (insideDirections.any { listOf(FloodFillDirection.E, FloodFillDirection.SE, FloodFillDirection.NE).contains(it) }) {
                listOf(FloodFillDirection.SE)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, myInsides))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          else -> throw IllegalStateException("Unknown Start Pipe")
        }
        myInsides.forEach { myInside ->
          when (myInside) {
            FloodFillDirection.NE -> {
              if (
                location.row > 0 &&
                location.col < area[location.row].size - 1 &&
                area[location.row - 1][location.col + 1] == GROUND
              ) {
                area[location.row - 1][location.col + 1] = INSIDE
              }
            }

            FloodFillDirection.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == GROUND
              ) {
                area[location.row - 1][location.col - 1] = INSIDE
              }
            }

            FloodFillDirection.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == GROUND
              ) {
                area[location.row + 1][location.col + 1] = INSIDE
              }
            }

            FloodFillDirection.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == GROUND
              ) {
                area[location.row + 1][location.col - 1] = INSIDE
              }
            }

            FloodFillDirection.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == GROUND
              ) {
                area[location.row - 1][location.col] = INSIDE
              }
            }

            FloodFillDirection.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == GROUND
              ) {
                area[location.row + 1][location.col] = INSIDE
              }
            }

            FloodFillDirection.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == GROUND
              ) {
                area[location.row][location.col + 1] = INSIDE
              }
            }

            FloodFillDirection.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == GROUND
              ) {
                area[location.row][location.col - 1] = INSIDE
              }
            }

            FloodFillDirection.UNKNOWN -> { /* save for later */ }
          }
        }
      }
    }
  }

  data class StepData(
    val directionCameFrom: DirectionCameFrom,
    val location: Location,
    val stepCount: Int = 1,
    val insideDirections: List<FloodFillDirection> = emptyList()
  )

  data class Location(val row: Int, val col: Int) {
    fun goNorth() = Location(row - 1, col)
    fun goSouth() = Location(row + 1, col)
    fun goEast() = Location(row, col + 1)
    fun goWest() = Location(row, col - 1)
  }


  enum class DirectionCameFrom { N, S, E, W }

  enum class FloodFillDirection { NE, NW, SE, SW, N, S, E, W, UNKNOWN }

  enum class LandType(private val symbol: Char) {
    NS_PIPE('|'),
    EW_PIPE('-'),
    NE_PIPE('L'),
    NW_PIPE('J'),
    SW_PIPE('7'),
    SE_PIPE('F'),
    GROUND('.'),
    START('S'),
    INSIDE('I');

    override fun toString(): String = symbol.toString()

    companion object {
      fun fromSymbol(symbol: Char): LandType = values().first { it.symbol == symbol }
    }
  }
}
