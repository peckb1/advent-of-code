package me.peckb.aoc._2017.calendar.day16

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day16 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    dance("abcdefghijklmnop", input)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    val original = "abcdefghijklmnop"
    var afterDance = dance(original, input)
    repeat(1_000_000_000 - 1) {

      if (it % 1_000_000 == 0) println(it / 1_000_000)
      afterDance = dance(original, input)
      if (afterDance == original) {
        println("REPEAT MODE: $it :REPEAT MODE")
        println("REPEAT MODE: $it :REPEAT MODE")
        println("REPEAT MODE: $it :REPEAT MODE")
        println("REPEAT MODE: $it :REPEAT MODE")
        println("REPEAT MODE: $it :REPEAT MODE")
      }
    }

    // not "podlbieamjchfgnk"
    // not "kblheigpmjdafnoc"
    afterDance//.joinToString("")
  }

  @Suppress("SameParameterValue")
  private fun dance(originalPosition: String, input: String): String{
    val dancers = originalPosition.toCharArray()

    input.split(",").forEach { danceMove ->
      println(dancers)
      when (danceMove[0]) {
        's' -> dancers.spin(danceMove.drop(1).toInt())
        'x' -> dancers.swapPositions(danceMove.drop(1).split("/"))
        'p' -> dancers.swapDancers(danceMove.drop(1).split("/"))
        else -> throw IllegalArgumentException("Unknown Dance Move: $danceMove")
      }
    }

    return dancers.joinToString("")
  }
}

private fun CharArray.swapDancers(dancers: List<String>) {
  val i1 = indexOf(dancers.first()[0])
  val i2 = indexOf(dancers.last()[0])
  val temp = this[i1]
  this[i1] = this[i2]
  this[i2] = temp
}

private fun CharArray.swapPositions(positions: List<String>) {
  val i1 = positions.first().toInt()
  val i2 = positions.last().toInt()
  val temp = this[i1]
  this[i1] = this[i2]
  this[i2] = temp
}

private fun CharArray.spin(steps: Int) {
  val positionsToRotate = steps % size
  val newBeginning = slice(size - positionsToRotate until size)
  val newEnd = slice(0 until (size - positionsToRotate))

  newBeginning.plus(newEnd).forEachIndexed { i, c ->
    this[i] = c
  }
}
