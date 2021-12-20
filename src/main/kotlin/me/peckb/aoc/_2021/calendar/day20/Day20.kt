package me.peckb.aoc._2021.calendar.day20

import me.peckb.aoc._2021.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day20 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val (algorithm, image) = parse(input)

    var imageArray = image.map { it.toList() }

    (1..2).forEach { count -> imageArray = enhance(algorithm, imageArray, count) }

    imageArray.sumOf { it.count { c -> c == '#' } }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val (algorithm, image) = parse(input)

    var imageArray = image.map { it.toList() }

    (1..50).forEach { count -> imageArray = enhance(algorithm, imageArray, count) }

    imageArray.sumOf { it.count { c -> c == '#' } }
  }

  private fun enhance(algorithm: String, imageArray: List<List<Char>>, count: Int = 1): List<List<Char>> {
    val borderSizeIncrease = 2
    val result = MutableList(imageArray.size + borderSizeIncrease) {
      MutableList(imageArray[0].size + borderSizeIncrease) {
        '.'
      }
    }

    result.indices.forEach { resultY ->
      result[resultY].indices.forEach { resultX ->
        val imageYFromResultY = resultY - (borderSizeIncrease / 2)
        val imageXFromResultY = resultX - (borderSizeIncrease / 2)

        val encoding = (imageYFromResultY - 1..imageYFromResultY + 1).flatMap { imageY ->
          (imageXFromResultY - 1..imageXFromResultY + 1).map { imageX ->
            imageArray.find(imageY, imageX, count, algorithm)
          }
        }

        val code = encoding.map { if (it == '.') 0 else 1 }
          .joinToString("")
          .toInt(2)

        result[resultY][resultX] = algorithm[code]
      }
    }

    return result
  }

  private fun List<List<Char>>.find(y: Int, x: Int, count: Int, algorithm: String) : Char {
    return this.getOrNull(y)?.getOrNull(x)?: run {
      if (count % 2 == 0) {
        algorithm[0]
      } else {
        '.'
      }
    }
  }

  private fun parse(input: Sequence<String>): Pair<String, List<String>> {
    val data = input.toList()
    val algorithm = data.first()
    val image = data.drop(2)

    return algorithm to image
  }
}
