package me.peckb.aoc._2024.calendar.day25

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day25 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val keys = mutableListOf<Key>()
    val locks = mutableListOf<Lock>()
    // The locks are schematics that have the top row filled (#)
    // the keys have the top row empty and the bottom row filled.
    input.chunked(8).forEach search@ { image ->
      var a = 0
      var b = 0
      var c = 0
      var d = 0
      var e = 0
      var lock = false

      val searchRange = if (image[0] == "#####") { lock = true; 1 .. 5 } else { 5 downTo 1 }
      searchRange.forEach { i ->
        if (image[i][0] == '#') { a++ }
        if (image[i][1] == '#') { b++ }
        if (image[i][2] == '#') { c++ }
        if (image[i][3] == '#') { d++ }
        if (image[i][4] == '#') { e++ }
      }

      if (lock) { locks.add(Lock(a, b, c, d, e)) } else { keys.add(Key(a, b, c, d, e)) }
    }

    keys.sumOf { key ->
      val (ka, kb, kc, kd, ke) = key

      locks.count { lock ->
        val (la, lb, lc, ld, le) = lock

        listOf(ka + la, kb + lb, kc + lc, kd + ld, ke + le).all { it <= 5 }
      }
    }
  }
}

data class Key(val a: Int, val b: Int, val c: Int, val d: Int,val e: Int)

data class Lock(val a: Int, val b: Int, val c: Int, val d: Int,val e: Int)
