package me.peckb.aoc._2016.calendar.day11

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day11 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }

  // private fun loadLayout(input: Sequence<String>): FloorPlan {
  //   val floors = input.mapIndexed { index, line ->
  //     val generators = sortedSetOf<String>()
  //     val microchips = sortedSetOf<String>()
  //
  //     // val equipmentOnFloor = line.substringAfter("floor contains ").split(", ")
  //     val equipmentOnFloor = line.substringAfter("floor contains ")
  //       .split("a ")
  //       .filterNot { it.isEmpty() }
  //       .filterNot { it.contains("nothing") }
  //
  //     val (generatorData, microchipData) = equipmentOnFloor.partition { it.contains("generator") }
  //     generatorData.forEach { generators.add(it.substring(0, 3)) }
  //     microchipData.forEach { microchips.add(it.substring(0, 3)) }
  //
  //     Floor(index == 0, generators, microchips)
  //   }
  //   return FloorPlan(floors.toList())
  // }
}
//
// class RTGDijkstra: Dijkstra<FloorPlan, Int, FloorPlanWithCost> {
//   override fun FloorPlan.withCost(cost: Int) = FloorPlanWithCost(this, cost)
//
//   override infix fun Int.plus(cost: Int) = this + cost
//
//   override fun minCost() = 0
//
//   override fun maxCost() = Int.MAX_VALUE
// }
//
// enum class Direction { UP, DOWN }
//
// data class Floor(val elevator: Boolean, val generators: TreeSet<String>, val microchips: TreeSet<String>)
//
// data class FloorPlan(val floors: List<Floor>) {
//   fun neighbors(): List<Pair<FloorPlan, Int>> {
//     val moves = mutableListOf<Pair<FloorPlan, Int>>()
//
//     val floor = floors.withIndex().first { it.value.elevator }
//     // find single item movement
//     val microchipGoingUp = floor.value.microchips.filter { canMoveMicrochip(floor.index, it, UP) }
//     val microchipGoingDown = floor.value.microchips.filter { canMoveMicrochip(floor.index, it, DOWN) }
//     val generatorGoingUp = floor.value.generators.filter { canMoveGenerator(floor.index, it, UP) }
//     val generatorGoingDown = floor.value.generators.filter { canMoveGenerator(floor.index, it, DOWN) }
//
//     // find double item movement
//     val microchipsGoingUp = floor.value.microchips.windowed(2).filter {
//       it.size == 2 && canMoveMicrochips(floor.index, it.first(), it.last(), UP)
//     }.map { it.first() to it.last() }
//     val microchipsGoingDown = floor.value.microchips.windowed(2).filter {
//       it.size == 2 && canMoveMicrochips(floor.index, it.first(), it.last(), DOWN)
//     }.map { it.first() to it.last() }
//     val generatorsGoingUp = floor.value.generators.windowed(2).filter {
//       it.size == 2 && canMoveGenerators(floor.index, it.first(), it.last(), UP)
//     }.map { it.first() to it.last() }
//     val generatorsGoingDown = floor.value.generators.windowed(2).filter {
//       it.size == 2 && canMoveGenerators(floor.index, it.first(), it.last(), DOWN)
//     }.map { it.first() to it.last() }
//     val RTGsOnFloor = floor.value.microchips.mapNotNull { m ->
//       floor.value.generators.find { it == m }?.let { m to it }
//     }
//     val RTGsGoingUp = RTGsOnFloor.filter { canMoveRTG(floor.index, UP) }
//     val RTGsGoingDown = RTGsOnFloor.filter { canMoveRTG(floor.index, DOWN) }
//
//     microchipGoingUp.forEach { microchip ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, f.generators, TreeSet<String>().apply { addAll(f.microchips.minus(microchip)) })
//           floor.index + 1 -> Floor(true, f.generators, TreeSet<String>().apply { addAll(f.microchips.plus(microchip)) })
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     microchipGoingDown.forEach { microchip ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, f.generators, TreeSet<String>().apply { addAll(f.microchips.minus(microchip)) })
//           floor.index - 1 -> Floor(true, f.generators, TreeSet<String>().apply { addAll(f.microchips.plus(microchip)) })
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     generatorGoingUp.forEach { generator ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, TreeSet<String>().apply { addAll(f.generators.minus(generator)) }, f.generators)
//           floor.index + 1 -> Floor(true, TreeSet<String>().apply { addAll(f.generators.plus(generator)) }, f.microchips)
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     generatorGoingDown.forEach { generator ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, TreeSet<String>().apply { addAll(f.generators.minus(generator)) }, f.microchips)
//           floor.index - 1 -> Floor(true, TreeSet<String>().apply { addAll(f.generators.plus(generator)) }, f.microchips)
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     microchipsGoingUp.forEach { (m1, m2) ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, f.generators, TreeSet<String>().apply { addAll(f.microchips.minus(m1).minus(m2)) })
//           floor.index + 1 -> Floor(true, f.generators, TreeSet<String>().apply { addAll(f.microchips.plus(m1).plus(m2)) })
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     microchipsGoingDown.forEach { (m1, m2) ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, f.generators, TreeSet<String>().apply { addAll(f.microchips.minus(m1).minus(m2)) })
//           floor.index - 1 -> Floor(true, f.generators, TreeSet<String>().apply { addAll(f.microchips.plus(m1).plus(m2)) })
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     generatorsGoingUp.forEach { (g1, g2) ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, TreeSet<String>().apply { addAll(f.generators.minus(g1).minus(g2)) }, f.microchips)
//           floor.index + 1 -> Floor(true, TreeSet<String>().apply { addAll(f.generators.plus(g1).plus(g2)) }, f.microchips)
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     generatorsGoingDown.forEach { (g1, g2) ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, TreeSet<String>().apply { addAll(f.generators.minus(g1).minus(g2)) }, f.microchips)
//           floor.index - 1 -> Floor(true, TreeSet<String>().apply { addAll(f.generators.plus(g1).plus(g2)) }, f.microchips)
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     RTGsGoingUp.forEach { (m, g) ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, TreeSet<String>().apply { addAll(f.generators.minus(g)) }, TreeSet<String>().apply { addAll(f.microchips.minus(m)) })
//           floor.index + 1 -> Floor(true, TreeSet<String>().apply { addAll(f.generators.plus(g)) }, TreeSet<String>().apply { addAll(f.microchips.plus(m)) })
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     RTGsGoingDown.forEach { (m, g) ->
//       val newFloors = floors.mapIndexed { i, f ->
//         when (i) {
//           floor.index -> Floor(false, TreeSet<String>().apply { addAll(f.generators.minus(g)) }, TreeSet<String>().apply { addAll(f.microchips.minus(m)) })
//           floor.index - 1 -> Floor(true, TreeSet<String>().apply { addAll(f.generators.plus(g)) }, TreeSet<String>().apply { addAll(f.microchips.plus(m)) })
//           else -> Floor(false, f.generators, f.microchips)
//         }
//       }
//       val newPlan = FloorPlan(newFloors)
//       moves.add(newPlan to 1)
//     }
//
//     return moves
//   }
//
//   private fun canMoveMicrochip(floorIndex: Int, microchip: String, direction: Direction): Boolean {
//     if (floorIndex == floors.size - 1 && direction == UP) return false
//     if (floorIndex == 0 && direction == DOWN) return false
//     if (direction == DOWN && floors.subList(0, floorIndex).all { it.microchips.isEmpty() && it.generators.isEmpty() }) return false
//
//     val step = if (direction == UP) 1 else -1
//     val me = floors[floorIndex]
//     val next = floors[floorIndex + step]
//
//     // if we take the microchip away, does it leave an unmatched generator that will fry an unmatched microchip?
//     if (me.generators.contains(microchip) && me.microchips.filterNot { it == microchip }.any { !me.generators.contains(it) }) {
//       return false
//     }
//
//     // if we take the microchip to the new floor, will it get fried by an unmatched generator?
//     if (!next.generators.contains(microchip) && next.generators.any { !next.microchips.contains(it) }) {
//       return false
//     }
//
//     return true
//   }
//
//   private fun canMoveMicrochips(floorIndex: Int, microchipOne: String, microchipTwo: String, direction: Direction): Boolean {
//     return canMoveMicrochip(floorIndex, microchipOne, direction) && canMoveMicrochip(floorIndex, microchipTwo, direction)
//   }
//
//   private fun canMoveGenerator(floorIndex: Int, generator: String, direction: Direction): Boolean {
//     if (floorIndex == floors.size - 1 && direction == UP) return false
//     if (floorIndex == 0 && direction == DOWN) return false
//     if (direction == DOWN && floors.subList(0, floorIndex).all { it.microchips.isEmpty() && it.generators.isEmpty() }) return false
//
//     val step = if (direction == UP) 1 else -1
//     val me = floors[floorIndex]
//     val next = floors[floorIndex + step]
//
//     // if we take the generator away, will a matching microchip get fried?
//     if (me.microchips.contains(generator) && me.generators.filterNot { it == generator }.any { !me.microchips.contains(it) }) {
//       return false
//     }
//
//     // if we move the generator will it fry any microchips?
//     if (next.microchips.any { !next.generators.contains(it) }) {
//       return false
//     }
//
//     return true
//   }
//
//   private fun canMoveGenerators(floorIndex: Int, generatorOne: String, generatorTwo: String, direction: Direction): Boolean {
//     return canMoveGenerator(floorIndex, generatorOne, direction) && canMoveGenerator(floorIndex, generatorTwo, direction)
//   }
//
//   private fun canMoveRTG(floorIndex: Int, direction: Direction): Boolean {
//     if (floorIndex == floors.size - 1 && direction == UP) return false
//     if (floorIndex == 0 && direction == DOWN) return false
//     if (direction == DOWN && floors.subList(0, floorIndex).all { it.microchips.isEmpty() && it.generators.isEmpty() }) return false
//
//     return true
//   }
// }
//
// data class FloorPlanWithCost(val floorPlan: FloorPlan, val cost: Int) : DijkstraNodeWithCost<FloorPlan, Int> {
//   override fun compareTo(other: DijkstraNodeWithCost<FloorPlan, Int>) = cost.compareTo(other.cost())
//   override fun node() = floorPlan
//   override fun cost() = cost
//
//   override fun neighbors(): List<FloorPlanWithCost> {
//     return floorPlan.neighbors().map {
//       FloorPlanWithCost(it.first, it.second)
//     }
//   }
// }

/*
The first floor contains a thulium generator, a thulium-compatible microchip, a plutonium generator, and a strontium generator.
The second floor contains a plutonium-compatible microchip and a strontium-compatible microchip.
The third floor contains a promethium generator, a promethium-compatible microchip, a ruthenium generator, and a ruthenium-compatible microchip.
The fourth floor contains nothing relevant.

F4 .  .   .   .   .   .   .   .   .   .   .
F3 .  .   .   .   .   .   .  PrG PrM RuG RuM
F2 .  .   .   .  PlM StM  .   .   .   .   .
F1 E ThG ThM PlG  .   .  StG  .   .   .   .

+3 (3)

F4 E ThG ThM  .   .   .   .   .   .   .   .
F3 .  .   .   .   .   .   .  PrG PrM RuG RuM
F2 .  .   .   .  PlM StM  .   .   .   .   .
F1 .  .   .  PlG  .   .  StG  .   .   .   .

+2 (5)

F4 . ThG  .   .   .   .   .   .   .   .   .
F3 .  .   .   .   .   .   .  PrG PrM RuG RuM
F2 E  .  ThM  .  PlM StM  .   .   .   .   .
F1 .  .   .  PlG  .   .  StG  .   .   .   .

+4 (9)

F4 E ThG  .  PlG PlM  .   .   .   .   .   .
F3 .  .   .   .   .   .   .  PrG PrM RuG RuM
F2 .  .  ThM  .   .  StM  .   .   .   .   .
F1 .  .   .   .   .   .  StG  .   .   .   .

+2 (11)

F4 . ThG  .  PlG  .   .   .   .   .   .   .
F3 .  .   .   .   .   .   .  PrG PrM RuG RuM
F2 E  .  ThM  .  PlM StM  .   .   .   .   .
F1 .  .   .   .   .   .  StG  .   .   .   .

+4 (15)

F4 E ThG  .  PlG  .  StM StG  .   .   .   .
F3 .  .   .   .   .   .   .  PrG PrM RuG RuM
F2 .  .  ThM  .  PlM  .   .   .   .   .   .
F1 .  .   .   .   .   .   .   .   .   .   .

+4 (19)

F4 E ThG  .  PlG PlM StM StG  .   .   .   .
F3 .  .   .   .   .   .   .  PrG PrM RuG RuM
F2 .  .  ThM  .   .   .   .   .   .   .   .
F1 .  .   .   .   .   .   .   .   .   .   .

+4 (23)

F4 E ThG ThM PlG PlM StM StG  .   .   .   .
F3 .  .   .   .   .   .   .  PrG PrM RuG RuM
F2 .  .   .   .   .   .   .   .   .   .   .
F1 .  .   .   .   .   .   .   .   .   .   .

+4 (27)

F4 E ThG ThM PlG PlM StM StG PrG PrM  .   .
F3 .  .   .   .   .   .   .   .   .  RuG RuM
F2 .  .   .   .   .   .   .   .   .   .   .
F1 .  .   .   .   .   .   .   .   .   .   .

+4 (31)

F4 E ThG ThM PlG PlM StM StG PrG PrM RuG RuM
F3 .  .   .   .   .   .   .   .   .   .   .
F2 .  .   .   .   .   .   .   .   .   .   .
F1 .  .   .   .   .   .   .   .   .   .   .

 */