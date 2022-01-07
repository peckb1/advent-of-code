package me.peckb.aoc._2016.calendar.day11

import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode

data class FloorPlan(val floors: List<Floor>, val elevatorIndex: Int) : DijkstraNode<FloorPlan> {
  override fun neighbors(): Map<FloorPlan, Int> {
    val neighbors = mutableListOf<FloorPlan>()
    val elevatorFloor = floors[elevatorIndex]

    if (elevatorIndex != floors.size - 1) {
      // items that can only go upwards
      neighbors.addAll(findMovableRTGs(elevatorFloor))
      neighbors.addAll(findLonelyMicrochips(elevatorFloor))
    }
    if (elevatorIndex != 0) {
      // items that are allowed to travel back down
      neighbors.addAll(findRescueMicrochips(elevatorFloor))
    }

    return neighbors.associateWith { 1 }
  }

  /**
   * Pairs of generators and microchips that want to get to the top floor
   */
  private fun findMovableRTGs(elevatorFloor: Floor): Collection<FloorPlan> {
    val rtgSet = elevatorFloor.generators.intersect(elevatorFloor.microchips)

    return rtgSet.map { rtg ->
      val newFloors = floors.mapIndexed { i, f ->
        when(i) {
          elevatorIndex -> Floor(f.generators.minus(rtg), f.microchips.minus(rtg))
          elevatorIndex + 1 -> Floor(f.generators.plus(rtg), f.microchips.plus(rtg))
          else -> f
        }
      }
      FloorPlan(newFloors, elevatorIndex + 1)
    }
  }

  /**
   * Microchips can move up in pairs iff
   *  - they are not on a floor with their generator
   *  - they are not above their generator
   */
  private fun findLonelyMicrochips(elevatorFloor: Floor): Collection<FloorPlan> {
    val lonelyMicrochips = elevatorFloor.microchips.filter { microchip ->
      !elevatorFloor.generators.contains(microchip)
        && floors.slice(0 until elevatorIndex).none { lowerFloor -> lowerFloor.generators.contains(microchip) }
    }

    return if (lonelyMicrochips.size >= 2) {
      lonelyMicrochips.windowed(2).map { microchips ->
        val newFloors = floors.mapIndexed { i, f ->
          when(i) {
            elevatorIndex -> Floor(f.generators, f.microchips.minus(microchips))
            elevatorIndex + 1 -> Floor(f.generators, f.microchips.plus(microchips))
            else -> f
          }
        }
        FloorPlan(newFloors, elevatorIndex + 1)
      }
    } else {
      emptyList()
    }
  }

  /**
   * Microchips can move down solo iff
   *  - a stranded microchip is below them
   *  - their matching generator is below them
   */
  private fun findRescueMicrochips(elevatorFloor: Floor): Collection<FloorPlan> {
    val rescueMicrochips = elevatorFloor.microchips.filter { microchip ->
      floors.slice(0 until elevatorIndex).withIndex().any { indexedFloor ->
        indexedFloor.value.microchips.any { possiblyStrandedMicrochip -> !indexedFloor.value.generators.contains(possiblyStrandedMicrochip) }
          || indexedFloor.value.generators.contains(microchip)
      }
    }
    return rescueMicrochips.map { microchip ->
      val newFloors = floors.mapIndexed { i, f ->
        when(i) {
          elevatorIndex -> Floor(f.generators, f.microchips.minus(microchip))
          elevatorIndex - 1 -> Floor(f.generators, f.microchips.plus(microchip))
          else -> f
        }
      }
      FloorPlan(newFloors, elevatorIndex - 1)
    }
  }
}
