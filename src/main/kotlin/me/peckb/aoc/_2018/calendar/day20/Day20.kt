package me.peckb.aoc._2018.calendar.day20

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.StringBuilder
import kotlin.Int.Companion.MAX_VALUE
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day20 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readOne { regex ->
    var minX = MAX_VALUE
    var maxX = MIN_VALUE
    var minY = MAX_VALUE
    var maxY = MIN_VALUE

    val (paths, _) = findPaths(regex, 0)
    val source = Point(0, 0)
    val rooms = mutableSetOf(source.copy())
    val eastWestDoors = mutableSetOf<Point>()
    val northSouthDoors = mutableSetOf<Point>()

    paths.forEach { path ->
      val currentLocation = source.copy()

      var index = 0
      while (index < path.length) {
        when (path[index]) {
          'N' -> {
            currentLocation.travelNorth()
            rooms.add(currentLocation.copy())
            northSouthDoors.add(currentLocation.copy(y = currentLocation.y + 1))
          }
          'E' -> {
            currentLocation.travelEast()
            rooms.add(currentLocation.copy())
            eastWestDoors.add(currentLocation.copy(x = currentLocation.x - 1))
          }
          'S' -> {
            currentLocation.travelSouth()
            rooms.add(currentLocation.copy())
            northSouthDoors.add(currentLocation.copy(y = currentLocation.y - 1))
          }
          'W' -> {
            currentLocation.travelWest()
            rooms.add(currentLocation.copy())
            eastWestDoors.add(currentLocation.copy(x = currentLocation.x + 1))
          }
        }

        maxX = max(maxX, currentLocation.x)
        maxY = max(maxY, currentLocation.y)
        minX = min(minX, currentLocation.x)
        minY = min(minY, currentLocation.y)
        index++
      }
    }

    val ySize = abs(maxY) + abs(minY) + 1 // add one for zero index
    val xSize = abs(maxX) + abs(minX) + 1 // add one for zero index

    // add an edge around it for the outer wall
    val area = Array(ySize + 2) { Array(xSize + 2) { '#' } }

    // add our shift value and half the wall size
    val yShift = abs(minY) + 1
    val xShift = abs(minX) + 1

    // fill up the building with rooms and walls
    rooms.forEach { room -> area[room.y + yShift][room.x + xShift] = '.' }
    northSouthDoors.forEach { door -> area[door.y + yShift][door.x + xShift] = '-' }
    eastWestDoors.forEach { door -> area[door.y + yShift][door.x + xShift] = '|' }

    area.forEach { println(it.joinToString("")) }

    -1
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readOne { input ->
    -1
  }

  private fun findPaths(path: String, startIndex: Int): Pair<MutableList<String>, Int> {
    val finishedPaths = mutableListOf<String>()
    var currentPaths = mutableListOf(StringBuilder())

    var index = startIndex
    var endIndex = startIndex
    while(index < path.length) {
      when (val c = path[index]) {
        'N', 'E', 'S', 'W' -> {
          currentPaths.forEach { currentPath -> currentPath.append(c) }
          index++
        }
        '(' -> {
          val (subPaths, newIndex) = findPaths(path, index + 1)
          currentPaths = currentPaths.flatMap { currentPath ->
            subPaths.map { subPath -> StringBuilder("$currentPath$subPath") }
          }.toMutableList()
          index = newIndex + 1
        }
        '|' -> {
          currentPaths.forEach { currentPath -> finishedPaths.add(currentPath.toString()) }
          currentPaths = mutableListOf(StringBuilder())
          index++
        }
        ')' -> {
          endIndex = index
          index = path.length
        }
        else -> index++
      }
    }
    currentPaths.forEach { currentPath -> finishedPaths.add(currentPath.toString()) }

    println("$endIndex / $index -- ${finishedPaths.size}")

    return finishedPaths to endIndex
  }
}

data class Point(var x: Int, var y: Int) {
  fun travelNorth() { y -= 2 }
  fun travelEast() { x += 2 }
  fun travelSouth() { y += 2 }
  fun travelWest() { x -= 2 }
}
