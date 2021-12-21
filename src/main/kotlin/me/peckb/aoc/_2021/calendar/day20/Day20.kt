package me.peckb.aoc._2021.calendar.day20

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day20 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  companion object {
    const val OUTPUT_LIT = '#'
    const val OUTPUT_DIM = ' '

    const val INPUT_LIT = '#'
    const val INPUT_DIM = '.'

    private fun conversion(c: Char): Char {
      return when (c) {
        INPUT_DIM -> OUTPUT_DIM
        INPUT_LIT -> OUTPUT_LIT
        else -> throw IllegalArgumentException()
      }
    }
  }

  fun partOne(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val (algorithm, image) = parse(input)

    val imageArray = runEnhancement(algorithm, image.map { it.toList() }, 2)

    imageArray.sumOf { it.count { c -> c == OUTPUT_LIT } }
  }

  fun partTwo(fileName: String) = generatorFactory.forFile(fileName).read { input ->
    val (algorithm, image) = parse(input)

    val imageArray = runEnhancement(algorithm, image.map { it.toList() }, 50)

    imageArray.sumOf { it.count { c -> c == OUTPUT_LIT } }
  }

  private fun runEnhancement(algorithm: Algorithm, input: List<List<Char>>, times: Int): List<List<Char>> {
    var imageArray = input
    repeat(times) {
      imageArray = enhance(algorithm, imageArray)
    }
    return imageArray
  }

  private fun enhance(algorithm: Algorithm, imageArray: List<List<Char>>): List<List<Char>> {
    algorithm.incrementEnhance()
    val borderSizeIncrease = 2
    val result = MutableList(imageArray.size + borderSizeIncrease) {
      MutableList(imageArray[0].size + borderSizeIncrease) {
        OUTPUT_DIM
      }
    }

    result.indices.forEach { resultY ->
      result[resultY].indices.forEach { resultX ->
        val imageYFromResultY = resultY - (borderSizeIncrease / 2)
        val imageXFromResultY = resultX - (borderSizeIncrease / 2)

        val encoding = (imageYFromResultY - 1..imageYFromResultY + 1).flatMap { imageY ->
          (imageXFromResultY - 1..imageXFromResultY + 1).map { imageX ->
            imageArray.find(imageY, imageX, algorithm)
          }
        }

        val code = encoding.map { if (it == OUTPUT_DIM) 0 else 1 }
          .joinToString("")
          .toInt(2)

        result[resultY][resultX] = conversion(algorithm.fromCode(code))
      }
    }

    return result
  }

  private fun List<List<Char>>.find(y: Int, x: Int, algorithm: Algorithm) : Char {
    return this.getOrNull(y)?.getOrNull(x)?: conversion(algorithm.getInfinity())
  }

  private fun parse(input: Sequence<String>): Pair<Algorithm, List<String>> {
    val data = input.toList()
    val algorithm = Algorithm.fromData(data.first())
    val image = data.drop(2)

    return algorithm to image.map { it.replace(INPUT_LIT, OUTPUT_LIT).replace(INPUT_DIM, OUTPUT_DIM) }
  }

  class Algorithm private constructor(private val enhancement: String, private var infiniteMarker: Char) {
    companion object {
      fun fromData(enhancement: String) = Algorithm(enhancement, enhancement[0])
    }

    fun fromCode(code: Int): Char = enhancement[code]

    fun incrementEnhance() {
      val num = if (infiniteMarker == INPUT_LIT) 1 else 0
      infiniteMarker = fromCode(num.toString().repeat(9).toInt(2))
    }

    fun getInfinity() = infiniteMarker
  }
}
