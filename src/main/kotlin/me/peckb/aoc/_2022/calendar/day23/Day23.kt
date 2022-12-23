package me.peckb.aoc._2022.calendar.day23

import me.peckb.aoc._2022.calendar.day23.Movement.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max
import kotlin.math.min

class Day23 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    runExpansion(input, 10).first
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    runExpansion(input, Int.MAX_VALUE).second
  }

  private fun runExpansion(input: Sequence<String>, maxRounds: Int): Pair<Int, Int> {
    val elfLocations: HashSet<Elf> = hashSetOf()

    input.forEachIndexed { y, line ->
      line.forEachIndexed { x, c ->
        if (c == '#') elfLocations.add(Elf(x, y).also { it.movement = North })
      }
    }

    var move = 0
    var someoneMoved = true
    while (someoneMoved && move < maxRounds) {
      someoneMoved = round(elfLocations)
      move++
    }

    return emptyCounts(elfLocations) to move
  }

  private fun round(elfLocations: HashSet<Elf>): Boolean {
    // first half consider positions
    // key = new location
    // map = elves proposing the change
    val proposedLocations = mutableMapOf<Elf, MutableList<Elf>>()

    elfLocations.forEach elfCheck@ { elf ->
      if (elf.neighbors().none { elfLocations.contains(it) }) {
        elf.movement = elf.movement.next
        return@elfCheck
      }

      // do we have a recommended movement?
      val recommendedMovement: Movement? = elf.movement.movements
        .firstOrNull { movement ->
          movement.exclusionPositions.none { (dx, dy) ->
            elfLocations.contains(Elf(elf.x + dx, elf.y + dy))
          }
        }

      // if we do, update the proposedLocations map
      recommendedMovement?.also { recommended ->
        val (newX, newY) = recommended.move(elf.x, elf.y)

        proposedLocations.merge(Elf(newX, newY), mutableListOf(elf)) { a, b ->
          a.also { it.add(b.first()) }
        }
      } ?: run { elf.movement = elf.movement.next }
    }

    // second half each elf simultaneously move
    // if solo move, you can move, else if shared move, don't move
    var someoneMoved = false
    proposedLocations.forEach { (newLocation, elvesWhoWantToMoveThere) ->
      if (elvesWhoWantToMoveThere.size == 1) {
        someoneMoved = true
        val elfWhoMoved = elvesWhoWantToMoveThere.first()
        elfLocations.remove(elfWhoMoved)
        elfLocations.add(newLocation.also { it.movement = elfWhoMoved.movement.next })
      } else {
        elvesWhoWantToMoveThere.forEach { e ->
          e.also { it.movement = e.movement.next }
        }
      }
    }

    return someoneMoved
  }

  private fun emptyCounts(elfLocations: HashSet<Elf>): Int {
    var minX = Int.MAX_VALUE
    var minY = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var maxY = Int.MIN_VALUE

    elfLocations.forEach {
      minX = min(it.x, minX)
      minY = min(it.y, minY)
      maxX = max(it.x, maxX)
      maxY = max(it.y, maxY)
    }

    // this could also just check the size of the box - elf counts
    // but since I used it for pretty printing, so I'm keeping it :D
    return (minY..maxY).sumOf { y ->
      (minX..maxX).count { x ->
        !elfLocations.contains(Elf(x, y))
      }
    }
  }

  data class Elf(val x: Int, val y: Int) {
    lateinit var movement: Movement

    fun neighbors(): List<Elf> = neighborLocations.map { (dx, dy) -> Elf(x + dx, y + dy) }

    companion object {
      private val neighborLocations = (-1..1).flatMap { dy ->
        (-1..1).mapNotNull { dx ->
          if (dx == 0 && dy == 0) null else (dx to dy)
        }
      }
    }
  }

  data class Delta(val dx: Int, val dy: Int)
}
