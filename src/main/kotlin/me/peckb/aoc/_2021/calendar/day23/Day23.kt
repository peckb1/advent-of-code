package me.peckb.aoc._2021.calendar.day23

import me.peckb.aoc.generators.InputGenerator
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE

class Day23 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  companion object {
    private val SMALL_GOAL = listOf(
      "#############",
      "#...........#",
      "###A#B#C#D###",
      "  #A#B#C#D#  ",
      "  #########  "
    )

    private val LARGE_GOAL = listOf(
      "#############",
      "#...........#",
      "###A#B#C#D###",
      "  #A#B#C#D#  ",
      "  #A#B#C#D#  ",
      "  #A#B#C#D#  ",
      "  #########  "
    )
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val startLayout = Layout.fromInput(input.toList())
    val dijkstra = AmphipodDijkstra()
    val costs = dijkstra.solve(startLayout)
    costs[Layout.fromInput(SMALL_GOAL)]
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val startLayout = Layout.fromInput(input.toList())
    val dijkstra = AmphipodDijkstra()
    val costs = dijkstra.solve(startLayout)
    costs[Layout.fromInput(LARGE_GOAL)]
  }
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
class AmphipodDijkstra : Dijkstra<Layout, Int, LayoutWithCost> {
  override fun minCost() = 0
  override fun maxCost() = MAX_VALUE
  override fun Int.plus(cost: Int) = this + cost
  override fun Layout.withCost(cost: Int) = LayoutWithCost(this, cost)
}

class Layout(val hallway: CharArray, val rooms: List<CharArray>) {
  fun neighbors(): List<Pair<Layout, Int>> {
    return emptyList()
  }

  companion object {
    fun fromInput(input: List<String>) : Layout {
      val hallway = input[1].replace("#", "").toCharArray()
      val rooms = input.subList(2, input.size - 1).map {
        it.replace("#", " ").drop(1).dropLast(1).toCharArray()
      }

      return Layout(hallway, rooms)
    }
  }
}

class LayoutWithCost(private val layout: Layout, private val cost: Int) : DijkstraNodeWithCost<Layout, Int> {
  override fun compareTo(other: DijkstraNodeWithCost<Layout, Int>) = cost.compareTo(other.cost())
  override fun node() = layout
  override fun cost() = cost

  override fun neighbors() = layout.neighbors().map { LayoutWithCost(it.first, it.second) }
}

// private val input = listOf(
  //   "#############",
  //   "#...........#",
  //   "###A#C#B#C###",
  //   "  #D#A#D#B#  ",
  //   "  #########  "
  // )
  //
  // private val goalPartOne = listOf(
  //   "#############",
  //   "#...........#",
  //   "###A#B#C#D###",
  //   "  #A#B#C#D#  ",
  //   "  #########  "
  // )
  //
  // private val goalPartTwo = listOf(
  //   "#############",
  //   "#...........#",
  //   "###A#B#C#D###",
  //   "  #A#B#C#D#  ",
  //   "  #A#B#C#D#  ",
  //   "  #A#B#C#D#  ",
  //   "  #########  "
  // )
  //
  // private val initialState = State.from(input)
  //
  // private val initialStateExtended = State.from(input.take(3) + "  #D#C#B#A#  " + "  #D#B#A#C#  " + input.takeLast(2))
  //
  // fun solvePart1(): Int? {
  //   val nodesToCostMap = HotelDijkstra().solve(initialState)
  //
  //   return nodesToCostMap[State.from(goalPartOne)]
  // }
  //
  // fun solvePart2() : Int? {
  //   val nodesToCostMap = HotelDijkstra().solve(initialStateExtended)
  //
  //   return nodesToCostMap[State.from(goalPartTwo)]
  // }
  //
  // private fun organizeAmphipods(initialState: State): Int {
  //   val toVisit = PriorityQueue<StateWithCost>().apply { add(StateWithCost(initialState, 0)) }
  //   val visited = mutableSetOf<StateWithCost>()
  //   val currentCosts = mutableMapOf<State, Int>().withDefault { MAX_VALUE }
  //
  //   while (toVisit.isNotEmpty()) {
  //     val current = toVisit.poll().also { visited.add(it) }
  //     current.state.nextPossibleStates().forEach { next ->
  //       if (!visited.contains(next)) {
  //         val newCost = current.cost + next.cost
  //         if (newCost < currentCosts.getValue(next.state)) {
  //           currentCosts[next.state] = newCost
  //           toVisit.add(StateWithCost(next.state, newCost))
  //         }
  //       }
  //     }
  //   }
  //
  //   val cheapestFinishedState = currentCosts.keys.first { it.isFinished() }
  //
  //   return currentCosts.getValue(cheapestFinishedState)
  // }
  //
  // data class State(private val config: List<List<Char>>) {
  //   private val hallway = config[0]
  //   private val rooms = config.drop(1)
  //   private val destinationRooms = mapOf(
  //     'A' to Room('A', 2, rooms.map { row -> row[2] }),
  //     'B' to Room('B', 4, rooms.map { row -> row[4] }),
  //     'C' to Room('C', 6, rooms.map { row -> row[6] }),
  //     'D' to Room('D', 8, rooms.map { row -> row[8] })
  //   )
  //   private val multipliers = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
  //   private val legalHallwayIndexes
  //     get() = listOf(0, 1, 3, 5, 7, 9, 10).filter { hallway[it] == '.' }
  //
  //   fun isFinished() = destinationRooms.values.all { it.hasOnlyValidAmphipods() }
  //
  //   fun nextPossibleStates(): List<StateWithCost> {
  //     return mutableListOf<StateWithCost>().apply {
  //       amphipodsInHallwayThatCanMove().forEach {
  //         val room = destinationRooms.getValue(it.value)
  //         if (hallwayPathIsClear(it.index, room.index)) {
  //           val y = room.content.lastIndexOf('.') + 1
  //           val cost = (abs(it.index - room.index) + y) * multipliers.getValue(it.value)
  //           add(StateWithCost(State(
  //             config.map { row -> row.toMutableList() }.apply {
  //               get(0)[it.index] = '.'
  //               get(y)[room.index] = it.value
  //             }
  //           ), cost))
  //         }
  //       }
  //       roomsWithWrongAmphipods().forEach { room ->
  //         val toMove = room.content.withIndex().first { it.value != '.' }
  //         legalHallwayIndexes.forEach { index ->
  //           if (hallwayPathIsClear(index, room.index)) {
  //             val y = toMove.index + 1
  //             val cost = (abs(room.index - index) + y) * multipliers.getValue(toMove.value)
  //             add(StateWithCost(State(
  //               config.map { row -> row.toMutableList() }.apply {
  //                 get(y)[room.index] = '.'
  //                 get(0)[index] = toMove.value
  //               }
  //             ), cost))
  //           }
  //         }
  //       }
  //     }
  //   }
  //
  //   private fun amphipodsInHallwayThatCanMove(): List<IndexedValue<Char>> {
  //     return hallway.withIndex().filter {
  //       it.value.isLetter() && destinationRooms.getValue(it.value).isEmptyOrHasAllValidAmphipods()
  //     }
  //   }
  //
  //   private fun roomsWithWrongAmphipods() = destinationRooms.values.filter { it.hasAmphipodsWithWrongType() }
  //
  //   private fun hallwayPathIsClear(start: Int, end: Int): Boolean {
  //     return hallway.slice(
  //       when (start > end) {
  //         true -> (start - 1) downTo end
  //         false -> (start + 1)..end
  //       }
  //     ).all { it == '.' }
  //   }
  //
  //   companion object {
  //     fun from(input: List<String>) = State(input.drop(1).dropLast(1).map { it.drop(1).dropLast(1).toList() })
  //   }
  // }
  //
  // class StateWithCost(val state: State, val cost: Int) : DijkstraNodeWithCost<State, Int> {
  //   // override fun compareTo(other: StateWithCost) = cost.compareTo(other.cost)
  //
  //   override fun compareTo(other: DijkstraNodeWithCost<State, Int>): Int {
  //     return cost.compareTo(other.cost())
  //   }
  //
  //   override fun neighbors(): List<DijkstraNodeWithCost<State, Int>> {
  //     return state.nextPossibleStates()
  //   }
  //
  //   override fun node(): State {
  //     return state
  //   }
  //
  //   override fun cost(): Int {
  //     return cost
  //   }
  // }
  //
  // private class Room(val char: Char, val index: Int, val content: List<Char>) {
  //   fun hasOnlyValidAmphipods() = content.all { it == char }
  //
  //   fun isEmptyOrHasAllValidAmphipods() = content.all { it == '.' || it == char }
  //
  //   fun hasAmphipodsWithWrongType() = !isEmptyOrHasAllValidAmphipods()
  // }
// }
