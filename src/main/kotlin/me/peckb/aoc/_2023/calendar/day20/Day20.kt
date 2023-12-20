package me.peckb.aoc._2023.calendar.day20

import me.peckb.aoc._2023.calendar.day20.Day20.Module.Broadcaster
import me.peckb.aoc._2023.calendar.day20.Day20.Module.FlipFlop
import me.peckb.aoc._2023.calendar.day20.Day20.Pulse.HIGH
import me.peckb.aoc._2023.calendar.day20.Day20.Pulse.LOW
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import org.apache.commons.math3.util.ArithmeticUtils
import org.apache.commons.math3.util.ArithmeticUtils.lcm
import java.util.LinkedList
import java.util.Queue

class Day20 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::module) { input ->
    val modules = input.associateBy { it.id }
    modules.values.filterIsInstance(Module.Conjunction::class.java).map { conjunction ->
      conjunction.addSenderData(
        modules.values.filter { it.destinationIds.contains(conjunction.id) }
      )
    }

    var totalLow: Long = 0
    var totalHigh: Long = 0

    repeat(1000) {
      val (high, low) = pushButton(it + 1L, modules)
      totalLow += low
      totalHigh += high
    }

    totalLow * totalHigh
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::module) { input ->
    val modules = input.associateBy { it.id }


    listOf(
      "111110100111",
      "111101100111",
      "111101101011",
      "111110110011",
    ).map { it.toLong(2) }.fold(1L) { a, b -> lcm(a, b) }
//    val modules = input.associateBy { it.id }
//    modules.values.filterIsInstance(Module.Conjunction::class.java).map { conjunction ->
//      conjunction.addSenderData(
//        modules.values.filter { it.destinationIds.contains(conjunction.id) }
//      )
//    }
//
//    var totalLow: Long = 0
//    var totalHigh: Long = 0
//
////    var counter = 0L
//    repeat(10) {
//      val (high, low) = pushButton(it + 1L, modules)
////      totalLow += low
////      totalHigh += high
////      counter++
//
//      listOf("qm", "kb", "hv", "rn", "ml", "gk", "qv", "ks", "xk", "nv", "vq", "qj", "kd", "ts")
//        .forEach { print(modules[it]!!.state()) }
//      println()
//      listOf("ct", "mj", "xg", "vs", "pg", "lz", "tk", "vk", "jg", "fr", "fl", "tp", "mh", "qs")
//        .forEach { print(modules[it]!!.state()) }
//      println()
//      listOf("ft", "qn", "sk", "qd", "jp", "pc", "xl", "gb", "rp", "bk", "bd", "lg", "cm", "dt")
//        .forEach { print(modules[it]!!.state()) }
//      println()
//      listOf("hr", "zq", "th", "bh", "lj", "cx", "jk", "fs", "lh", "pz", "mf", "mp", "zz", "js")
//        .forEach { print(modules[it]!!.state()) }
//      println()
//      println()
//    }
  }

  private fun pushButton(timePressed: Long, modules: Map<String, Module>): Pair<Long, Long> {
    val modulesToHandleMessage: Queue<Module> = LinkedList<Module>().apply {
      modules["broadcaster"]?.receivePulse("button", LOW)
      add(modules["broadcaster"]!!)
    }

    var lowPulses: Long = 1
    var highPulses: Long = 0

    while(modulesToHandleMessage.isNotEmpty()) {
      val nextModule = modulesToHandleMessage.remove()
      val messages = nextModule.handleStoredPulses(modules)

      messages.forEach { (destination, pulse) ->
        modules[destination]?.let {
//          if (destination == "cl" && pulse == HIGH) println("cl received $pulse from ${nextModule.id} on press $timePressed")
//          if (it.id == "rx") throw RuntimeException(timePressed.toString())
          modulesToHandleMessage.add(it)
        }
        when (pulse) {
          HIGH -> highPulses++
          LOW  -> lowPulses++
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
//      println("$senderId -$pulse-> $id")
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
        return "$id ${if(on) 1 else 0} "
      }
    }

    // TODO this needs to know about all senders who can send us data, even if they have
    // not sent anything yet
    class Conjunction(id: String, destinations: List<String>) : Module(id, destinations) {
      private val senderHistory = mutableMapOf<String, Pulse>()

      override fun handleStoredPulses(modules: Map<String, Module>): List<Pair<String, Pulse>> {
        val destinationsWithNewMessage = mutableListOf<Pair<String, Pulse>>()

        while (messages.isNotEmpty()) {
          val (sender, pulse) = messages.remove()

          if (id == "ts") {
            if (senderHistory.count { it.value == HIGH } > 1) {
              -1
            }
//            if (pulse == HIGH) {
////              println("cl received HIGH")
//            } else {
//              if (senderHistory[sender] == HIGH) {
//                println("cl changed a HIGH with a LOW for $sender")
//              }
//            }
          }

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

  enum class Pulse(val symbol: String) {
    HIGH("H"),
    LOW("L");

    override fun toString(): String {
      return symbol
    }
  }
}


/*

qm kb hv rn ml gk qv ks xk nv vq qj     {ks, gk, vq, xk, kb, qm, hv, nv, qj} C ts {kd=H}
1  1  1  0  0  1  0  1  1  1  1  1

111110100111

ct mj xg vs pg lz tk vk jg fr fl tp     {mj, fr, tp, fl, tk, jg, xg, lz, ct} C qs {mh=H}
1  1  1  0  0  1  1  0  1  1  1  1

111101100111

ft qn sk qd jp pc xl gb rp bk bd lg      C cm {bd, lg, qd, rp, pc, xl, qn, bk, ft}C dt {cm}
1  1  0  1  0  1  1  0  1  1  1  1

111101101011

hr zq th bh lj cx jk fs lh pz mf mp      C zz {lj, lh, mp, pz, zq, hr, cx, mf, fs}C js {zz}
1  1  0  0  1  1  0  1  1  1  1  1

111110110011
 */