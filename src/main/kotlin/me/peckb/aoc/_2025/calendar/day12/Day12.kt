package me.peckb.aoc._2025.calendar.day12

import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day12 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  companion object {
    private const val PRESENT_COUNT = 6
    private const val PRESENT_DATA_SIZE = 5
  }

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val presents = mutableListOf<Present>()
    val regions = mutableListOf<Region>()

    val inputData = input.toList()

    inputData.take(PRESENT_COUNT * PRESENT_DATA_SIZE)
      .chunked(PRESENT_DATA_SIZE)
      .forEach { (_, top, middle, bottom, _) ->
        presents.add(Present(listOf(top.toCharArray(), middle.toCharArray(), bottom.toCharArray())))
      }

    inputData.drop(PRESENT_COUNT * PRESENT_DATA_SIZE)
      .forEach { regionLine ->
        val (size, presentIndices) = regionLine.split(": ")
        val (w, h) = size.split("x").map { it.toInt() }
        val region = presentIndices.split(" ")
          .map { it.toInt() }
          .let {
            Region(
              width = w,
              height = h,
              neededPresentIndices = it.mapIndexed { i, p -> PresentIndex(i, p) }
            )
          }
        regions.add(region)
      }

    regions.count { region ->
      val areaNeeded = region.neededPresentIndices.sumOf { (index, count) ->
        presents[index].coveredAreaCount * count
      }

      region.area > areaNeeded
    }
  }
}

data class Present(val area: List<CharArray>) {
  val coveredAreaCount = area.sumOf { it.count { it == '#' } }
}

data class Region(val width: Int, val height: Int, val neededPresentIndices: List<PresentIndex>) {
  val area = width * height
}

data class PresentIndex(val index: Int, val count: Int)