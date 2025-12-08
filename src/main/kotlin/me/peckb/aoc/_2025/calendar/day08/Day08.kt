package me.peckb.aoc._2025.calendar.day08

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.PriorityQueue
import java.util.UUID
import kotlin.collections.get
import kotlin.math.sqrt
import kotlin.math.pow

class Day08 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day08) { input ->
    val (_, distances) = createConnections(input)
    val circuits = connectBoxes(distances) { connections, _ ->
      // short circuit after we have made 1000 connections
      connections >= 1000
    }.first

    circuits.map { it.value.boxes.size }.sortedByDescending { it }.take(3).fold(1) { acc, n -> acc * n }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day08) { input ->
    val (boxes, distances) = createConnections(input)
    val lastConnection = connectBoxes(distances) { _, circuits ->
      // short circuit after we have a circuit that contains every box
      circuits[boxes.first().circuitId]?.boxes?.size == boxes.size
    }.second

    lastConnection.fold(1L) { acc, n -> acc * n.x }
  }

  private fun day08(line: String) = line.split(",")
    .map { it.toLong() }
    .let { (x, y, z) -> Box(x, y, z) }

  private fun createConnections(input: Sequence<Box>): Pair<List<Box>, PriorityQueue<Distance>> {
    val boxes = input.toList()

    val distances = PriorityQueue<Distance>()

    boxes.forEachIndexed { index, box1 ->
      ((index + 1)until boxes.size).forEach { box2Index ->
        distances.add(Distance(box1, boxes[box2Index]))
      }
    }

    return boxes to distances
  }

  private fun connectBoxes(distances: PriorityQueue<Distance>, shortCircuit: (Long, Map<String, Circuit>) -> Boolean) : Pair<MutableMap<String, Circuit>, MutableList<Box>> {
    var connectionsMade = 0L
    val circuits = mutableMapOf<String, Circuit>()
    val lastConnection = mutableListOf<Box>()

    while(distances.isNotEmpty()) {
      connectionsMade++
      val distance = distances.poll()
      val b1 = distance.b1
      val b2 = distance.b2

      if (b1.circuitId != null && b2.circuitId == b1.circuitId) {
        // if the two are already in the same circuit
        // do nothing!
      } else if (b1.circuitId == null && b2.circuitId == null) {
        // if neither are in a circuit, make a new circuit that contains them
        val circuit = Circuit(UUID.randomUUID().toString())
        b1.circuitId = circuit.id
        b2.circuitId = circuit.id
        circuit.add(b1)
        circuit.add(b2)
        circuits[circuit.id] = circuit
      } else if (b1.circuitId == null) {
        // b1 gets to join b2's circuit
        circuits[b2.circuitId]!!.add(b1)
        b1.circuitId = b2.circuitId
      } else if (b2.circuitId == null) {
        // b2 gets to join b1's circuit
        circuits[b1.circuitId]!!.add(b2)
        b2.circuitId = b1.circuitId
      } else {
        // we need to collapse the circuits!
        val firstCircuit = circuits[b1.circuitId]!!
        val secondCircuit = circuits[b2.circuitId]!!
        // we pick b2 to "lose" and join b1 - theoretically you would want to have the smaller circuit join the larger
        circuits.remove(b2.circuitId)
        secondCircuit.boxes.forEach { box ->
          box.circuitId = firstCircuit.id
          firstCircuit.add(box)
        }
      }

      if (shortCircuit(connectionsMade, circuits)) {
        lastConnection.add(b1)
        lastConnection.add(b2)
        distances.clear()
      }
    }

    return circuits to lastConnection
  }
}

data class Box(val x: Long, val y: Long, val z: Long, var circuitId: String? = null) {
  override fun toString(): String = "($x, $y, $z)"
}

data class Circuit(val id: String, val boxes: MutableList<Box> = mutableListOf()) {
  fun add(box: Box) = boxes.add(box)

  override fun toString(): String = "size: ${boxes.size}"
}

data class Distance(val b1: Box, val b2: Box, val distance: Double = euclideanDistance(b1, b2)) : Comparable<Distance> {
  override fun compareTo(other: Distance): Int {
    return this.distance.compareTo(other.distance)
  }
}

fun euclideanDistance(b1: Box, b2: Box): Double {
  val deltaX = b2.x - b1.x
  val deltaY = b2.y - b1.y
  val deltaZ = b2.z - b1.z

  return sqrt(deltaX.toDouble().pow(2) + deltaY.toDouble().pow(2) + deltaZ.toDouble().pow(2))
}