package me.peckb.aoc._2021.calendar.day20

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day20 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val data = input.toList()
    val algorithm = data.first()

    val image = data.drop(2)

    var imageArray = image.map { it.toList() }

    imageArray = enhance(algorithm, imageArray, 1)
    imageArray = enhance(algorithm, imageArray, 2)

    imageArray.sumOf { it.count { c -> c == '#' } }
  }

  private fun enhance(algorithm: String, imageArray: List<List<Char>>, count: Int = 1): List<List<Char>> {
    val result = MutableList(imageArray.size + 10) {
      MutableList(imageArray[0].size + 10) {
        '.'
      }
    }

    result.indices.forEach { resultY ->
      result[resultY].indices.forEach { resultX ->
        val imageYFromResultY = resultY - 5
        val imageXFromResultY = resultX - 5

        val a = (imageYFromResultY-1..imageYFromResultY+1).flatMap { imageY ->
          (imageXFromResultY-1..imageXFromResultY+1).map { imageX ->
            imageArray.find(imageY, imageX, count)
          }
        }

        val b = a.map { if (it == '.') 0 else 1 }

        val c = b.joinToString("")

        val code = c.toInt(2)

        result[resultY][resultX] = algorithm[code]
      }
    }

    return result
  }

  private fun List<List<Char>>.find(y: Int, x: Int, count: Int) : Char {
    return try {
      this[y][x]
    } catch (e: IndexOutOfBoundsException) {
      if (count % 2 == 0) {
        '#'
      } else {
        '.'
      }
    }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val data = input.toList()
    val algorithm = data.first()

    val image = data.drop(2)

    var imageArray = image.map { it.toList() }

    (1..50).forEach { count ->
      imageArray = enhance(algorithm, imageArray, count)
    }

    imageArray.sumOf { it.count { c -> c == '#' } }
  }

  fun day20(line: String) = 4
}
