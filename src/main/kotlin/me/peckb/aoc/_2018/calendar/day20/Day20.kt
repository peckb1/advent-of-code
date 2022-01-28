package me.peckb.aoc._2018.calendar.day20

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day20 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { regex ->
    val (path, _) = walk(regex, 0)

    path.length
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { regex ->
    val (path, _) = walk(regex, 0)

    path.length
  }

  private fun walk(regex: String, startIndex: Int): Pair<String, Int> {
    val paths = mutableListOf<String>()
    var index = startIndex
    var path = StringBuilder()

    var done = false
    while(!done && index < regex.length) {
      when (val c = regex[index]) {
        'N', 'S', 'E', 'W' -> path.append(c)
        '(' -> {
          val (longestSubPath, newIndex) = walk(regex, index + 1)
          path.append(longestSubPath)
          index = newIndex - 1
        }
        '|' -> {
          paths.add(path.toString())
          path = StringBuilder()
        }
        ')' -> {
          paths.add(path.toString())
          path = StringBuilder()
          done = true
        }
      }
      index++
    }

    if (paths.isEmpty()) {
      paths.add(path.toString())
    }

    return paths.map { it.removeBackSteps() }.maxByOrNull { it.length }!! to index
  }

  private fun String.removeBackSteps(): String {
    val result = StringBuilder()

    fun alterResult(me: Char, opposite: Char) {
      if (result.last() == opposite) {
        result.deleteCharAt(result.length - 1)
      } else {
        result.append(me)
      }
    }

    forEach { c ->
      if(result.isEmpty()) {
        result.append(c)
      } else {
        when (c) {
          'E' -> alterResult(c, 'W')
          'W' -> alterResult(c, 'E')
          'N' -> alterResult(c, 'S')
          'S' -> alterResult(c, 'N')
        }
      }
    }

    return result.toString()
  }

}
