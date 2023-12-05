package me.peckb.aoc._2023.calendar.day05

class TransformationMap(private val transformations: List<Transformation>) {
  fun transform(s: Long): Long {
    return transformations.firstNotNullOfOrNull { it.transform(s) } ?: s
  }

  fun reverseTransform(s: Long): Long {
    return transformations.firstNotNullOfOrNull { it.reverseTransformation(s) } ?: s
  }
}
