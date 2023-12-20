package me.peckb.aoc._2023.calendar.day20

import me.peckb.aoc._2023.calendar.day20.Pulse.HIGH
import me.peckb.aoc._2023.calendar.day20.Pulse.LOW

class FlipFlop(id: String, destinations: List<String>) : Module(id, destinations) {
  private var on = false

  override fun handleStoredPulses(modules: Map<String, Module>): List<Pair<String, Pulse>> {
    val destinationsWithNewMessage = mutableListOf<Pair<String, Pulse>>()

    while (messages.isNotEmpty()) {
      val (_, pulse) = messages.remove()
      if (pulse == LOW) {
        on = !on

        val pulseToSend = if (on) HIGH else LOW

        destinationIds.forEach {
          destinationsWithNewMessage.add(it to pulseToSend)
          modules[it]!!.receivePulse(id, pulseToSend)
        }
      }
    }

    return destinationsWithNewMessage
  }

  override fun toString(): String = id
}
