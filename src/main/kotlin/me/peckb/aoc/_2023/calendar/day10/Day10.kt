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
    val area = mutableListOf<MutableList<Pipe>>()

    var startLocationRow = -1
    var startLocationCol = -1
    input.forEachIndexed { rowIndex, row ->
      val pipeRow = mutableListOf<Pipe>()
      row.forEachIndexed { colIndex, c ->
        Pipe.fromSymbol(c).also { pipe ->
          pipeRow.add(pipe)
          if (pipe == Pipe.UNKNOWN) {
            startLocationRow = rowIndex
            startLocationCol = colIndex
          }
        }
      }
      area.add(pipeRow)
    }

    val south = startLocationRow < area.size - 1 && listOf(
      Pipe.NS, Pipe.NW, Pipe.NE
    ).contains(area[startLocationRow + 1][startLocationCol])
    val north = startLocationRow > 0 && listOf(
      Pipe.NS, Pipe.SW, Pipe.SE
    ).contains(area[startLocationRow - 1][startLocationCol])
    val west = startLocationCol > 0 && listOf(
      Pipe.EW, Pipe.SE, Pipe.NE
    ).contains(area[startLocationRow][startLocationCol - 1])
    val east = startLocationCol < area[startLocationRow].size - 1 && listOf(
      Pipe.EW, Pipe.SW, Pipe.NW
    ).contains(area[startLocationRow][startLocationCol + 1])

    if (south && north) {
      area[startLocationRow][startLocationCol] = Pipe.NS
    } else if (south && east) {
      area[startLocationRow][startLocationCol] = Pipe.SE
    } else if (south && west) {
      area[startLocationRow][startLocationCol] = Pipe.SW
    } else if (north && east) {
      area[startLocationRow][startLocationCol] = Pipe.NE
    } else if (north && west) {
      area[startLocationRow][startLocationCol] = Pipe.NW
    } else if (east && west) {
      area[startLocationRow][startLocationCol] = Pipe.EW
    } else {
      throw IllegalStateException("Unknown Starting Pipe Type")
    }

    val stepsToTake: Queue<StepData> = LinkedList()
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      // These directions are NOT generic, and specific to my solution - but I can likely find a proper direction after splitting
      // clear and flood fill algorithms
      Pipe.NS -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
      }
      Pipe.EW -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.N)))
      }
      Pipe.NE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.NE)))
      }
      Pipe.NW -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.NW)))
      }
      Pipe.SW -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SW)))
      }
      Pipe.SE -> {
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
          Pipe.NS -> {
            if (directionCameFrom == N) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == S) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, emptyList()))
            }
          }

          Pipe.EW -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, emptyList()))
            } else if (directionCameFrom == E) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, emptyList()))
            }
          }

          Pipe.NE -> {
            if (directionCameFrom == E) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, emptyList()))
            }
          }

          Pipe.NW -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, emptyList()))
            }
          }

          Pipe.SW -> {
            if (directionCameFrom == S) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, emptyList()))
            } else if (directionCameFrom == W) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, emptyList()))
            }
          }

          Pipe.SE -> {
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
    val area = mutableListOf<MutableList<Pipe>>()

    var startLocationRow = -1
    var startLocationCol = -1
    input.forEachIndexed { rowIndex, row ->
      val pipeRow = mutableListOf<Pipe>()
      row.forEachIndexed { colIndex, c ->
        Pipe.fromSymbol(c).also { pipe ->
          pipeRow.add(pipe)
          if (pipe == Pipe.UNKNOWN) {
            startLocationRow = rowIndex
            startLocationCol = colIndex
          }
        }
      }
      area.add(pipeRow)
    }

    val south = startLocationRow < area.size - 1 && listOf(
      Pipe.NS, Pipe.NW, Pipe.NE
    ).contains(area[startLocationRow + 1][startLocationCol])
    val north = startLocationRow > 0 && listOf(
      Pipe.NS, Pipe.SW, Pipe.SE
    ).contains(area[startLocationRow - 1][startLocationCol])
    val west = startLocationCol > 0 && listOf(
      Pipe.EW, Pipe.SE, Pipe.NE
    ).contains(area[startLocationRow][startLocationCol - 1])
    val east = startLocationCol < area[startLocationRow].size - 1 && listOf(
      Pipe.EW, Pipe.SW, Pipe.NW
    ).contains(area[startLocationRow][startLocationCol + 1])

    if (south && north) {
      area[startLocationRow][startLocationCol] = Pipe.NS
    } else if (south && east) {
      area[startLocationRow][startLocationCol] = Pipe.SE
    } else if (south && west) {
      area[startLocationRow][startLocationCol] = Pipe.SW
    } else if (north && east) {
      area[startLocationRow][startLocationCol] = Pipe.NE
    } else if (north && west) {
      area[startLocationRow][startLocationCol] = Pipe.NW
    } else if (east && west) {
      area[startLocationRow][startLocationCol] = Pipe.EW
    } else {
      throw IllegalStateException("Unknown Starting Pipe Type")
    }

    val stepsToTake: Queue<StepData> = LinkedList()
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      Pipe.NS -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
      }
      Pipe.EW -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.N)))
      }
      Pipe.NE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.NE)))
      }
      Pipe.NW -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.NW)))
      }
      Pipe.SW -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SW)))
      }
      Pipe.SE -> {
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
          Pipe.NS -> {
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

          Pipe.EW -> {
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

          Pipe.NE -> {
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

          Pipe.NW -> {
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

          Pipe.SW -> {
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

          Pipe.SE -> {
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
                area[location.row - 1][location.col + 1] == Pipe.NONE
              ) {
                area[location.row - 1][location.col + 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == Pipe.NONE
              ) {
                area[location.row - 1][location.col - 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == Pipe.NONE
              ) {
                area[location.row + 1][location.col + 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == Pipe.NONE
              ) {
                area[location.row + 1][location.col - 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == Pipe.NONE
              ) {
                area[location.row - 1][location.col] = Pipe.INSIDE
              }
            }

            FloodFillDirection.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == Pipe.NONE
              ) {
                area[location.row + 1][location.col] = Pipe.INSIDE
              }
            }

            FloodFillDirection.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == Pipe.NONE
              ) {
                area[location.row][location.col + 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == Pipe.NONE
              ) {
                area[location.row][location.col - 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.UNKNOWN -> { /* save for later */ }
          }
        }
      }
    }

    area.forEachIndexed { rowIndex , row ->
      row.forEachIndexed { colIndex, col ->
        if (!locationDistances.containsKey(Location(rowIndex, colIndex)) && area[rowIndex][colIndex] != Pipe.INSIDE) {
          area[rowIndex][colIndex] = Pipe.NONE
        }
      }
    }

    things(area, startLocationCol, startLocationRow)
    things2(area)

    area.sumOf { row ->
      row.count { it == Pipe.INSIDE }
    }
  }

  private fun things2(area: MutableList<MutableList<Pipe>>) {
    area.forEachIndexed { r, row ->
      row.forEachIndexed { c, pipe ->
        if (pipe == Pipe.NONE) {
          if (r > 0 && area[r-1][c] == Pipe.INSIDE) {
            area[r][c] = Pipe.INSIDE
          }
          if (r < area.size - 1 && area[r+1][c] == Pipe.INSIDE) {
            area[r][c] = Pipe.INSIDE
          }
          if (c > 0 && area[r][c-1] == Pipe.INSIDE) {
            area[r][c] = Pipe.INSIDE
          }
          if (c < area[r].size - 1 && area[r][c+1] == Pipe.INSIDE) {
            area[r][c] = Pipe.INSIDE
          }
        }
      }
    }
  }

  fun things(area: MutableList<MutableList<Pipe>>, startLocationCol: Int, startLocationRow: Int) {
    val stepsToTake: Queue<StepData> = LinkedList()
    val locationDistances = mutableMapOf<Location, Int>()

    when (area[startLocationRow][startLocationCol]) {
      Pipe.NS -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.W)))
      }
      Pipe.EW -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.N)))
      }
      Pipe.NE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(FloodFillDirection.NE)))
      }
      Pipe.NW -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(FloodFillDirection.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.NW)))
      }
      Pipe.SW -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(FloodFillDirection.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(FloodFillDirection.SW)))
      }
      Pipe.SE -> {
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
          Pipe.NS -> {
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

          Pipe.EW -> {
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

          Pipe.NE -> {
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

          Pipe.NW -> {
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

          Pipe.SW -> {
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

          Pipe.SE -> {
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
                area[location.row - 1][location.col + 1] == Pipe.NONE
              ) {
                area[location.row - 1][location.col + 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == Pipe.NONE
              ) {
                area[location.row - 1][location.col - 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == Pipe.NONE
              ) {
                area[location.row + 1][location.col + 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == Pipe.NONE
              ) {
                area[location.row + 1][location.col - 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == Pipe.NONE
              ) {
                area[location.row - 1][location.col] = Pipe.INSIDE
              }
            }

            FloodFillDirection.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == Pipe.NONE
              ) {
                area[location.row + 1][location.col] = Pipe.INSIDE
              }
            }

            FloodFillDirection.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == Pipe.NONE
              ) {
                area[location.row][location.col + 1] = Pipe.INSIDE
              }
            }

            FloodFillDirection.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == Pipe.NONE
              ) {
                area[location.row][location.col - 1] = Pipe.INSIDE
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

  enum class Pipe(private val symbol: Char) {
    NS('|'),
    EW('-'),
    NE('L'),
    NW('J'),
    SW('7'),
    SE('F'),
    NONE('.'),
    UNKNOWN('S'),
    INSIDE('*'),
    OUTSIDE('#');

    override fun toString(): String = symbol.toString()

    companion object {
      fun fromSymbol(symbol: Char): Pipe = values().first { it.symbol == symbol }
    }
  }
}
