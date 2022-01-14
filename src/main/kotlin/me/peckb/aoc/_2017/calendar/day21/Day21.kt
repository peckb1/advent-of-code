package me.peckb.aoc._2017.calendar.day21

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day21 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::day21) { input ->
    val translations = input.flatten().groupBy { it.size }
    val output = expand(5, translations)
    output.sumOf { row -> row.count { it == '#' } }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::day21) { input ->
    val translations = input.flatten().groupBy { it.size }
    val output = expand(18, translations)
    output.sumOf { row -> row.count { it == '#' } }
  }

  private fun expand(times: Int, translations: Map<Int, List<Translation>>): List<String> {
    var output = IMAGE
    repeat(times) { output = translations.translate(output) }
    return output
  }

  private fun Map<Int, List<Translation>>.translate(image: List<String>): List<String> {
    val size = image.size
    if (size == 2 || size == 3) {
      val source = image.joinToString("/")
      return this[size]!!.first { it.source == source }.destination.split("/")
    }

    val chunkSize = if (size % 2 == 0) 2 else 3

    val newImages = (0 until size step chunkSize).map { yStart ->
      (0 until size step chunkSize).map { xStart ->
        val imageChunk = (yStart until (yStart + chunkSize)).map { y ->
          buildString {
            (xStart until (xStart + chunkSize)).map { x ->
              append(image[y][x])
            }
          }
        }
        translate(imageChunk)
      }
    }

    return buildList {
      newImages.forEach { destinationRows ->
        repeat(destinationRows[0].size) { index ->
          add(destinationRows.joinToString("") { it[index] })
        }
      }
    }
  }

  private fun day21(line: String): List<Translation> {
    val (originSource, destination) = line.split(" => ")

    val one = originSource.split("/")
    val two = one.rotate90()
    val three = two.rotate90()
    val four = three.rotate90()

    val five = one.flip()
    val six = two.flip()
    val seven = three.flip()
    val eight = four.flip()

    return listOf(one, two, three, four, five, six, seven, eight).map { it.toTranslation(one.size, destination) }
  }

  data class Translation(val size: Int, val source: String, val destination: String)

  private fun List<String>.rotate90(): List<String> {
    val parent = this

    return indices.map { x ->
      buildString {
        ((size - 1) downTo 0).forEach { y ->
          append(parent[y][x])
        }
      }
    }
  }

  private fun List<String>.flip(): List<String> {
    return this.map { it.reversed() }
  }

  private fun List<String>.toTranslation(size: Int, destination: String): Translation {
    return Translation(size, this.joinToString("/"), destination)
  }

  companion object {
    val IMAGE = listOf(
      ".#.",
      "..#",
      "###"
    )
  }
}
