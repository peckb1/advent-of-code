package me.peckb.aoc._2024.calendar.day24

import me.peckb.aoc.generators.CombinationsGenerator
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
    val data = mutableMapOf<String, Lazy<Int>>()
    val xValues = mutableListOf<String>()
    val yValues = mutableListOf<String>()
    val zValues = mutableListOf<String>()
    val wirings = mutableMapOf<String, Wiring>()

    val swaps = mapOf(
      "kth" to "z12", "carryChain13" to "z12",
      "z12" to "kth", "z12" to "carryChain13",
      "gsd" to "z26", "carry26" to "z26",
      "z26" to "gsd", "z26" to "carry26",
      "tbt" to "z32", "carryAfter32" to "z32",
      "z32" to "tbt", "z32" to "carryAfter32",
      "qnf" to "vpm",
      "vpm" to "qnf",
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
          data[key] = lazy { value.toInt() }
        }
      } else {
        line.split(" ").let { (a, operation, b, _, _result) ->
          val result = swaps[_result] ?: _result

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
          data[result] = lazy {
            op(data[a]!!.value, data[b]!!.value)
          }
        }
      }
    }

    val binaryX = xValues.sorted().map { data[it]!!.value }.reversed().joinToString("")
    val binaryY = yValues.sorted().map { data[it]!!.value }.reversed().joinToString("")

    val initialBinaryZ = zValues.sorted().map { data[it]!!.value }.reversed().joinToString("")
    // 1100101000010010000000110000000111000011101000
    // 1100100111001101111100110000001000000011101000
    //                                  ^
    val expectedBinaryZ = (binaryX.toLong(2) + binaryY.toLong(2)).toString(2)

    val sources = mutableMapOf<String, List<String>>()
    (33 .. 45).forEach loop@ { n ->
      val key = "z${n.toString().padStart(2, '0')}"

      val mySources = mutableSetOf<String>()
      val toCheck = LinkedList<String>()
      toCheck.add(key)
      while(toCheck.isNotEmpty()) {
        val (a, op, b, r) = wirings[toCheck.poll()]!!
        if (r.length == 3) {
          println("$a $op $b = $r")
        }

        if (a.startsWith('x') || a.startsWith('y')) { mySources.add(a) } else { toCheck.add(a) }
        if (b.startsWith('x') || b.startsWith('y')) { mySources.add(b) } else { toCheck.add(b) }
      }
      println()
      if (expectedBinaryZ[45 - n].digitToInt() != data[key]!!.value) {
        return@read -1
      }
      sources[key] = mySources.toList().sorted()
    }

//    val z00Sources = mutableListOf<String>()
//    val toCheck = LinkedList<String>()
//    toCheck.add("z00")
//    while(toCheck.isNotEmpty()) {
//      val (a, b, _) = wirings[toCheck.poll()]!!
//      if (a.startsWith('x') || a.startsWith('y')) {
//        z00Sources.add(a)
//      } else {
//        toCheck.add(a)
//      }
//      if (b.startsWith('x') || b.startsWith('y')) {
//        z00Sources.add(b)
//      } else {
//        toCheck.add(b)
//      }
//    }
    listOf("kth", "z12", "gsd", "z26", "tbt", "z32", "qnf", "vpm").sorted().joinToString(",")
  }

//  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
//    var readingBase = true
//
//    val data = mutableMapOf<String, () -> Int>()
//    val zValues = mutableListOf<String>()
//    val xValues = mutableListOf<String>()
//    val yValues = mutableListOf<String>()
//    val outputValues = mutableListOf<String>()
//
//    val swaps = mutableMapOf<String, String>()
//
//    input.forEach input@{ line ->
//      if (line.isEmpty()) {
//        readingBase = false
//        return@input
//      }
//      if (readingBase) {
//        line.split(": ").let { (key, value) ->
//          if (key.startsWith('x')) { xValues.add(key) }
//          if (key.startsWith('y')) { yValues.add(key) }
//          data[key] = { value.toInt() }
//        }
//      } else {
//        line.split(" ").let { (first, operator, second, _, result) ->
//          outputValues.add(result)
//          if (result.startsWith('z')) {
//            zValues.add(result)
//          }
//          val op = when (operator) {
//            "AND" -> Int::and
//            "OR" -> Int::or
//            "XOR" -> Int::xor
//            else -> throw IllegalArgumentException("Unknown operation $operator")
//          }
//          data[result] = {
//            val a = swaps[first] ?: first
//            val b = swaps[second] ?: second
//
//            op(data[a]!!(), data[b]!!())
//          }
//        }
//      }
//    }
//
//    val binaryX = xValues.sorted().map { data[it]!!() }.reversed().joinToString("")
//    val binaryY = yValues.sorted().map { data[it]!!() }.reversed().joinToString("")
//
//    val initialBinaryZ = zValues.sorted().map { data[it]!!() }.reversed().joinToString("")
//    val expectedBinaryZ = (binaryX.toLong(2) + binaryY.toLong(2)).toString(2)
//
//    val diff = expectedBinaryZ.zip(initialBinaryZ).map { (a, b) ->
//      if (a == b) 0 else 1
//    }.joinToString("").padStart(expectedBinaryZ.length, '0')
//
//    val singleSwapDiffs = CombinationsGenerator.findCombinations(outputValues.toTypedArray(), 2).mapIndexedNotNull { i, s ->
//      println(i)
//      val (a, b) = s
//      swaps.clear()
//      swaps[a] = b
//      swaps[b] = a
//
//      var use = false
//      try {
//        val newZ = zValues.sorted().map { data[it]!!() }.reversed().joinToString("")
//        val myDiff = initialBinaryZ.zip(newZ).map { (a, b) ->
//          if (a == b) 0 else 1.also { use = true }
//        }.joinToString("").padStart(initialBinaryZ.length, '0')
//
//        if (use) {
////          println("change for $a and $b")
//          (a to b) to myDiff
//        } else {
////          println("No change for $a and $b")
//          null
//        }
//      } catch (e: StackOverflowError) {
////        println("Don't swap $a and $b")
//        null
//      }
//    }
//
//    -1
//    // 1100100111001101111100110000001000000011101000
//    // 1100101000010010000000110000000111000011101000
//    // 0000001111011111111100000000001111000000000000
//
//    /*
//
//
//             z12                                                       z13                         z14                          z15                 z26                  z27                        z28                          z29                        z30                          z31                        z32                         z33                         z34                         z36                           z37                         z38                        z39
//      psw     OR     nng                                               mtp     XOR     kth         rpt     XOR     hbh          ckk     XOR    sqb      x26 AND y26      swt     XOR     cmf         ghk    XOR     pgc          fht     XOR     vbp         nvq    XOR     gtd          trd     XOR     dtj         vtg    AND     bkh          nwm    XOR     rpb          ksf    XOR     bhh          htb    XOR     qnf          hpp      XOR     wkk         phr     XOR     ths         hhd    XOR     qnv
//  x12 AND y12    nhb     AND                        cdq                           mtp AND kth     nhb XOR cdq  cns OR qrs     y14 XOR x14  x15 XOR y15     ctc OR skj               y27 XOR x27     gsd OR kbg  tbc OR nvm     y28 XOR x28  y29 XOR x29     crb OR qhw  drg OR wbb     x30 XOR y30  x31 XOR y31     rkn OR mck  nhq OR bjf     x32 XOR y32  tbt OR skt     x33 XOR y33  kmb OR jtq     x34 XOR y34  bbb OR cnp     y36 AND x36  x37 XOR y37      thg OR vpm  x38 XOR y38     ggw OR gpv  bdg OR kmg     x39 XOR y39
//             y12 XOR x12        fkw                  OR     mdq
//                         tnr    AND     kdw     x11 AND y11
//                     mjb OR knt     y11 XOR x11
//              y10 AND x10  kqs AND fnm
//                     rjj OR qsm   y10 XOR x10
//              hsq AND bpc  x09 AND y09
//          x09 XOR y09 rbb      OR gth
//                   ndh AND whc    y08 AND x08
//              y08 XOR x08  hnm     OR vqk
//                       mwg AND wnd    x07 AND y07
//                 mjs OR dbc    x07 XOR y07
//             jrg AND rjb  x06 AND y06
//         y06 XOR x06   vth    OR jvv
//                   x05 AND y05  str AND gtt
//                            hwt OR mfj  x05 XOR y05
//                       y04 AND x04  vwf AND dft
//                               y04 XOR x04  nbj OR rnw
//                                      twj AND jfr  y03 AND x03
//                                   fph OR nkm  x03 XOR y03
//                              y02 AND x02  rhr AND rsk
//                                      cbq OR gwd  x02 XOR y02
//     */
//  }
}

data class Wiring(val a: String, val op: String, val b: String, val result: String)