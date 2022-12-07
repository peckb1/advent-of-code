package me.peckb.aoc._2022.calendar.day07

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import java.lang.IllegalStateException

class Day07 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val allDirectories = scanCommands(input.toList())
    allDirectories.filter { it.size <= 100_000 }.sumOf { it.size }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val allDirectories = scanCommands(input.toList())

    val totalAllowed = 70_000_000
    val needFree = 30_000_000

    val currentFree = totalAllowed - allDirectories.first { it.id == "/" }.size
    val freeSpaceNeeded = needFree - currentFree

    allDirectories.filter { it.size > freeSpaceNeeded }.minOf { it.size }
  }

  private fun scanCommands(input: List<String>): MutableSet<Directory> {
    val rootDirectory = Directory("/", null)
    val allDirectories = mutableSetOf<Directory>().also { it.add(rootDirectory) }

    var currentDirectory = rootDirectory
    var index = 1

    while(index < input.size) {
      val lineParts = input[index].split(" ")
      val command = lineParts[1]
      val location by lazy { lineParts[2] }

      when (command) {
        "ls" -> {
          var itemsInDirectory = 0
          var nextIndex = index + 1
          while (nextIndex < input.size && input[nextIndex][0] != '$') {
            val item = input[nextIndex]
            item.split(" ").let { (a, b) ->
              when (a) {
                "dir" -> {
                  val directory = Directory(b, currentDirectory)
                  allDirectories.add(directory)
                  currentDirectory.directories.putIfAbsent(b, directory)
                }
                else -> currentDirectory.files.putIfAbsent(b, File(b, a.toLong()))
              }
            }
            itemsInDirectory++
            nextIndex++
          }
          index += itemsInDirectory + 1 // add one because we want to have the index pass by the last item in the dir
        }
        "cd" -> {
          currentDirectory = when (location) {
            "/" -> rootDirectory
            ".." -> currentDirectory.parent!!
            else -> currentDirectory.directories[location]!!
          }
          index++
        }
        else -> throw IllegalStateException("Unknown Command $location")
      }
    }

    return allDirectories
  }

  data class File(val name: String, val size: Long)

  data class Directory(val id: String, val parent: Directory?) {
    val files = mutableMapOf<String, File>()
    val directories = mutableMapOf<String, Directory>()

    val size: Long by lazy {
      files.values.sumOf { it.size } + directories.values.sumOf { it.size }
    }
  }
}
