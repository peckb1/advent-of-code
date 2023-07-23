package me.peckb.aoc._2020.calendar.day16

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day16 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  data class Field(val name: String, val lowerRange: IntRange, val upperRange: IntRange)

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (fields, _, otherTicketData) = setup(input)

    val ticketScanningErrors = otherTicketData.mapNotNull { ticketData ->
      val invalidTicketValues = ticketData.filterNot { ticketValue ->
        fields.any { (_, lowerRange, upperRange) ->
          lowerRange.contains(ticketValue) || upperRange.contains(ticketValue)
        }
      }
      if (invalidTicketValues.isNotEmpty()) {
        invalidTicketValues.sum()
      } else {
        null
      }
    }

    ticketScanningErrors.sum()
  }


  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (fields, myTicketData, otherTicketData) = setup(input)

    // first filter out all the bad tickets
    val validTickets = otherTicketData
      .filter { ticketValues ->
      val invalidTicketValues = ticketValues.filterNot { ticketValue ->
        fields.any { (_, lowerRange, upperRange) ->
          lowerRange.contains(ticketValue) || upperRange.contains(ticketValue)
        }
      }
      invalidTicketValues.isEmpty()
    }

    // then find out which index of our ticket scan can match to which fields
    val fieldIndexMap = mutableMapOf<Int, List<Field>>()
    fields.indices.forEach { ticketNumberIndex ->
      var possibleFields = fields.toList()
      validTickets.forEach { ticketData ->
        val ticketItemWeCareAbout = ticketData[ticketNumberIndex]
        possibleFields = possibleFields.filter {
          it.lowerRange.contains(ticketItemWeCareAbout) || it.upperRange.contains(ticketItemWeCareAbout)
        }
      }
      fieldIndexMap[ticketNumberIndex] = possibleFields
    }

    // now that we have all possible fields for each index, let's grab them one by one
    // and know the actual index in our scan for each field
    val foundFieldIndicesMap = mutableMapOf<Field, Int>()
    fieldIndexMap.entries.sortedBy { it.value.size }
      .forEach { (index, possibleFields) ->
        val onlyPossibility = possibleFields.filterNot { foundFieldIndicesMap.contains(it) }.first()
        foundFieldIndicesMap[onlyPossibility] = index
      }

    // and for the answer to the data we get the departure fields and find their product
    foundFieldIndicesMap
      .filter { it.key.name.contains("departure") }
      .map { myTicketData[it.value] }
      .fold(1L) { acc, ticketValue -> acc * ticketValue}
  }

  private fun setup(input: Sequence<String>): Triple<List<Field>, List<Int>, List<List<Int>>> {
    val data = input.toList()
    val fields = data.takeWhile { it.isNotBlank() }
      .map { it ->
        val name = it.substringBefore(':')
        val parts = it.substringAfter(":").split(" ")
        val (lowerLowerRange, lowerUpperRange) = parts[1].split("-").map{ it.toInt() }
        val (upperLowerRange, upperUpperRange) = parts[3].split("-").map{ it.toInt() }

        Field(name, lowerLowerRange..lowerUpperRange, upperLowerRange .. upperUpperRange)
      }

    val myTicketData = data.asSequence().drop(fields.size + 1)
      .takeWhile { it.isNotBlank() }
      .drop(1)
      .map { it.split(",").map { ticketValue -> ticketValue.toInt() } }
      .first()

    val otherTicketData = data.drop(fields.size + 4)
      .takeWhile { it.isNotBlank() }
      .drop(1)
      .map { it.split(",").map { ticketValue -> ticketValue.toInt() } }

    return Triple(fields, myTicketData, otherTicketData)
  }
}
