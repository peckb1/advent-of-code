package me.peckb.aoc._2022.calendar.day07

import me.peckb.aoc._2022.calendar.day07.Day07.Directory.Companion.createDirectory
import me.peckb.aoc._2022.calendar.day07.Day07.Directory.Companion.createRoot
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

    val currentFree = totalAllowed - allDirectories.first { it.name == ROOT_PATH }.size
    val freeSpaceNeeded = needFree - currentFree

    allDirectories.filter { it.size > freeSpaceNeeded }.minOf { it.size }
  }

  private fun scanCommands(input: List<String>): MutableSet<Directory> {
    val rootDirectory = createRoot()
    val allDirectories = mutableSetOf<Directory>().also { it.add(rootDirectory) }

    var currentDirectory = rootDirectory
    var index = 1 // we can skip the first line that just goes into the root

    while (index < input.size) {
      val lineParts = input[index].split(" ")
      val command = lineParts[1]
      val location by lazy { lineParts[2] }

      when (command) {
        LIST_FILES_AND_FOLDERS -> {
          var itemsInDirectory = 0
          var nextIndex = index + 1
          while (nextIndex < input.size && input[nextIndex][0] != COMMAND_START) {
            val item = input[nextIndex]
            item.split(" ").let { (a, b) ->
              when (a) {
                DIRECTORY -> {
                  createDirectory(b, currentDirectory).also {
                    allDirectories.add(it)
                    currentDirectory.directories.putIfAbsent(b, it)
                  }
                }
                else -> currentDirectory.files.add(File(b, a.toLong()))
              }
            }
            itemsInDirectory++
            nextIndex++
          }
          index += itemsInDirectory
        }
        CHANGE_DIRECTORY -> {
          currentDirectory = when (location) {
            ROOT_PATH -> rootDirectory
            PARENT_PATH -> currentDirectory.parent
            // to match modern filesystems if a directory cannot be `cd` into, stay where we are
            else -> currentDirectory.directories[location] ?: currentDirectory
          }
        }

        else -> throw IllegalStateException("Unknown Command $location")
      }

      index++
    }

    return allDirectories
  }

  data class File(val name: String, val size: Long)

  class Directory private constructor(val name: String) {
    lateinit var parent: Directory

    val files = mutableListOf<File>()
    val directories = mutableMapOf<String, Directory>()

    val size: Long by lazy {
      files.sumOf { it.size } + directories.values.sumOf { it.size }
    }

    companion object {
      fun createRoot() =
        Directory(ROOT_PATH).apply { parent = this }

      fun createDirectory(name: String, parent: Directory) =
        Directory(name).apply { this.parent = parent }
    }
  }

  companion object {
    private const val ROOT_PATH = "/"
    private const val PARENT_PATH = ".."
    private const val LIST_FILES_AND_FOLDERS = "ls"
    private const val CHANGE_DIRECTORY = "cd"
    private const val DIRECTORY = "dir"
    private const val COMMAND_START = '$'
  }
}
