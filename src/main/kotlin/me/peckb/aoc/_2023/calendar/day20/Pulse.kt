package me.peckb.aoc._2023.calendar.day20;

enum class Pulse(private val symbol: String) {
  HIGH("H"),
  LOW("L");

  override fun toString(): String = symbol
}
