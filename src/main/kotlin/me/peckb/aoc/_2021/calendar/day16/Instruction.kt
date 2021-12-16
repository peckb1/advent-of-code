package me.peckb.aoc._2021.calendar.day16

sealed class Instruction(protected val version: Int, protected val packetType: Int) {
  abstract fun versionSum(): Int
  abstract fun value(): Long

  class Operator(version: Int, packet: Int, private val data: List<Instruction>) : Instruction(version, packet) {
    override fun versionSum() = version + data.sumOf { it.versionSum() }

    override fun value(): Long {
      return when (packetType) {
        0 -> data.sumOf { it.value() }
        1 -> data.productOf { it.value() }
        2 -> data.minOf { it.value() }
        3 -> data.maxOf { it.value() }
        5 -> if (data.first().value() > data.last().value()) 1 else 0
        6 -> if (data.first().value() < data.last().value()) 1 else 0
        7 -> if (data.first().value() == data.last().value()) 1 else 0
        else -> throw Exception("Invalid Operator")
      }
    }
  }

  class Literal(version: Int, packet: Int, private val data: Long) : Instruction(version, packet) {
    override fun versionSum() = version

    override fun value() = data
  }

  protected fun <T> List<T>.productOf(selector: (T) -> Long) =
    this.fold(1L) { acc, t -> acc * selector(t) }
}