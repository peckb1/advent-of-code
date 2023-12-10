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
    val locationDistances: Map<Location, Int> = findOurPipes(area, startLocationRow, startLocationCol)
    clearExtraneousPipe(area, locationDistances)

    val outsideCorner: Location = findOutsideCorner(area)
    markInsideAreas(area, outsideCorner.row, outsideCorner.col)
    floodFillInsideItems(area)

    area.sumOf { row ->
      row.count { it == INSIDE }
    }
  }



  private fun generateArea(input: Sequence<String>): Triple<MutableList<MutableList<LandType>>, Int, Int> {
    val area = mutableListOf<MutableList<LandType>>()

    var startLocationRow = -1
    var startLocationCol = -1
    input.forEachIndexed { rowIndex, row ->
      val landTypeRow = mutableListOf<LandType>()
      row.forEachIndexed { colIndex, c ->
        LandType.fromSymbol(c).also { landType ->
          landTypeRow.add(landType)
          if (landType == START) {
            startLocationRow = rowIndex
            startLocationCol = colIndex
          }
        }
      }
      area.add(landTypeRow)
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

  private fun clearExtraneousPipe(area: MutableList<MutableList<LandType>>, locationMap: Map<Location, Int>) {
    area.forEachIndexed { rowIndex , row ->
      row.indices.forEach { colIndex ->
        if (!locationMap.containsKey(Location(rowIndex, colIndex))) {
          area[rowIndex][colIndex] = GROUND
        }
      }
    }
  }

  private fun findOutsideCorner(area: MutableList<MutableList<LandType>>): Location {
    area.forEachIndexed { rowIndex, row ->
      row.forEachIndexed { colIndex, landType ->
        if (landType != GROUND) {
          return Location(rowIndex, colIndex)
        }
      }
    }
    throw IllegalStateException("No pipe found")
  }

  private fun floodFillInsideItems(area: MutableList<MutableList<LandType>>) {
    area.forEachIndexed { r, row ->
      row.forEachIndexed { c, landType ->
        if (landType == GROUND) {
          if (r > 0 && area[r-1][c] == INSIDE) {
            area[r][c] = INSIDE
          } else if (r < area.size - 1 && area[r+1][c] == INSIDE) {
            area[r][c] = INSIDE
          } else if (c > 0 && area[r][c-1] == INSIDE) {
            area[r][c] = INSIDE
          } else if (c < area[r].size - 1 && area[r][c+1] == INSIDE) {
            area[r][c] = INSIDE
          }
        }
      }
    }
  }

  private fun markInsideAreas(area: MutableList<MutableList<LandType>>, startLocationRow: Int, startLocationCol: Int) {
    val stepsToTake: Queue<StepData> = LinkedList()
    val locations = mutableSetOf<Location>()

    val startLocation = Location(startLocationRow, startLocationCol)

    when (area[startLocationRow][startLocationCol]) {
      NE_PIPE -> {
        stepsToTake.add(StepData(S, startLocation.goNorth(), insideDirections = listOf(FloodFill.NE)))
        stepsToTake.add(StepData(W, startLocation.goEast(), insideDirections = listOf(FloodFill.NE)))
      }
      NW_PIPE -> {
        stepsToTake.add(StepData(S, startLocation.goNorth(), insideDirections = listOf(FloodFill.NW)))
        stepsToTake.add(StepData(E, startLocation.goWest(), insideDirections = listOf(FloodFill.NW)))
      }
      SW_PIPE -> {
        stepsToTake.add(StepData(E, startLocation.goWest(), insideDirections = listOf(FloodFill.SW)))
        stepsToTake.add(StepData(N, startLocation.goSouth(), insideDirections = listOf(FloodFill.SW)))
      }
      SE_PIPE -> {
        stepsToTake.add(StepData(N, startLocation.goSouth(), insideDirections = listOf(FloodFill.SE)))
        stepsToTake.add(StepData(W, startLocation.goEast(), insideDirections = listOf(FloodFill.SE)))
      }
      else -> throw IllegalStateException("Unknown Corner Pipe")
    }

    locations.add(Location(startLocationRow, startLocationCol))

    val floodDirectionsWithNorth = setOf(FloodFill.N, FloodFill.NW, FloodFill.NE)
    val floodDirectionsWithSouth = setOf(FloodFill.S, FloodFill.SW, FloodFill.SE)
    val floodDirectionsWithEast = setOf(FloodFill.E, FloodFill.NE, FloodFill.SE)
    val floodDirectionsWithWest = setOf(FloodFill.W, FloodFill.NW, FloodFill.SW)

    while (stepsToTake.isNotEmpty()) {
      val (directionCameFrom, location, _, insideDirections) = stepsToTake.remove()

      val eastSide by lazy { insideDirections.any { floodDirectionsWithEast.contains(it) } }
      val westSide by lazy { insideDirections.any { floodDirectionsWithWest.contains(it) } }
      val northSide by lazy { insideDirections.any { floodDirectionsWithNorth.contains(it) } }
      val southSide by lazy { insideDirections.any { floodDirectionsWithSouth.contains(it) } }

      if (!locations.contains(location)) {
        val myInsides: List<FloodFill>
        when (area[location.row][location.col]) {
          NS_PIPE -> {
            myInsides = if (eastSide) {
              listOf(FloodFill.E)
            } else if (westSide) {
              listOf(FloodFill.W)
            } else {
              emptyList()
            }
            when (directionCameFrom) {
              N -> stepsToTake.add(StepData(N, Location(location.row + 1, location.col), insideDirections = myInsides))
              S -> stepsToTake.add(StepData(S, Location(location.row - 1, location.col), insideDirections = myInsides))
              else -> throw IllegalStateException("Where did I come from")
            }
          }

          EW_PIPE -> {
            myInsides = if (northSide) {
              listOf(FloodFill.N)
            } else if (southSide) {
              listOf(FloodFill.S)
            } else {
              emptyList()
            }
            when (directionCameFrom) {
              W -> stepsToTake.add(StepData(W, Location(location.row, location.col + 1), insideDirections = myInsides))
              E -> stepsToTake.add(StepData(E, Location(location.row, location.col - 1), insideDirections = myInsides))
              else -> throw IllegalStateException("Where did I come from")
            }
          }

          NE_PIPE -> {
            when (directionCameFrom) {
              E -> {
                myInsides = if (northSide) {
                  listOf(FloodFill.NE)
                } else if (southSide) {
                  listOf(FloodFill.S, FloodFill.SW, FloodFill.W)
                } else {
                  emptyList()
                }
                stepsToTake.add(StepData(S, Location(location.row - 1, location.col), insideDirections = myInsides))
              }
              N -> {
                myInsides = if(westSide) {
                  listOf(FloodFill.W, FloodFill.SW, FloodFill.S)
                } else if (eastSide) {
                  listOf(FloodFill.NE)
                } else {
                  emptyList()
                }
                stepsToTake.add(StepData(W, Location(location.row, location.col + 1), insideDirections = myInsides))
              }
              else -> throw IllegalStateException("Where did I come from")
            }
          }

          NW_PIPE -> {
            when (directionCameFrom) {
              W -> {
                myInsides = if(northSide) {
                  listOf(FloodFill.NW)
                } else if (southSide) {
                  listOf(FloodFill.S, FloodFill.SE, FloodFill.E)
                } else {
                  emptyList()
                }
                stepsToTake.add(StepData(S, Location(location.row - 1, location.col), insideDirections = myInsides))
              }
              N -> {
                myInsides = if(westSide) {
                  listOf(FloodFill.NW)
                } else if (eastSide) {
                  listOf(FloodFill.S, FloodFill.SE, FloodFill.E)
                } else {
                  emptyList()
                }
                stepsToTake.add(StepData(E, Location(location.row, location.col - 1), insideDirections = myInsides))
              }
              else -> throw IllegalStateException("Where did I come from")
            }
          }

          SW_PIPE -> {
            when (directionCameFrom) {
              S -> {
                myInsides = if(eastSide) {
                  listOf(FloodFill.E, FloodFill.NE, FloodFill.N)
                } else if (westSide) {
                  listOf(FloodFill.SW)
                } else {
                  emptyList()
                }
                stepsToTake.add(StepData(E, Location(location.row, location.col - 1), insideDirections = myInsides))
              }
              W -> {
                myInsides = if(northSide) {
                  listOf(FloodFill.N, FloodFill.NE, FloodFill.E)
                } else if (southSide) {
                  listOf(FloodFill.SW)
                } else {
                  emptyList()
                }
                stepsToTake.add(StepData(N, Location(location.row + 1, location.col), insideDirections = myInsides))
              }
              else -> throw IllegalStateException("Where did I come from")
            }
          }

          SE_PIPE -> {
            when (directionCameFrom) {
              E -> {
                myInsides = if(northSide) {
                  listOf(FloodFill.N, FloodFill.NW, FloodFill.W)
                } else if (southSide) {
                  listOf(FloodFill.SE)
                } else {
                  emptyList()
                }
                stepsToTake.add(StepData(N, Location(location.row + 1, location.col), insideDirections = myInsides))
              }
              S -> {
                myInsides = if(westSide) {
                  listOf(FloodFill.W, FloodFill.NW, FloodFill.N)
                } else if (eastSide) {
                  listOf(FloodFill.SE)
                } else {
                  emptyList()
                }
                stepsToTake.add(StepData(W, Location(location.row, location.col + 1), insideDirections = myInsides))
              }
              else -> throw IllegalStateException("Where did I come from")
            }
          }

          else -> throw IllegalStateException("Unknown Start Pipe")
        }

        myInsides.forEach { myInside ->
          when (myInside) {
            FloodFill.NE -> {
              if (
                location.row > 0 &&
                location.col < area[location.row].size - 1 &&
                area[location.row - 1][location.col + 1] == GROUND
              ) {
                area[location.row - 1][location.col + 1] = INSIDE
              }
            }

            FloodFill.NW -> {
              if (
                location.row > 0 &&
                location.col > 0 &&
                area[location.row - 1][location.col - 1] == GROUND
              ) {
                area[location.row - 1][location.col - 1] = INSIDE
              }
            }

            FloodFill.SE -> {
              if (
                location.row < area.size - 1 &&
                location.col < area[location.row].size - 1 &&
                area[location.row + 1][location.col + 1] == GROUND
              ) {
                area[location.row + 1][location.col + 1] = INSIDE
              }
            }

            FloodFill.SW -> {
              if (
                location.row < area.size - 1 &&
                location.col > 0 &&
                area[location.row + 1][location.col - 1] == GROUND
              ) {
                area[location.row + 1][location.col - 1] = INSIDE
              }
            }

            FloodFill.N -> {
              if (
                location.row > 0 &&
                area[location.row - 1][location.col] == GROUND
              ) {
                area[location.row - 1][location.col] = INSIDE
              }
            }

            FloodFill.S -> {
              if (
                location.row < area.size - 1 &&
                area[location.row + 1][location.col] == GROUND
              ) {
                area[location.row + 1][location.col] = INSIDE
              }
            }

            FloodFill.E -> {
              if (
                location.col < area[location.row].size - 1 &&
                area[location.row][location.col + 1] == GROUND
              ) {
                area[location.row][location.col + 1] = INSIDE
              }
            }

            FloodFill.W -> {
              if (
                location.col > 0 &&
                area[location.row][location.col - 1] == GROUND
              ) {
                area[location.row][location.col - 1] = INSIDE
              }
            }

            FloodFill.UNKNOWN -> { /* ignore! */ }
          }
        }
      }
    }
  }

  data class StepData(
    val directionCameFrom: DirectionCameFrom,
    val location: Location,
    val stepCount: Int = 1,
    val insideDirections: List<FloodFill> = emptyList()
  )

  data class Location(val row: Int, val col: Int) {
    fun goNorth() = Location(row - 1, col)
    fun goSouth() = Location(row + 1, col)
    fun goEast() = Location(row, col + 1)
    fun goWest() = Location(row, col - 1)
  }

  enum class DirectionCameFrom { N, S, E, W }

  enum class FloodFill { NE, NW, SE, SW, N, S, E, W, UNKNOWN }

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
