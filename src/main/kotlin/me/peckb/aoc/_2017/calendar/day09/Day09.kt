package me.peckb.aoc._2017.calendar.day09

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day09 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var groupDepth = 0
    var insideGarbage = false
    var score = 0
    
    input.forEachIndexed loop@ { index, character ->
      when (character) {
        '{' -> {
          if (insideGarbage) return@loop
          val numCancelsBeforeMe = input.countCancelsBefore(index)
          if ((numCancelsBeforeMe % 2) == 0) groupDepth ++
        }
        '}' -> {
          if (insideGarbage) return@loop
          val numCancelsBeforeMe = input.countCancelsBefore(index)
          if ((numCancelsBeforeMe % 2) == 0) {
            score += groupDepth
            groupDepth --
          }
        }
        '<' -> {
          if (insideGarbage) return@loop
          val numCancelsBeforeMe = input.countCancelsBefore(index)
          if ((numCancelsBeforeMe % 2) == 0) insideGarbage = true
        }
        '>' -> {
          if (!insideGarbage) return@loop
          val numCancelsBeforeMe = input.countCancelsBefore(index)
          if ((numCancelsBeforeMe % 2) == 0) insideGarbage = false
        }
      }  
    }

    score
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    var groupDepth = 0
    var insideGarbage = false
    var cancelling = false
    var garbageCount = 0

    input.forEachIndexed loop@ { index, character ->
      if (insideGarbage) garbageCount++
      when (character) {
        '!' -> {
          cancelling = !cancelling
          garbageCount--
        }
        else -> {
          if (cancelling) {
            cancelling = false
            garbageCount--
          }
          when (character) {
            '{' -> {
              if (insideGarbage) return@loop
              val numCancelsBeforeMe = input.countCancelsBefore(index)
              if ((numCancelsBeforeMe % 2) == 0) groupDepth++
            }
            '}' -> {
              if (insideGarbage) return@loop
              val numCancelsBeforeMe = input.countCancelsBefore(index)
              if ((numCancelsBeforeMe % 2) == 0) groupDepth--
            }
            '<' -> {
              if (insideGarbage) return@loop
              val numCancelsBeforeMe = input.countCancelsBefore(index)
              if ((numCancelsBeforeMe % 2) == 0) insideGarbage = true
            }
            '>' -> {
              if (!insideGarbage) return@loop
              val numCancelsBeforeMe = input.countCancelsBefore(index)
              if ((numCancelsBeforeMe % 2) == 0) {
                garbageCount--
                insideGarbage = false
              }
            }
          }
        }
      }
    }

    garbageCount
  }

  private fun String.countCancelsBefore(index: Int): Int {
    var lookback = 1
    var cancels = 0
    var keepLooking = true
    while(keepLooking && index - lookback > 0) {
      if (this[index - lookback] == '!') {
        cancels++
      } else {
        keepLooking = false
      }
      lookback++
    }
    return cancels
  }
}
