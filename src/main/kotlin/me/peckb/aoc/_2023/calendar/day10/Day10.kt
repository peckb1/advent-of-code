package me.peckb.aoc._2023.calendar.day10

import me.peckb.aoc._2023.calendar.day10.Day10.DirectionCameFrom.*
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
      LandType.NS_PIPE, LandType.NW_PIPE, LandType.NE_PIPE
    ).contains(area[startLocationRow + 1][startLocationCol])
    val north = startLocationRow > 0 && listOf(
      LandType.NS_PIPE, LandType.SW_PIPE, LandType.SE_PIPE
    ).contains(area[startLocationRow - 1][startLocationCol])
    val west = startLocationCol > 0 && listOf(
      LandType.EW_PIPE, LandType.SE_PIPE, LandType.NE_PIPE
    ).contains(area[startLocationRow][startLocationCol - 1])
    val east = startLocationCol < area[startLocationRow].size - 1 && listOf(
      LandType.EW_PIPE, LandType.SW_PIPE, LandType.NW_PIPE
    ).contains(area[startLocationRow][startLocationCol + 1])

    if (south && north) {
      area[startLocationRow][startLocationCol] = LandType.NS_PIPE
    } else if (south && east) {
      area[startLocationRow][startLocationCol] = LandType.SE_PIPE
    } else if (south && west) {
      area[startLocationRow][startLocationCol] = LandType.SW_PIPE
    } else if (north && east) {
      area[startLocationRow][startLocationCol] = LandType.NE_PIPE
    } else if (north && west) {
      area[startLocationRow][startLocationCol] = LandType.NW_PIPE
    } else if (east && west) {
      area[startLocationRow][startLocationCol] = LandType.EW_PIPE
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
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      LandType.NS_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1))
      }
      LandType.EW_PIPE -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1))
      }
      LandType.NE_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1))
      }
      LandType.NW_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1))
      }
      LandType.SW_PIPE -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1))
      }
      LandType.SE_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1))
      }
      else -> throw IllegalStateException("Unknown Start Pipe")
    }

    while (stepsToTake.isNotEmpty()) {
      val (directionCameFrom, location, stepCount, _) = stepsToTake.remove()

      if (!locationDistances.containsKey(location)) {

        locationDistances[location] = stepCount

        when (area[location.row][location.col]) {
          LandType.NS_PIPE -> {
            if (directionCameFrom == N) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1))
            } else if (directionCameFrom == S) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1))
            }
          }

          LandType.EW_PIPE -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1))
            } else if (directionCameFrom == E) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1))
            }
          }

          LandType.NE_PIPE -> {
            if (directionCameFrom == E) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1))
            }
          }

          LandType.NW_PIPE -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1))
            }
          }

          LandType.SW_PIPE -> {
            if (directionCameFrom == S) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1))
            } else if (directionCameFrom == W) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1))
            }
          }

          LandType.SE_PIPE -> {
            if (directionCameFrom == E) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1))
            } else if (directionCameFrom == S) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1))
            }
          }

          else -> throw IllegalStateException("Unknown Start Pipe")
        }
      }
    }

    return locationDistances
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (area, startLocationRow, startLocationCol) = generateArea(input)

    val stepsToTake: Queue<StepData> = LinkedList()
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      LandType.NS_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
      }
      LandType.EW_PIPE -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.N)))
      }
      LandType.NE_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.NE)))
      }
      LandType.NW_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.NW)))
      }
      LandType.SW_PIPE -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SW)))
      }
      LandType.SE_PIPE -> {
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
          LandType.NS_PIPE -> {
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

          LandType.EW_PIPE -> {
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

          LandType.NE_PIPE -> {
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

          LandType.NW_PIPE -> {
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

          LandType.SW_PIPE -> {
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

          LandType.SE_PIPE -> {
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
                area[location.row - 1][location.col + 1] == LandType.GROUND
              ) {
                area[location.row - 1][location.col + 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == LandType.GROUND
              ) {
                area[location.row - 1][location.col - 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == LandType.GROUND
              ) {
                area[location.row + 1][location.col + 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == LandType.GROUND
              ) {
                area[location.row + 1][location.col - 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == LandType.GROUND
              ) {
                area[location.row - 1][location.col] = LandType.INSIDE
              }
            }

            FloodFillDirection.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == LandType.GROUND
              ) {
                area[location.row + 1][location.col] = LandType.INSIDE
              }
            }

            FloodFillDirection.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == LandType.GROUND
              ) {
                area[location.row][location.col + 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == LandType.GROUND
              ) {
                area[location.row][location.col - 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.UNKNOWN -> { /* save for later */ }
          }
        }
      }
    }

    area.forEachIndexed { rowIndex , row ->
      row.forEachIndexed { colIndex, col ->
        if (!locationDistances.containsKey(Location(rowIndex, colIndex)) && area[rowIndex][colIndex] != LandType.INSIDE) {
          area[rowIndex][colIndex] = LandType.GROUND
        }
      }
    }

    things(area, startLocationCol, startLocationRow)
    things2(area)

    area.sumOf { row ->
      row.count { it == LandType.INSIDE }
    }
  }

  private fun things2(area: MutableList<MutableList<LandType>>) {
    area.forEachIndexed { r, row ->
      row.forEachIndexed { c, pipe ->
        if (pipe == LandType.GROUND) {
          if (r > 0 && area[r-1][c] == LandType.INSIDE) {
            area[r][c] = LandType.INSIDE
          }
          if (r < area.size - 1 && area[r+1][c] == LandType.INSIDE) {
            area[r][c] = LandType.INSIDE
          }
          if (c > 0 && area[r][c-1] == LandType.INSIDE) {
            area[r][c] = LandType.INSIDE
          }
          if (c < area[r].size - 1 && area[r][c+1] == LandType.INSIDE) {
            area[r][c] = LandType.INSIDE
          }
        }
      }
    }
  }

  fun things(area: MutableList<MutableList<LandType>>, startLocationCol: Int, startLocationRow: Int) {
    val stepsToTake: Queue<StepData> = LinkedList()
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      LandType.NS_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
      }
      LandType.EW_PIPE -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.N)))
      }
      LandType.NE_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.NE)))
      }
      LandType.NW_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.NW)))
      }
      LandType.SW_PIPE -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SW)))
      }
      LandType.SE_PIPE -> {
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
          LandType.NS_PIPE -> {
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

          LandType.EW_PIPE -> {
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

          LandType.NE_PIPE -> {
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

          LandType.NW_PIPE -> {
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

          LandType.SW_PIPE -> {
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

          LandType.SE_PIPE -> {
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
                area[location.row - 1][location.col + 1] == LandType.GROUND
              ) {
                area[location.row - 1][location.col + 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == LandType.GROUND
              ) {
                area[location.row - 1][location.col - 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == LandType.GROUND
              ) {
                area[location.row + 1][location.col + 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == LandType.GROUND
              ) {
                area[location.row + 1][location.col - 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == LandType.GROUND
              ) {
                area[location.row - 1][location.col] = LandType.INSIDE
              }
            }

            FloodFillDirection.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == LandType.GROUND
              ) {
                area[location.row + 1][location.col] = LandType.INSIDE
              }
            }

            FloodFillDirection.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == LandType.GROUND
              ) {
                area[location.row][location.col + 1] = LandType.INSIDE
              }
            }

            FloodFillDirection.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == LandType.GROUND
              ) {
                area[location.row][location.col - 1] = LandType.INSIDE
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
    val stepCount: Int,
    val insideDirections: List<FloodFillDirection> = emptyList()
  )

  data class Location(val row: Int, val col: Int)

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
