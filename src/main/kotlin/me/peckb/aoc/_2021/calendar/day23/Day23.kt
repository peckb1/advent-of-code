package me.peckb.aoc._2021.calendar.day23

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import me.peckb.aoc._2021.calendar.day23.Resident.AMBER
import me.peckb.aoc._2021.calendar.day23.Resident.BRONZE
import me.peckb.aoc._2021.calendar.day23.Resident.COPPER
import me.peckb.aoc._2021.calendar.day23.Resident.DESERT
import me.peckb.aoc.generators.InputGenerator
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.Int.Companion.MAX_VALUE

class Day23 @Inject constructor(private val generatorFactory: InputGenerator.InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val floor = readInput(input)
    val moves = floor.availableMoves()

    val cheapestCost = AtomicInteger(MAX_VALUE)

    runBlocking {
      val started = moves.size
      val completed = AtomicInteger(0)
      val cheapestMoves = moves.map { movement: Movement ->
        async(context = Dispatchers.Default) {
          // copy floor
          val myFloor = floor.cloneFloor()

          // make move manually
          myFloor.makeMove(movement)

          // println("Starting search after $movement")

          // start checking every move synchronously for each of these
          myFloor.findCheapestRearrangement(ArrayDeque(listOf(movement)), movement.cost, cheapestCost).also {
            // println("Finished $movement")
            println("${completed.incrementAndGet()} / $started done.")
          }
        }
      }.awaitAll().mapNotNull { it }

      if (cheapestMoves.isEmpty()) {
        null
      } else {
        println(cheapestMoves.minByOrNull { it.first })
        cheapestMoves.minOf { it.first }
      }
    }
  }

  private fun readInput(input: Sequence<String>): Floor {
    val data = input.toList()

    val hallwaySetup = data[1]
    val firstRow = listOf(data[2].replace("#", "").toCharArray())
    val nextRows = data.subList(3, data.size - 1).map { it.replace(" ", "").replace("#", "").toCharArray() }
    val totalRooms = nextRows.size + 1

    val hallway = Array<Resident?>(hallwaySetup.count { it == '.' }) { null }
    val rooms = Resident.values().associateWith { Array<Resident?>(totalRooms) { null } }

    firstRow.plus(nextRows).forEachIndexed { index, row ->
      rooms[AMBER]?.let { it[index] = Resident.fromResident(row[0]) }
      rooms[BRONZE]?.let { it[index] = Resident.fromResident(row[1]) }
      rooms[COPPER]?.let { it[index] = Resident.fromResident(row[2]) }
      rooms[DESERT]?.let { it[index] = Resident.fromResident(row[3]) }
    }

    return Floor(hallway, rooms)
  }
}
