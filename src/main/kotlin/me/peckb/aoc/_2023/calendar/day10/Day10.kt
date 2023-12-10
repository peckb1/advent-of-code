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
      Pipe.NS -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, emptyList()))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, emptyList()))
      }
      Pipe.EW -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, emptyList()))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, emptyList()))
      }
      Pipe.NE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(Direction.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(Direction.NE)))
      }
      Pipe.NW -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(Direction.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(Direction.NW)))
      }
      Pipe.SW -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(Direction.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(Direction.SW)))
      }
      Pipe.SE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(Direction.SE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(Direction.SE)))
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
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          Pipe.EW -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, emptyList()))
            } else if (directionCameFrom == E) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, emptyList()))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          Pipe.NE -> {
            if (directionCameFrom == E) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, emptyList()))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          Pipe.NW -> {
            if (directionCameFrom == W) {
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == N) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, emptyList()))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          Pipe.SW -> {
            if (directionCameFrom == S) {
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, emptyList()))
            } else if (directionCameFrom == W) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, emptyList()))
            } else {
              throw IllegalStateException("Where did I come from")
            }
          }

          Pipe.SE -> {
            if (directionCameFrom == E) {
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, emptyList()))
            } else if (directionCameFrom == S) {
              stepsToTake.add(StepData(W, Location(location.row, location.col + 1), stepCount + 1, emptyList()))
            } else {
              throw IllegalStateException("Where did I come from")
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
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(Direction.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(Direction.W)))
      }
      Pipe.EW -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(Direction.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(Direction.N)))
      }
      Pipe.NE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(Direction.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(Direction.NE)))
      }
      Pipe.NW -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(Direction.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(Direction.NW)))
      }
      Pipe.SW -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(Direction.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(Direction.SW)))
      }
      Pipe.SE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(Direction.SE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(Direction.SE)))
      }
      else -> throw IllegalStateException("Unknown Start Pipe")
    }

//    area[startLocationRow][startLocationCol] = Pipe.UNKNOWN

    locationDistances[Location(startLocationRow, startLocationCol)] = 0

    while (stepsToTake.isNotEmpty()) {
      val (directionCameFrom, location, stepCount, insideDirections) = stepsToTake.remove()

      if (!locationDistances.containsKey(location)) {

//        area.forEachIndexed { r, row ->
//          row.forEachIndexed { c, pipe ->
//            if (r == location.row && c == location.col) {
//              print("@")
//            } else {
//              print(pipe)
//            }
//          }
//          println()
//        }
//        println()

        locationDistances[location] = stepCount

        val myInsides: List<Direction>
        when (area[location.row][location.col]) {
          Pipe.NS -> {
            myInsides = if (insideDirections.any { listOf(Direction.E, Direction.NE, Direction.SE).contains(it) }) {
              listOf(Direction.E)
            } else if (insideDirections.any { listOf(Direction.W, Direction.NW, Direction.SW).contains(it) }) {
              listOf(Direction.W)
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
            myInsides = if (insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
              listOf(Direction.N)
            } else if (insideDirections.any { listOf(Direction.S, Direction.SW, Direction.SE).contains(it) }) {
              listOf(Direction.S)
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
              myInsides = if (insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
                listOf(Direction.NE)
              } else if (insideDirections.any { listOf(Direction.S, Direction.SE, Direction.SW).contains(it) }) {
                listOf(Direction.S, Direction.SW, Direction.W)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == N) {
              myInsides = if(insideDirections.any { listOf(Direction.W, Direction.SW, Direction.NW).contains(it) }) {
                listOf(Direction.W, Direction.SW, Direction.S)
              } else if (insideDirections.any { listOf(Direction.E, Direction.NE, Direction.SE).contains(it) }) {
                listOf(Direction.NE)
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
              myInsides = if(insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
                listOf(Direction.NW)
              } else if (insideDirections.any { listOf(Direction.S, Direction.SW, Direction.SE).contains(it) }) {
                listOf(Direction.S, Direction.SE, Direction.E)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == N) {
              myInsides = if(insideDirections.any { listOf(Direction.W, Direction.NW, Direction.SW).contains(it) }) {
                listOf(Direction.NW)
              } else if (insideDirections.any { listOf(Direction.E, Direction.NE, Direction.SE).contains(it) }) {
                listOf(Direction.S, Direction.SE, Direction.E)
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
              myInsides = if(insideDirections.any { listOf(Direction.E, Direction.NE, Direction.SE).contains(it) }) {
                listOf(Direction.E, Direction.NE, Direction.N)
              } else if (insideDirections.any { listOf(Direction.W, Direction.NW, Direction.SW).contains(it) }) {
                listOf(Direction.SW)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, myInsides))
            } else if (directionCameFrom == W) {
              myInsides = if(insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
                listOf(Direction.N, Direction.NE, Direction.E)
              } else if (insideDirections.any { listOf(Direction.S, Direction.SW, Direction.SE).contains(it) }) {
                listOf(Direction.SW)
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
              myInsides = if(insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
                listOf(Direction.N, Direction.NW, Direction.W)
              } else if (insideDirections.any { listOf(Direction.S, Direction.SE, Direction.SW).contains(it) }) {
                listOf(Direction.SE)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == S) {
              myInsides = if(insideDirections.any { listOf(Direction.W, Direction.NW, Direction.SW).contains(it) }) {
                listOf(Direction.W, Direction.NW, Direction.N)
              } else if (insideDirections.any { listOf(Direction.E, Direction.SE, Direction.NE).contains(it) }) {
                listOf(Direction.SE)
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
            Direction.NE -> {
              if (
                location.row > 0 &&
                location.col < area[location.row].size - 1 &&
                area[location.row - 1][location.col + 1] == Pipe.NONE
              ) {
                area[location.row - 1][location.col + 1] = Pipe.INSIDE
              }
            }

            Direction.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == Pipe.NONE
              ) {
                area[location.row - 1][location.col - 1] = Pipe.INSIDE
              }
            }

            Direction.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == Pipe.NONE
              ) {
                area[location.row + 1][location.col + 1] = Pipe.INSIDE
              }
            }

            Direction.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == Pipe.NONE
              ) {
                area[location.row + 1][location.col - 1] = Pipe.INSIDE
              }
            }

            Direction.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == Pipe.NONE
              ) {
                area[location.row - 1][location.col] = Pipe.INSIDE
              }
            }

            Direction.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == Pipe.NONE
              ) {
                area[location.row + 1][location.col] = Pipe.INSIDE
              }
            }

            Direction.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == Pipe.NONE
              ) {
                area[location.row][location.col + 1] = Pipe.INSIDE
              }
            }

            Direction.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == Pipe.NONE
              ) {
                area[location.row][location.col - 1] = Pipe.INSIDE
              }
            }

            Direction.UNKNOWN -> { /* save for later */ }
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

//    area.forEach {
//      println(it.joinToString(""))
//    }

    println()
    things(area, startLocationCol, startLocationRow)
    println()
    things2(area)

    area.forEach {
      println(it.joinToString(""))
    }

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
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(Direction.W)))
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(Direction.W)))
      }
      Pipe.EW -> {
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(Direction.N)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(Direction.N)))
      }
      Pipe.NE -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(Direction.NE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(Direction.NE)))
      }
      Pipe.NW -> {
        stepsToTake.add(StepData(S, Location(startLocationRow - 1, startLocationCol), 1, listOf(Direction.NW)))
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(Direction.NW)))
      }
      Pipe.SW -> {
        stepsToTake.add(StepData(E, Location(startLocationRow, startLocationCol - 1), 1, listOf(Direction.SW)))
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(Direction.SW)))
      }
      Pipe.SE -> {
        stepsToTake.add(StepData(N, Location(startLocationRow + 1, startLocationCol), 1, listOf(Direction.SE)))
        stepsToTake.add(StepData(W, Location(startLocationRow, startLocationCol + 1), 1, listOf(Direction.SE)))
      }
      else -> throw IllegalStateException("Unknown Start Pipe")
    }

//    area[startLocationRow][startLocationCol] = Pipe.UNKNOWN

    locationDistances[Location(startLocationRow, startLocationCol)] = 0

    while (stepsToTake.isNotEmpty()) {
      val (directionCameFrom, location, stepCount, insideDirections) = stepsToTake.remove()

      if (!locationDistances.containsKey(location)) {

//        area.forEachIndexed { r, row ->
//          row.forEachIndexed { c, pipe ->
//            if (r == location.row && c == location.col) {
//              print("@")
//            } else {
//              print(pipe)
//            }
//          }
//          println()
//        }
//        println()

        locationDistances[location] = stepCount

        val myInsides: List<Direction>
        when (area[location.row][location.col]) {
          Pipe.NS -> {
            myInsides = if (insideDirections.any { listOf(Direction.E, Direction.NE, Direction.SE).contains(it) }) {
              listOf(Direction.E)
            } else if (insideDirections.any { listOf(Direction.W, Direction.NW, Direction.SW).contains(it) }) {
              listOf(Direction.W)
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
            myInsides = if (insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
              listOf(Direction.N)
            } else if (insideDirections.any { listOf(Direction.S, Direction.SW, Direction.SE).contains(it) }) {
              listOf(Direction.S)
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
              myInsides = if (insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
                listOf(Direction.NE)
              } else if (insideDirections.any { listOf(Direction.S, Direction.SE, Direction.SW).contains(it) }) {
                listOf(Direction.S, Direction.SW, Direction.W)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == N) {
              myInsides = if(insideDirections.any { listOf(Direction.W, Direction.SW, Direction.NW).contains(it) }) {
                listOf(Direction.W, Direction.SW, Direction.S)
              } else if (insideDirections.any { listOf(Direction.E, Direction.NE, Direction.SE).contains(it) }) {
                listOf(Direction.NE)
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
              myInsides = if(insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
                listOf(Direction.NW)
              } else if (insideDirections.any { listOf(Direction.S, Direction.SW, Direction.SE).contains(it) }) {
                listOf(Direction.S, Direction.SE, Direction.E)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(S, Location(location.row - 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == N) {
              myInsides = if(insideDirections.any { listOf(Direction.W, Direction.NW, Direction.SW).contains(it) }) {
                listOf(Direction.NW)
              } else if (insideDirections.any { listOf(Direction.E, Direction.NE, Direction.SE).contains(it) }) {
                listOf(Direction.S, Direction.SE, Direction.E)
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
              myInsides = if(insideDirections.any { listOf(Direction.E, Direction.NE, Direction.SE).contains(it) }) {
                listOf(Direction.E, Direction.NE, Direction.N)
              } else if (insideDirections.any { listOf(Direction.W, Direction.NW, Direction.SW).contains(it) }) {
                listOf(Direction.SW)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(E, Location(location.row, location.col - 1), stepCount + 1, myInsides))
            } else if (directionCameFrom == W) {
              myInsides = if(insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
                listOf(Direction.N, Direction.NE, Direction.E)
              } else if (insideDirections.any { listOf(Direction.S, Direction.SW, Direction.SE).contains(it) }) {
                listOf(Direction.SW)
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
              myInsides = if(insideDirections.any { listOf(Direction.N, Direction.NE, Direction.NW).contains(it) }) {
                listOf(Direction.N, Direction.NW, Direction.W)
              } else if (insideDirections.any { listOf(Direction.S, Direction.SE, Direction.SW).contains(it) }) {
                listOf(Direction.SE)
              } else {
                emptyList()
              }
              stepsToTake.add(StepData(N, Location(location.row + 1, location.col), stepCount + 1, myInsides))
            } else if (directionCameFrom == S) {
              myInsides = if(insideDirections.any { listOf(Direction.W, Direction.NW, Direction.SW).contains(it) }) {
                listOf(Direction.W, Direction.NW, Direction.N)
              } else if (insideDirections.any { listOf(Direction.E, Direction.SE, Direction.NE).contains(it) }) {
                listOf(Direction.SE)
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
            Direction.NE -> {
              if (
                location.row > 0 &&
                location.col < area[location.row].size - 1 &&
                area[location.row - 1][location.col + 1] == Pipe.NONE
              ) {
                area[location.row - 1][location.col + 1] = Pipe.INSIDE
              }
            }

            Direction.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == Pipe.NONE
              ) {
                area[location.row - 1][location.col - 1] = Pipe.INSIDE
              }
            }

            Direction.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == Pipe.NONE
              ) {
                area[location.row + 1][location.col + 1] = Pipe.INSIDE
              }
            }

            Direction.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == Pipe.NONE
              ) {
                area[location.row + 1][location.col - 1] = Pipe.INSIDE
              }
            }

            Direction.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == Pipe.NONE
              ) {
                area[location.row - 1][location.col] = Pipe.INSIDE
              }
            }

            Direction.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == Pipe.NONE
              ) {
                area[location.row + 1][location.col] = Pipe.INSIDE
              }
            }

            Direction.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == Pipe.NONE
              ) {
                area[location.row][location.col + 1] = Pipe.INSIDE
              }
            }

            Direction.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == Pipe.NONE
              ) {
                area[location.row][location.col - 1] = Pipe.INSIDE
              }
            }

            Direction.UNKNOWN -> { /* save for later */ }
          }
        }
      }
    }
  }

  data class StepData(
    val directionCameFrom: DirectionCameFrom,
    val location: Location,
    val stepCount: Int,
    val insideDirections: List<Direction>
  )

  data class Location(val row: Int, val col: Int)

  enum class DirectionCameFrom { N, S, E, W }

  enum class Direction { NE, NW, SE, SW, N, S, E, W, UNKNOWN }

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
