package me.peckb.aoc._2023.calendar.day20

import java.util.Queue
import java.util.LinkedList

abstract class Module(val id: String, val destinationIds: List<String>) {
  protected val messages: Queue<Pair<String, Pulse>> = LinkedList()

  fun receivePulse(senderId: String, pulse: Pulse) { messages.add(senderId to pulse) }

  abstract fun handleStoredPulses(modules: Map<String, Module>): List<Pair<String, Pulse>>
}
