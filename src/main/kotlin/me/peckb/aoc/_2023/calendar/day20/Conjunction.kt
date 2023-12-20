package me.peckb.aoc._2023.calendar.day20

import me.peckb.aoc._2023.calendar.day20.Pulse.HIGH
import me.peckb.aoc._2023.calendar.day20.Pulse.LOW

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

  override fun toString(): String = "${senderHistory.map { it.key }}"
}
