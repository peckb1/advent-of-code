package me.peckb.aoc._2023.calendar.day05

data class Data(
  val seedsToPlant: List<Long>,
  val seedToSoil: TransformationMap,
  val soilToFertilizer: TransformationMap,
  val fertilizerToWater: TransformationMap,
  val waterToLight: TransformationMap,
  val lightToTemperature: TransformationMap,
  val temperatureToHumidity: TransformationMap,
  val humidityToLocation: TransformationMap
)
