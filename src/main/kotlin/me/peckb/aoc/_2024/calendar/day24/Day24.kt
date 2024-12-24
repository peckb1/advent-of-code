package me.peckb.aoc._2024.calendar.day24

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.util.LinkedList

class Day24 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    var readingBase = true

    val data = mutableMapOf<String, Lazy<Int>>()
    val zValues = mutableListOf<String>()

    input.forEach input@{ line ->
      if (line.isEmpty()) {
        readingBase = false
        return@input
      }
      if (readingBase) {
        line.split(": ").let { (key, value) ->
          data[key] = lazy { value.toInt() }
        }
      } else {
        line.split(" ").let { (first, operator, second, _, result) ->
          if (result.startsWith('z')) {
            zValues.add(result)
          }
          val op = when (operator) {
            "AND" -> Int::and
            "OR" -> Int::or
            "XOR" -> Int::xor
            else -> throw IllegalArgumentException("Unknown operation $operator")
          }
          data[result] = lazy { op(data[first]!!.value, data[second]!!.value) }
        }
      }
    }

    val zResults = zValues.sorted().map { data[it]!!.value }.reversed()

    zResults.joinToString("").toLong(2)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    var readingBase = true
    val data = mutableMapOf<String, () -> Int>()
    val xValues = mutableListOf<String>()
    val yValues = mutableListOf<String>()
    val zValues = mutableListOf<String>()
    val wirings = mutableMapOf<String, Wiring>()

    val swaps = mutableMapOf<String, String>(
//      /*"kth" to "z12",*/ "carryChain13" to "z12",
//      /*"z12" to "kth",*/ "z12" to "carryChain13",
//      /*"gsd" to "z26",*/ "carry26" to "z26",
//      /*"z26" to "gsd",*/ "z26" to "carry26",
//      /*"tbt" to "z32",*/ "carryAfter32" to "z32",
//      /*"z32" to "tbt",*/ "z32" to "carryAfter32",
//      "qnf" to "vpm",
//      "vpm" to "qnf",
    )

    input.forEach input@{ line ->
      if (line.isEmpty()) {
        readingBase = false
        return@input
      }
      if (readingBase) {
        line.split(": ").let { (key, value) ->
          if (key.startsWith('x')) { xValues.add(key) }
          if (key.startsWith('y')) { yValues.add(key) }
          data[key] = { value.toInt() }
        }
      } else {
        line.split(" ").let { (a, operation, b, _, possibleResult) ->

          val result = swaps[possibleResult] ?: possibleResult

          if (result.startsWith('z')) {
            zValues.add(result)
          }
          val op = when (operation) {
            "AND" -> Int::and
            "OR" -> Int::or
            "XOR" -> Int::xor
            else -> throw IllegalArgumentException("Unknown operation $operation")
          }
          wirings[result] = Wiring(a, operation, b, result)
          data[result] = {
            op(data[a]!!(), data[b]!!())
          }
        }
      }
    }

    val binaryX = xValues.sortedDescending().map { data[it]!!() }.joinToString("")
    val binaryY = yValues.sortedDescending().map { data[it]!!() }.joinToString("")

    val goodBinaryZ = (binaryX.toLong(2) + binaryY.toLong(2)).toString(2)
//    val result222 = zValues.sortedDescending().map { data[it]!!() }.joinToString("")
//    val goodBinaryZ = (binaryX.toLong(2) + binaryY.toLong(2)).toString(2)

    fun swap(swap: Pair<String, String>) {
      val oldA = wirings[swap.first]!!
      val oldB = wirings[swap.second]!!

      val newB = oldB.copy(result = oldA.result)
      val newA = oldA.copy(result = oldB.result)

      wirings[swap.first] = newB
      wirings[swap.second] = newA

      val opA = findOperation(newA.op)
      val opB = findOperation(newB.op)

      data[newA.result] = { opA(data[newA.a]!!(), data[newA.b]!!()) }
      data[newB.result] = { opB(data[newB.a]!!(), data[newB.b]!!()) }

      swaps[swap.first] = swap.second
      swaps[swap.second] = swap.first
    }

    val correctWirings = mutableMapOf<String, List<Wiring>>()

    fun populateCorrectWirings(result: String) {
      val myWirings = mutableListOf<Wiring>()
      val toCheck = LinkedList<String>()
      toCheck.add(result)
      while(toCheck.isNotEmpty()) {
        val wiring = wirings[toCheck.poll()]!!
        val (a, op, b, r) = wiring

        myWirings.add(wiring)

        val aIsInput = a.startsWith('x') || a.startsWith('y')
        val bIsInput = b.startsWith('x') || b.startsWith('y')

        if (!aIsInput) { toCheck.add(a) }
        if (!bIsInput) { toCheck.add(b) }
      }

      correctWirings[result] = myWirings
    }

    (0 .. 45).forEach loop@{ n ->
      val previousPreviousKey = "z${(n-2).toString().padStart(2, '0')}"
      val previousKey = "z${(n-1).toString().padStart(2, '0')}"
      val key = "z${n.toString().padStart(2, '0')}"
      when (n) {
        0 -> checkZero(wirings)?.also { swap(it) }
        1 -> checkOne(wirings)?.also { swap(it) }
        in (2..44) -> checkDefault(
          wirings = wirings,
          key = n to key,
          previousWirings = n - 1 to previousKey,
          previousPreviousWirings = n - 2 to previousPreviousKey,
          correctWirings = correctWirings
        )?.also { swap(it) }
        // don't need to check the last - the error won't be there.
      }
      populateCorrectWirings(key)
    }
    if (swaps.size != 8) {
      throw IllegalStateException("Did not find all the swaps")
    }

    val result = zValues.sortedDescending().map { data[it]!!() }.joinToString("")
    if (result != goodBinaryZ) throw IllegalStateException("We found eight swaps, but didn't get the right result")
    
    swaps.keys.sorted().joinToString(",")
  }

  private fun findOperation(op: String) : (Int, Int) -> Int {
    return when (op) {
      "AND" -> Int::and
      "OR" -> Int::or
      "XOR" -> Int::xor
      else -> throw IllegalArgumentException("Unknown operation $op")
    }
  }

  private fun checkZero(wirings: MutableMap<String, Wiring>) : Pair<String, String>? {
    val z00Wiring = wirings["z00"]!!
    val expectedInput = setOf("x00", "y00")
    return if (z00Wiring.op != "XOR" || expectedInput != z00Wiring.input()) {
      // find `x00 XOR y00 = ???`
      val itemToSwapTo = wirings.entries.first { (_, w) ->
        val (a, op, b, _) = w
        op == "XOR" && expectedInput == setOf(a, b)
      }
      return "z00" to itemToSwapTo.key
    } else {
      null
    }
  }

  private fun checkOne(wirings: MutableMap<String, Wiring>): Pair<String, String>? {
    val z01Wiring = wirings["z01"]!!

    // aaa XOR bbb = z01
    // y01 XOR x01 = bbb
    // x00 AND y00 = aaa
    val aaaInput = setOf("x00", "y00")
    val bbbInput = setOf("x01", "y01")

    val aaa = wirings.entries.first { (_, w) -> w.op == "AND" && aaaInput == setOf(w.a, w.b) }
    val bbb = wirings.entries.first { (_, w) -> w.op == "XOR" && bbbInput == setOf(w.a, w.b) }

    val correctInput = setOf(aaa.key, bbb.key)
    if (z01Wiring.input() == correctInput) {
      return null
    } else {
      // is there a `aaa.key AND bbb.key` which we need to swap to z01?
      val maybeZSwap = wirings.entries.find { (_, w) -> w.op == "XOR" && setOf(w.a, w.b) == setOf(aaa.key, bbb.key) }
      if (maybeZSwap != null) {
        return "z01" to maybeZSwap.key
      } else {
        // our "z01" was correct - so the input needs swapping
        return if (!z01Wiring.input().contains(aaa.key) && !z01Wiring.input().contains(bbb.key)) {
          // full disjointSet - this should not happen in the input
          throw IllegalStateException("Input has full disjoint set!")
        } else {
          // one of the items is missing
          if (aaa.key in z01Wiring.input()) {
            // bbb needs swap
            bbb.key to z01Wiring.input().minus(aaa.key).first()
          } else {
            // aaa needs swap
            aaa.key to z01Wiring.input().minus(bbb.key).first()
          }
        }
      }
    }
  }

  fun checkDefault(
    wirings: MutableMap<String, Wiring>,
    key: Pair<Int, String>,
    previousWirings: Pair<Int, String>,
    previousPreviousWirings: Pair<Int, String>,
    correctWirings: Map<String, List<Wiring>>,
  ): Pair<String, String>? {
    val myWirings = mutableListOf<Wiring>()
    val toCheck = LinkedList<String>()
    toCheck.add(key.second)
    while(toCheck.isNotEmpty()) {
      val wiring = wirings[toCheck.poll()]!!
      val (a, op, b, r) = wiring

      myWirings.add(wiring)

      val aIsInput = a.startsWith('x') || a.startsWith('y')
      val bIsInput = b.startsWith('x') || b.startsWith('y')

      if (!aIsInput) { toCheck.add(a) }
      if (!bIsInput) { toCheck.add(b) }
    }

    val currentWirings = myWirings.minus(correctWirings[previousWirings.second]!!)
    val previousNewWirings = correctWirings[previousWirings.second]!!.minus(correctWirings[previousPreviousWirings.second]!!)

    // the items we need ...
    // for zN
    // sum(N-1)         AND  carryChain(N-1)  = carryAfter(N-1)
    // y(N-1)           AND  x(N-1)           = carry(N-1)
    // carryAfter(N-1)  OR   carry(N-1)       = carryChainN
    // yN               XOR     yN            = sumN
    // sumN             XOR  carryChainN      = zN

    // carry(N-2) is in the previousNewWirings as x(N-2) AND y(N-2)
    val x2 = previousPreviousWirings.first.let { "x${it.toString().padStart(2, '0')}" }
    val y2 = previousPreviousWirings.first.let { "y${it.toString().padStart(2, '0')}" }
    val carryN2 = previousNewWirings.first { w -> w.op == "AND" && setOf(w.a, w.b) == setOf(x2, y2) }

    // sum(N-1)
    val x1 = previousWirings.first.let { "x${it.toString().padStart(2, '0')}" }
    val y1 = previousWirings.first.let { "y${it.toString().padStart(2, '0')}" }
    val sumN1 = previousNewWirings.first { w -> w.op == "XOR" && setOf(w.a, w.b) == setOf(x1, y1) }

    // carry(N-1)
    val carryN1 = wirings.entries.first { (_, w) -> w.op == "AND" && setOf(w.a, w.b) == setOf(x1, y1) }.value

    // sumN
    val x = key.first.let { "x${it.toString().padStart(2, '0')}" }
    val y = key.first.let { "y${it.toString().padStart(2, '0')}" }
    val sumN = wirings.entries.first { (_, w) -> w.op == "XOR" && setOf(w.a, w.b) == setOf(x, y) }.value

    // carryChain(N-1)
    val carryChainN1 = previousNewWirings.find { w -> w.op == "OR" } ?: previousNewWirings.first { w -> w.op == "AND" }
    // carryAfter(N-1)
    val carryAfterN1 = wirings.entries.find { (_, w) -> w.op == "AND" && setOf(w.a, w.b) == setOf(sumN1.result, carryChainN1.result) }?.value

    if (carryAfterN1 == null) {
      // TODO: update to match below
      throw IllegalArgumentException("Something Wrong with $carryChainN1 or $sumN1")
    }

    // carryChainN
    val carryChainN = wirings.entries.find { (_, w) -> w.op == "OR" && setOf(w.a, w.b) == setOf(carryAfterN1.result, carryN1.result) }?.value
    if (carryChainN == null) {
      // TODO: update to match below
      throw IllegalArgumentException("Something Wrong with $carryAfterN1 or $carryN1")
    }

    // zN
    val zN = wirings.entries.find { (_, w) -> w.op == "XOR" && setOf(w.a, w.b) == setOf(sumN.result, carryChainN.result) }?.value
    if (zN == null) {
      val correctZN = wirings[key.second]!!
      if (carryChainN.result in correctZN.input()) {
        // something bad with sumN
        return sumN.result to correctZN.input().minus(carryChainN.result).first()
      } else {
        // something bad with carryChainN
        return carryChainN.result to correctZN.input().minus(sumN.result).first()
      }
    }

    if (setOf(carryN1, sumN, carryAfterN1, carryChainN, zN) != currentWirings.toSet()) {
      // if we got this far - we need to swap out zN values
      val toSwapWith = currentWirings.first { it.result == key.second }
      return toSwapWith.result to zN.result
    }

    return null
  }
}

data class Wiring(val a: String, val op: String, val b: String, val result: String) {
  fun input() = setOf(a, b)
}