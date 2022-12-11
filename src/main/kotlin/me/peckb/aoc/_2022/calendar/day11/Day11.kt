package me.peckb.aoc._2022.calendar.day11

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day11 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    findMonkeyBusiness(
      monkeys = parseInput(input),
      rounds = 20,
      work = Monkey::inspectGenericItems
    )
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    findMonkeyBusiness(
      monkeys = parseInput(input),
      rounds = 10_000,
      work = Monkey::inspectFragileItems
    )
  }

  private fun findMonkeyBusiness(monkeys: List<Monkey>, rounds: Int, work: (Monkey, List<Monkey>) -> Unit): Long {
    repeat(rounds) { monkeys.forEach { monkey -> work(monkey, monkeys) } }

    return monkeys.sortedByDescending { it.numberOfItemsInspected }
      .take(2)
      .map { it.numberOfItemsInspected }
      .reduce(Long::times)
  }

  data class Monkey(
    val id: Int,
    val items: MutableList<Long>,
    val operation: (Long) -> Long,
    val test: (Long) -> Boolean,
    val ifTrue: Int,
    val ifFalse: Int
  ) {
    private var bigMod: Long = 0

    var numberOfItemsInspected = 0L
      private set

    fun inspectGenericItems(monkeys: List<Monkey>) {
      inspectItems(monkeys) { operation(it) / THREE }
    }

    fun inspectFragileItems(monkeys: List<Monkey>) {
      inspectItems(monkeys) { operation(it) }
    }

    private fun inspectItems(monkeys: List<Monkey>, newWorryGenerator: (Long) -> Long) {
      items.forEach { item ->
        numberOfItemsInspected++
        // DEV NOTE: this is the "trick" of the problem today:
        //           By doing a `%=` of our Least Common Multiple (of primes, so just the product today)
        //           we can keep our numbers small and our operations quick
        val newWorry = newWorryGenerator(item).mod(bigMod)
        if (test(newWorry)) {
          monkeys[ifTrue].takeItem(newWorry)
        } else {
          monkeys[ifFalse].takeItem(newWorry)
        }
      }
      items.clear()
    }

    private fun takeItem(newItem: Long) {
      items.add(newItem)
    }

    fun tellBigMod(bigMod: Long) {
      this.bigMod = bigMod
    }
  }

  private fun parseInput(input: Sequence<String>): List<Monkey> {
    var bigMod: Long = 1

    return input.chunked(7).map { monkeyData ->
      val id = parseId(monkeyData[0])
      val startingItems = parseStartingItems(monkeyData[1])
      val operation = parseOperation(monkeyData[2])
      val (test, primeDivisor) = parseTest(monkeyData[3])
      val ifTrue = parseMonkeyDestination(monkeyData[4])
      val ifFalse = parseMonkeyDestination(monkeyData[5])

      bigMod *= primeDivisor
      Monkey(id, startingItems, operation, test, ifTrue, ifFalse)
    }.toList().onEach { monkey -> monkey.tellBigMod(bigMod) }
  }

  private fun parseId(line: String): Int {
    return line.split(" ")[1].dropLast(1).toInt()
  }

  private fun parseStartingItems(line: String): MutableList<Long> {
    return line.split(": ")[1].split(", ").map { it.toLong() }.toMutableList()
  }

  private fun parseOperation(line: String): (Long) -> Long {
    return line.split(": ")[1].split(" ").let { (_, _, _, operator, value) ->
      val modifierValue = value.toLongOrNull()
      when (operator) {
        "*" -> { x: Long -> (x * (modifierValue ?: x)) }
        "+" -> { x: Long -> (x + (modifierValue ?: x)) }
        else -> throw IllegalArgumentException("Unknown Operator $operator")
      }
    }
  }

  private fun parseTest(line: String): Pair<(Long) -> Boolean, Long> {
    return line.split(": ")[1]
      .split(" ")[2]
      .toLong()
      .let { ({ x: Long -> x.mod(it) == 0L } to it) }
  }

  private fun parseMonkeyDestination(line: String): Int {
    return line.split(": ")[1].split(" ")[3].toInt()
  }

  companion object {
    private const val THREE = 3L
  }
}
