package me.peckb.aoc._2016.calendar.day11

import me.peckb.aoc._2016.calendar.day11.GenericIntDijkstra.DijkstraNode

data class FloorPlan(val floors: List<Floor>, val elevatorIndex: Int) : DijkstraNode<FloorPlan> {
  override fun neighbors(): Map<FloorPlan, Int> {
    val neighbors = mutableListOf<FloorPlan>()
    val elevatorFloor = floors[elevatorIndex]
    // RTGs can only go up
    if (elevatorIndex != floors.size - 1) {
      val rtgSet = elevatorFloor.generators.intersect(elevatorFloor.microchips)
      rtgSet.forEach { rtg ->
        val newFloors = floors.mapIndexed { i, f ->
          when(i) {
            elevatorIndex -> Floor(f.generators.minus(rtg), f.microchips.minus(rtg))
            elevatorIndex + 1 -> Floor(f.generators.plus(rtg), f.microchips.plus(rtg))
            else -> f
          }
        }
        neighbors.add(FloorPlan(newFloors, elevatorIndex + 1))
      }

      // Microchips can move up in pairs iff
      //  - they are not on a floor with their generator
      //  - they are not above their generator
      val lonelyMicrochips = elevatorFloor.microchips.filter { microchip ->
        !elevatorFloor.generators.contains(microchip)
          && floors.slice(0 until elevatorIndex).none { lowerFloor -> lowerFloor.generators.contains(microchip) }
      }
      if (lonelyMicrochips.size >= 2) {
        lonelyMicrochips.windowed(2).forEach { microchips ->
          val newFloors = floors.mapIndexed { i, f ->
            when(i) {
              elevatorIndex -> Floor(f.generators, f.microchips.minus(microchips))
              elevatorIndex + 1 -> Floor(f.generators, f.microchips.plus(microchips))
              else -> f
            }
          }
          neighbors.add(FloorPlan(newFloors, elevatorIndex + 1))
        }
      }
    }

    if (elevatorIndex != 0) {
      // Microchips can move down solo iff
      //  - a stranded microchip is below them
      //  - their matching generator is below them
      val hungryMicrochips = elevatorFloor.microchips.filter { microchip ->
        floors.slice(0 until elevatorIndex).withIndex().any { indexedFloor ->
          indexedFloor.value.microchips.any { possiblyStrandedMicrochip -> !indexedFloor.value.generators.contains(possiblyStrandedMicrochip) }
            || indexedFloor.value.generators.contains(microchip)
        }
      }
      hungryMicrochips.forEach { microchip ->
        val newFloors = floors.mapIndexed { i, f ->
          when(i) {
            elevatorIndex -> Floor(f.generators, f.microchips.minus(microchip))
            elevatorIndex - 1 -> Floor(f.generators, f.microchips.plus(microchip))
            else -> f
          }
        }
        neighbors.add(FloorPlan(newFloors, elevatorIndex - 1))
      }
    }

    return neighbors.associateWith { 1 }
  }
}