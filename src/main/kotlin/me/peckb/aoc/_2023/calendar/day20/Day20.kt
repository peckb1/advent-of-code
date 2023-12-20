package me.peckb.aoc._2023.calendar.day20

import me.peckb.aoc._2023.calendar.day20.Pulse.HIGH
import me.peckb.aoc._2023.calendar.day20.Pulse.LOW
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.util.ArithmeticUtils.lcm
import java.util.LinkedList
import java.util.Queue

class Day20 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::module) { input ->
    val modules = setupModules(input)

    var totalLow: Long = 0
    var totalHigh: Long = 0

    repeat(1000) {
      val (high, low) = pushButton(modules)
      totalLow += low
      totalHigh += high
    }

    totalLow * totalHigh
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::module) { input ->
    val modules = setupModules(input)

    val groups = modules["broadcaster"]!!.destinationIds

    val paths = groups.map { start ->
      val path = mutableListOf<String>().apply { add(start) }

      var nextId = start
      while (modules[nextId]!! is FlipFlop) {
        val nextModule = modules[nextId]!!
        nextId = nextModule.destinationIds.firstOrNull { modules[it]!! is FlipFlop }
          ?: nextModule.destinationIds.first()
        path.add(nextId)
      }

      path
    }

    val binaryValues = paths.map { path ->
      val combinationNode = modules[path.last()]!! as Conjunction

      val binary = "0".repeat(path.size - 1).toCharArray()
      combinationNode.senderHistory.map { (sender, _) -> binary[path.indexOf(sender)] = '1' }

      binary.reversed().joinToString("")
    }

    binaryValues
      .map { it.toLong(2) }
      .fold(1L) { a, b -> lcm(a, b) }
  }

  private fun pushButton(modules: Map<String, Module>): Pair<Long, Long> {
    val modulesToHandleMessage: Queue<Module> = LinkedList<Module>().apply {
      modules["broadcaster"]?.receivePulse("button", LOW)
      add(modules["broadcaster"]!!)
    }

    var lowPulses: Long = 1
    var highPulses: Long = 0

    while (modulesToHandleMessage.isNotEmpty()) {
      val nextModule = modulesToHandleMessage.remove()
      val messages = nextModule.handleStoredPulses(modules)

      messages.forEach { (destination, pulse) ->
        modules[destination]?.let { modulesToHandleMessage.add(it) }
        when (pulse) {
          HIGH -> highPulses++
          LOW -> lowPulses++
        }
      }
    }

    return highPulses to lowPulses
  }

  private fun setupModules(input: Sequence<Module>): Map<String, Module> {
    return input.associateBy { it.id }.apply {
      values.filterIsInstance(Conjunction::class.java).map { conjunction ->
        conjunction.addSenderData(values.filter { it.destinationIds.contains(conjunction.id) })
      }
    }
  }

  private fun module(line: String): Module {
    val (typeAndName, destinationsString) = line.split(" -> ")
    val destinations = destinationsString.split(", ")

    return when {
      typeAndName.startsWith("broadcaster") -> Broadcaster("broadcaster", destinations)
      typeAndName.startsWith('%') -> FlipFlop(typeAndName.drop(1), destinations)
      typeAndName.startsWith('&') -> Conjunction(typeAndName.drop(1), destinations)
      else -> throw IllegalArgumentException("Unknown Module data $typeAndName")
    }
  }
}
