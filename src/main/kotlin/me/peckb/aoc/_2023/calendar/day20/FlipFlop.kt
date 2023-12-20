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

  override fun toString(): String = id
}
