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
    val area = mutableListOf<MutableList<LAND_TYPE>>()

    var startLocationRow = -1
    var startLocationCol = -1
    input.forEachIndexed { rowIndex, row ->
      val pipeRow = mutableListOf<LAND_TYPE>()
      row.forEachIndexed { colIndex, c ->
        LAND_TYPE.fromSymbol(c).also { pipe ->
          pipeRow.add(pipe)
          if (pipe == LAND_TYPE.START) {
            startLocationRow = rowIndex
            startLocationCol = colIndex
          }
        }
      }
      area.add(pipeRow)
    }

    val south = startLocationRow < area.size - 1 && listOf(
      LAND_TYPE.NS_PIPE, LAND_TYPE.NW_PIPE, LAND_TYPE.NE_PIPE
    ).contains(area[startLocationRow + 1][startLocationCol])
    val north = startLocationRow > 0 && listOf(
      LAND_TYPE.NS_PIPE, LAND_TYPE.SW_PIPE, LAND_TYPE.SE_PIPE
    ).contains(area[startLocationRow - 1][startLocationCol])
    val west = startLocationCol > 0 && listOf(
      LAND_TYPE.EW_PIPE, LAND_TYPE.SE_PIPE, LAND_TYPE.NE_PIPE
    ).contains(area[startLocationRow][startLocationCol - 1])
    val east = startLocationCol < area[startLocationRow].size - 1 && listOf(
      LAND_TYPE.EW_PIPE, LAND_TYPE.SW_PIPE, LAND_TYPE.NW_PIPE
    ).contains(area[startLocationRow][startLocationCol + 1])

    if (south && north) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.NS_PIPE
    } else if (south && east) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.SE_PIPE
    } else if (south && west) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.SW_PIPE
    } else if (north && east) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.NE_PIPE
    } else if (north && west) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.NW_PIPE
    } else if (east && west) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.EW_PIPE
    } else {
      throw IllegalStateException("Unknown Starting Pipe Type")
    }

    val stepsToTake: Queue<StepData> = LinkedList()
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      // These directions are NOT generic, and specific to my solution - but I can likely find a proper direction after splitting
      // clear and flood fill algorithms
      LAND_TYPE.NS_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
      }
      LAND_TYPE.EW_PIPE -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.N)))
      }
      LAND_TYPE.NE_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.NE)))
      }
      LAND_TYPE.NW_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.NW)))
      }
      LAND_TYPE.SW_PIPE -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SW)))
      }
      LAND_TYPE.SE_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.SE)))
      }
      else -> throw IllegalStateException("Unknown Start Pipe")
    }

    while (stepsToTake.isNotEmpty()) {
      val (directionCameFrom, location, stepCount, _) = stepsToTake.remove()

      if (!locationDistances.containsKey(location)) {

        locationDistances[location] = stepCount

        when (area[location.row][location.col]) {
          LAND_TYPE.NS_PIPE -> {
            if (directionCameFrom == N) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == S) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, emptyList()))
            }
          }

          LAND_TYPE.EW_PIPE -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, emptyList()))
            } else if (directionCameFrom == E) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, emptyList()))
            }
          }

          LAND_TYPE.NE_PIPE -> {
            if (directionCameFrom == E) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, emptyList()))
            }
          }

          LAND_TYPE.NW_PIPE -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, emptyList()))
            }
          }

          LAND_TYPE.SW_PIPE -> {
            if (directionCameFrom == S) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, emptyList()))
            } else if (directionCameFrom == W) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, emptyList()))
            }
          }

          LAND_TYPE.SE_PIPE -> {
            if (directionCameFrom == E) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == S) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, emptyList()))
            }
          }

          else -> throw IllegalStateException("Unknown Start Pipe")
        }
      }
    }

    locationDistances.values.maxOf { it }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val area = mutableListOf<MutableList<LAND_TYPE>>()

    var startLocationRow = -1
    var startLocationCol = -1
    input.forEachIndexed { rowIndex, row ->
      val pipeRow = mutableListOf<LAND_TYPE>()
      row.forEachIndexed { colIndex, c ->
        LAND_TYPE.fromSymbol(c).also { pipe ->
          pipeRow.add(pipe)
          if (pipe == LAND_TYPE.START) {
            startLocationRow = rowIndex
            startLocationCol = colIndex
          }
        }
      }
      area.add(pipeRow)
    }

    val south = startLocationRow < area.size - 1 && listOf(
      LAND_TYPE.NS_PIPE, LAND_TYPE.NW_PIPE, LAND_TYPE.NE_PIPE
    ).contains(area[startLocationRow + 1][startLocationCol])
    val north = startLocationRow > 0 && listOf(
      LAND_TYPE.NS_PIPE, LAND_TYPE.SW_PIPE, LAND_TYPE.SE_PIPE
    ).contains(area[startLocationRow - 1][startLocationCol])
    val west = startLocationCol > 0 && listOf(
      LAND_TYPE.EW_PIPE, LAND_TYPE.SE_PIPE, LAND_TYPE.NE_PIPE
    ).contains(area[startLocationRow][startLocationCol - 1])
    val east = startLocationCol < area[startLocationRow].size - 1 && listOf(
      LAND_TYPE.EW_PIPE, LAND_TYPE.SW_PIPE, LAND_TYPE.NW_PIPE
    ).contains(area[startLocationRow][startLocationCol + 1])

    if (south && north) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.NS_PIPE
    } else if (south && east) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.SE_PIPE
    } else if (south && west) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.SW_PIPE
    } else if (north && east) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.NE_PIPE
    } else if (north && west) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.NW_PIPE
    } else if (east && west) {
      area[startLocationRow][startLocationCol] = LAND_TYPE.EW_PIPE
    } else {
      throw IllegalStateException("Unknown Starting Pipe Type")
    }

    val stepsToTake: Queue<StepData> = LinkedList()
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      LAND_TYPE.NS_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
      }
      LAND_TYPE.EW_PIPE -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.N)))
      }
      LAND_TYPE.NE_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.NE)))
      }
      LAND_TYPE.NW_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.NW)))
      }
      LAND_TYPE.SW_PIPE -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SW)))
      }
      LAND_TYPE.SE_PIPE -> {
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
          LAND_TYPE.NS_PIPE -> {
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

          LAND_TYPE.EW_PIPE -> {
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

          LAND_TYPE.NE_PIPE -> {
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

          LAND_TYPE.NW_PIPE -> {
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

          LAND_TYPE.SW_PIPE -> {
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

          LAND_TYPE.SE_PIPE -> {
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
                area[location.row - 1][location.col + 1] == LAND_TYPE.GROUND
              ) {
                area[location.row - 1][location.col + 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == LAND_TYPE.GROUND
              ) {
                area[location.row - 1][location.col - 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == LAND_TYPE.GROUND
              ) {
                area[location.row + 1][location.col + 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == LAND_TYPE.GROUND
              ) {
                area[location.row + 1][location.col - 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == LAND_TYPE.GROUND
              ) {
                area[location.row - 1][location.col] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == LAND_TYPE.GROUND
              ) {
                area[location.row + 1][location.col] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == LAND_TYPE.GROUND
              ) {
                area[location.row][location.col + 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == LAND_TYPE.GROUND
              ) {
                area[location.row][location.col - 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.UNKNOWN -> { /* save for later */ }
          }
        }
      }
    }

    area.forEachIndexed { rowIndex , row ->
      row.forEachIndexed { colIndex, col ->
        if (!locationDistances.containsKey(Location(rowIndex, colIndex)) && area[rowIndex][colIndex] != LAND_TYPE.INSIDE) {
          area[rowIndex][colIndex] = LAND_TYPE.GROUND
        }
      }
    }

    things(area, startLocationCol, startLocationRow)
    things2(area)

    area.sumOf { row ->
      row.count { it == LAND_TYPE.INSIDE }
    }
  }

  private fun things2(area: MutableList<MutableList<LAND_TYPE>>) {
    area.forEachIndexed { r, row ->
      row.forEachIndexed { c, pipe ->
        if (pipe == LAND_TYPE.GROUND) {
          if (r > 0 && area[r-1][c] == LAND_TYPE.INSIDE) {
            area[r][c] = LAND_TYPE.INSIDE
          }
          if (r < area.size - 1 && area[r+1][c] == LAND_TYPE.INSIDE) {
            area[r][c] = LAND_TYPE.INSIDE
          }
          if (c > 0 && area[r][c-1] == LAND_TYPE.INSIDE) {
            area[r][c] = LAND_TYPE.INSIDE
          }
          if (c < area[r].size - 1 && area[r][c+1] == LAND_TYPE.INSIDE) {
            area[r][c] = LAND_TYPE.INSIDE
          }
        }
      }
    }
  }

  fun things(area: MutableList<MutableList<LAND_TYPE>>, startLocationCol: Int, startLocationRow: Int) {
    val stepsToTake: Queue<StepData> = LinkedList()
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      LAND_TYPE.NS_PIPE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
      }
      LAND_TYPE.EW_PIPE -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.N)))
      }
      LAND_TYPE.NE_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.NE)))
      }
      LAND_TYPE.NW_PIPE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.NW)))
      }
      LAND_TYPE.SW_PIPE -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SW)))
      }
      LAND_TYPE.SE_PIPE -> {
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
          LAND_TYPE.NS_PIPE -> {
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

          LAND_TYPE.EW_PIPE -> {
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

          LAND_TYPE.NE_PIPE -> {
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

          LAND_TYPE.NW_PIPE -> {
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

          LAND_TYPE.SW_PIPE -> {
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

          LAND_TYPE.SE_PIPE -> {
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
                area[location.row - 1][location.col + 1] == LAND_TYPE.GROUND
              ) {
                area[location.row - 1][location.col + 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == LAND_TYPE.GROUND
              ) {
                area[location.row - 1][location.col - 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == LAND_TYPE.GROUND
              ) {
                area[location.row + 1][location.col + 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == LAND_TYPE.GROUND
              ) {
                area[location.row + 1][location.col - 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == LAND_TYPE.GROUND
              ) {
                area[location.row - 1][location.col] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == LAND_TYPE.GROUND
              ) {
                area[location.row + 1][location.col] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == LAND_TYPE.GROUND
              ) {
                area[location.row][location.col + 1] = LAND_TYPE.INSIDE
              }
            }

            FloodFillDirection.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == LAND_TYPE.GROUND
              ) {
                area[location.row][location.col - 1] = LAND_TYPE.INSIDE
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
    val insideDirections: List<FloodFillDirection>
  )

  data class Location(val row: Int, val col: Int)

  enum class DirectionCameFrom { N, S, E, W }

  enum class FloodFillDirection { NE, NW, SE, SW, N, S, E, W, UNKNOWN }

  enum class LAND_TYPE(private val symbol: Char) {
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
      fun fromSymbol(symbol: Char): LAND_TYPE = values().first { it.symbol == symbol }
    }
  }
}
