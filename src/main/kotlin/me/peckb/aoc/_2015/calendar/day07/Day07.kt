package me.peckb.aoc._2015.calendar.day07

import me.peckb.aoc._2015.calendar.day07.Day07.Logic.And
import me.peckb.aoc._2015.calendar.day07.Day07.Logic.LeftShift
import me.peckb.aoc._2015.calendar.day07.Day07.Logic.Not
import me.peckb.aoc._2015.calendar.day07.Day07.Logic.Or
import me.peckb.aoc._2015.calendar.day07.Day07.Logic.Provides
import me.peckb.aoc._2015.calendar.day07.Day07.Logic.RightShift
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day07 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::bitLogic) { input ->
    val gates = mutableMapOf<String, Logic>()
    input.forEach { gates[it.destination] = it }

    gates["a"]!!.resolve(gates)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::bitLogic) { input ->
    val gates = mutableMapOf<String, Logic>()
    input.forEach { gates[it.destination] = it }

    val a = gates["a"]!!.resolve(gates)
    gates["b"] = Provides(a.toString(), "b")

    gates.values.forEach { it.resetMemoization() }
    gates["a"]!!.resolve(gates)
  }

  private fun bitLogic(line: String): Logic {
    val directionalSplit = line.split("->")
    val valueToSet = directionalSplit.last().trim()

    val input = directionalSplit.dropLast(1).first().trim().split(" ")

    return when (input.size) {
      1 -> Provides(input[0], valueToSet)
      2 -> Not(input[1], valueToSet)
      3 -> when (input[1]) {
        "AND" -> And(input[0], input[2], valueToSet)
        "OR" -> Or(input[0], input[2], valueToSet)
        "LSHIFT" -> LeftShift(input[0], input[2].toInt(), valueToSet)
        "RSHIFT" -> RightShift(input[0], input[2].toInt(), valueToSet)
        else -> throw IllegalArgumentException("Unknown Logic $line")
      }
      else -> throw IllegalArgumentException("Unknown Logic $line")
    }
  }

  sealed class Logic(val destination: String) {
    companion object {
      const val MAX = Short.MAX_VALUE.toInt()
    }

    private var resolvedValue: Int? = null

    protected abstract fun resolveGate(gates: MutableMap<String, Logic>) : Int

    fun resetMemoization() { resolvedValue = null }

    fun resolve(gates: MutableMap<String, Logic>): Int {
      return resolvedValue ?: resolveGate(gates).also { resolvedValue = it }
    }

    class Provides(private val signal: String, _destination: String) : Logic(_destination) {
      override fun resolveGate(gates: MutableMap<String, Logic>): Int {
        return signal.toIntOrNull() ?: gates[signal]!!.resolve(gates)
      }

      override fun toString() = "$signal -> $destination"
    }

    class And(private val inputA: String, private val inputB: String, _destination: String) : Logic(_destination) {
      override fun resolveGate(gates: MutableMap<String, Logic>): Int {
        val a = inputA.toIntOrNull() ?: gates[inputA]!!.resolve(gates)
        val b = inputB.toIntOrNull() ?: gates[inputB]!!.resolve(gates)
        return a and b
      }

      override fun toString() = "$inputA AND $inputB -> $destination"
    }

    class Or(private val inputA: String, private val inputB: String, _destination: String) : Logic(_destination) {
      override fun resolveGate(gates: MutableMap<String, Logic>): Int {
        val a = inputA.toIntOrNull() ?: gates[inputA]!!.resolve(gates)
        val b = inputB.toIntOrNull() ?: gates[inputB]!!.resolve(gates)
        return a or b
      }

      override fun toString() = "$inputA OR $inputB -> $destination"
    }

    class LeftShift(val input: String, private val shiftValue: Int, _destination: String) : Logic(_destination) {
      override fun resolveGate(gates: MutableMap<String, Logic>): Int {
        val a = gates[input]!!.resolve(gates)
        return a shl shiftValue
      }

      override fun toString() = "$input LSHIFT $shiftValue -> $destination"
    }

    class RightShift(val input: String, private val shiftValue: Int, _destination: String) : Logic(_destination) {
      override fun resolveGate(gates: MutableMap<String, Logic>): Int {
        val a = gates[input]!!.resolve(gates)
        return a shr shiftValue
      }

      override fun toString() = "$input RSHIFT $shiftValue -> $destination"
    }

    class Not(val input: String, _destination: String) : Logic(_destination) {
      override fun resolveGate(gates: MutableMap<String, Logic>): Int {
        return MAX + (gates[input]!!.resolve(gates).inv() + 1)
      }

      override fun toString() = "NOT $input -> $destination"
    }
  }
}
