package me.peckb.aoc._2016.calendar.day15

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day15 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::disk) { input ->
    val data = input.toList().sortedBy { it.id }
    dropCapsule(data)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::disk) { input ->
    val data = input.toList().sortedBy { it.id }
    val newDisk = Disk(data.size + 1, 11, 0)
    dropCapsule(data.plus(newDisk))
  }

  private fun dropCapsule(data: List<Disk>): Long {
    var happy = false
    var time = -1L
    while(!happy) {
      time++
      happy = data.all { disk ->
        (time + disk.id + disk.initialPosition) % disk.positions == 0L
      }
    }
    return time
  }

  private fun disk(line: String): Disk {
    val id = line.substringAfter("#").substringBefore(" ").toInt()
    val positions = line.substringBefore(" positions").substringAfterLast(" ").toInt()
    val initialPosition = line.substringAfterLast(" ").dropLast(1).toInt()

    return Disk(id, positions, initialPosition)
  }

  data class Disk(val id: Int, val positions: Int, val initialPosition: Int)
}
