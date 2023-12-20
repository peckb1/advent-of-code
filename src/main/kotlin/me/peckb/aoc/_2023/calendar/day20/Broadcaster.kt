package me.peckb.aoc._2023.calendar.day20

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

  override fun toString(): String = "B"
}
