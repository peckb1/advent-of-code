package me.peckb.aoc._2023.calendar.day05

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day05 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = generateData(input.iterator())

    data.seedsToPlant.minOf { seed ->
      data.humidityToLocation.transform(
        data.temperatureToHumidity.transform(
          data.lightToTemperature.transform(
            data.waterToLight.transform(
              data.fertilizerToWater.transform(
                data.soilToFertilizer.transform(
                  data.seedToSoil.transform(
                    seed
                  )
                )
              )
            )
          )
        )
      )
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = generateData(input.iterator())

    val seedRanges = data.seedsToPlant.chunked(2).map { seedData ->
      val (start, range) = seedData
      start until start + range
    }

    var location = 0L
    var foundSeed = -1L
    while (foundSeed == -1L) {
      val humidity = data.humidityToLocation.reverseTransform(location)
      val temperature = data.temperatureToHumidity.reverseTransform(humidity)
      val light = data.lightToTemperature.reverseTransform(temperature)
      val water = data.waterToLight.reverseTransform(light)
      val fertilizer = data.fertilizerToWater.reverseTransform(water)
      val soil = data.soilToFertilizer.reverseTransform(fertilizer)
      val seed = data.seedToSoil.reverseTransform(soil)

      if (seedRanges.any { it.contains(seed) }) {
        foundSeed = seed
      } else {
        location++
      }
    }

    location
  }

  private fun generateData(inputIterator: Iterator<String>): Data {
    val seedsToPlant = inputIterator.next().split(": ").last().split(" ").mapNotNull { it.toLongOrNull() }
    inputIterator.next()// blank line

    var seedToSoil = TransformationMap(emptyList())
    var soilToFertilizer = TransformationMap(emptyList())
    var fertilizerToWater = TransformationMap(emptyList())
    var waterToLight = TransformationMap(emptyList())
    var lightToTemperature = TransformationMap(emptyList())
    var temperatureToHumidity = TransformationMap(emptyList())
    var humidityToLocation = TransformationMap(emptyList())

    while (inputIterator.hasNext()) {
      when (inputIterator.next().dropLast(1)) {
        "seed-to-soil map" -> seedToSoil = populateMap(inputIterator)
        "soil-to-fertilizer map" -> soilToFertilizer = populateMap(inputIterator)
        "fertilizer-to-water map" -> fertilizerToWater = populateMap(inputIterator)
        "water-to-light map" -> waterToLight = populateMap(inputIterator)
        "light-to-temperature map" -> lightToTemperature = populateMap(inputIterator)
        "temperature-to-humidity map" -> temperatureToHumidity = populateMap(inputIterator)
        "humidity-to-location map" -> humidityToLocation = populateMap(inputIterator)
      }
    }

    return Data(
      seedsToPlant,
      seedToSoil,
      soilToFertilizer,
      fertilizerToWater,
      waterToLight,
      lightToTemperature,
      temperatureToHumidity,
      humidityToLocation
    )
  }

  private fun populateMap(input: Iterator<String>): TransformationMap {
    val transformations = mutableListOf<Transformation>()

    var data = input.next()
    while (input.hasNext() && data.isNotEmpty()) {
      val (destination, source, range) = data.split(" ").mapNotNull { it.toLongOrNull() }
      transformations.add(Transformation(source, destination, range))

      data = input.next()
    }

    return TransformationMap(transformations)
  }
}
