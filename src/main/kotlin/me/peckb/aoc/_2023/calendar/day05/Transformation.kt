package me.peckb.aoc._2023.calendar.day05

class Transformation(private val source: Long, private val destination: Long, range: Long) {
  private val transformationRange: LongRange = (source until source + range)
  private val reverseTransformationRange: LongRange = (destination until destination + range)

  fun transform(s: Long): Long? {
    return if (transformationRange.contains(s)) {
      destination + (s - source)
    } else {
      null
    }
  }

  fun reverseTransformation(d: Long): Long? {
    return if (reverseTransformationRange.contains(d)) {
      source + (d - destination)
    } else {
      null
    }
  }
}
