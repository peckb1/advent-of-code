package me.peckb.aoc._2023.calendar.day20

import me.peckb.aoc._2023.calendar.day20.Day20.Module.Broadcaster
import me.peckb.aoc._2023.calendar.day20.Day20.Module.FlipFlop
import me.peckb.aoc._2023.calendar.day20.Day20.Pulse.HIGH
import me.peckb.aoc._2023.calendar.day20.Day20.Pulse.LOW
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.util.ArithmeticUtils.lcm
import java.util.LinkedList
import java.util.Queue

class Day20 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::module) { input ->
    val modules = input.associateBy { it.id }.apply {
      values.filterIsInstance(Module.Conjunction::class.java).map { conjunction ->
        conjunction.addSenderData(values.filter { it.destinationIds.contains(conjunction.id) })
      }
    }

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
    val modules = input.associateBy { it.id }.apply {
      values.filterIsInstance(Module.Conjunction::class.java).map { conjunction ->
        conjunction.addSenderData(values.filter { it.destinationIds.contains(conjunction.id) })
      }
    }

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
      val combinationNode = modules[path.last()]!! as Module.Conjunction

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


  private fun module(line: String): Module {
    val (typeAndName, destinationsString) = line.split(" -> ")
    val destinations = destinationsString.split(", ")

    return when {
      typeAndName.startsWith("broadcaster") -> Broadcaster("broadcaster", destinations)
      typeAndName.startsWith('%') -> FlipFlop(typeAndName.drop(1), destinations)
      typeAndName.startsWith('&') -> Module.Conjunction(typeAndName.drop(1), destinations)
      else -> throw IllegalArgumentException("Unknown Module data $typeAndName")
    }
  }

  sealed class Module(val id: String, val destinationIds: List<String>) {
    protected val messages: Queue<Pair<String, Pulse>> = LinkedList()

    fun receivePulse(senderId: String, pulse: Pulse) {
      messages.add(senderId to pulse)
    }

    abstract fun handleStoredPulses(modules: Map<String, Module>): List<Pair<String, Pulse>>

    abstract fun state(): String

    class FlipFlop(id: String, destinations: List<String>) : Module(id, destinations) {
      private var on = false

      override fun handleStoredPulses(modules: Map<String, Module>): List<Pair<String, Pulse>> {
        val destinationsWithNewMessage = mutableListOf<Pair<String, Pulse>>()

        while (messages.isNotEmpty()) {
          val (_, pulse) = messages.remove()
          if (pulse == LOW) {
            on = !on
            if (on) {
              destinationIds.forEach {
                destinationsWithNewMessage.add(it to HIGH)
                modules[it]!!.receivePulse(id, HIGH)
              }
            } else {
              destinationIds.forEach {
                destinationsWithNewMessage.add(it to LOW)
                modules[it]!!.receivePulse(id, LOW)
              }
            }
          }
        }

        return destinationsWithNewMessage
      }

      override fun state(): String {
        return "$id ${if (on) 1 else 0} "
      }
    }

    class Conjunction(id: String, destinations: List<String>) : Module(id, destinations) {
      val senderHistory = mutableMapOf<String, Pulse>()

      override fun handleStoredPulses(modules: Map<String, Module>): List<Pair<String, Pulse>> {
        val destinationsWithNewMessage = mutableListOf<Pair<String, Pulse>>()

        while (messages.isNotEmpty()) {
          val (sender, pulse) = messages.remove()
          senderHistory[sender] = pulse
        }

        val pulseToSend = if (senderHistory.values.all { it == HIGH }) LOW else HIGH
        destinationIds.forEach {
          destinationsWithNewMessage.add(it to pulseToSend)
          modules[it]?.receivePulse(id, pulseToSend)
        }

        return destinationsWithNewMessage
      }

      fun addSenderData(senders: List<Module>) {
        senders.forEach { senderHistory[it.id] = LOW }
      }

      override fun state(): String {
        return "C $id $senderHistory"
      }
    }

    class Broadcaster(id: String, destinations: List<String>) : Module(id, destinations) {
      override fun handleStoredPulses(modules: Map<String, Module>): List<Pair<String, Pulse>> {
        val destinationsWithNewMessage = mutableListOf<Pair<String, Pulse>>()

        while (messages.isNotEmpty()) {
          val (_, pulse) = messages.remove()
          destinationIds.forEach {
            destinationsWithNewMessage.add(it to pulse)
            modules[it]?.receivePulse(id, pulse)
          }
        }

        return destinationsWithNewMessage
      }

      override fun state(): String {
        return "B $id"
      }
    }

    override fun toString(): String {
      return when (this) {
        is Broadcaster -> "B [$id, $destinationIds]"
        is Conjunction -> "C [$id, $destinationIds]"
        is FlipFlop -> "F [$id, $destinationIds]"
      }
    }
  }

  enum class Pulse(private val symbol: String) {
    HIGH("H"),
    LOW("L");

    override fun toString(): String {
      return symbol
    }
  }
}
