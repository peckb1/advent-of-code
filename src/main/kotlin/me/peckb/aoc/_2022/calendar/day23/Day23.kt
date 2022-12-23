package me.peckb.aoc._2022.calendar.day23

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
        if (c == '#') elfLocations.add(Elf(x, y).also { it.movement = Movement.North })
      }
    }

    fun round () : Boolean {
      // first half consider positions
      // key = new location
      // map = elves proposing the change
      val proposedLocations = mutableMapOf <Elf, List<Elf>>()

      elfLocations.forEach loop@ { elf ->
        val neighbor: Elf? = elf.neighbors().firstOrNull { elfLocations.contains(it) }
        if (neighbor == null) {
          elf.movement = elf.movement.next()
          return@loop
        }

        val recommendedMovement: Movement? = elf.movement.movements().firstOrNull { movement ->
          movement.missingElfRelativePositions.none { (dx, dy) ->
            elfLocations.contains(Elf(elf.x + dx, elf.y + dy))
          }
        }

        recommendedMovement?.also { recommended ->
          val newLocation = when (recommended) {
            Movement.East  -> Elf(elf.x + 1, elf.y)
            Movement.North -> Elf(elf.x, elf.y - 1)
            Movement.South -> Elf(elf.x, elf.y + 1)
            Movement.West  -> Elf(elf.x - 1, elf.y)
          }.also { it.movement = elf.movement.next()}

          proposedLocations.merge(newLocation, listOf(elf)) { listA, listB ->
            listA.plus(listB)
          }
        }

        if (recommendedMovement == null) {
          elf.movement = elf.movement.next()
        }
      }

      if (proposedLocations.isEmpty()) return false

      // second half each elf simultaneously move
      // if solo move, you can move, else if shared move, don't move
      var someoneMoved = false
      proposedLocations.forEach { (newLocation, elvesWhoWantToMoveThere) ->
        if (elvesWhoWantToMoveThere.size == 1) {
          someoneMoved = true
          elfLocations.remove(elvesWhoWantToMoveThere.first())
          elfLocations.add(newLocation)
        } else {
          elvesWhoWantToMoveThere.forEach { e ->
            e.also { it.movement = e.movement.next() }
          }
        }
      }

      return someoneMoved
    }

    var move = 0
    var someoneMoved = true
    while(someoneMoved && move < maxRounds) {
      someoneMoved = round()
      move++
    }

    return emptyCounts(elfLocations) to move
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

    return (minY..maxY).sumOf { y ->
      (minX..maxX).count { x ->
        !elfLocations.contains(Elf(x, y))
      }
    }
  }

  data class Elf(val x: Int, val y: Int) {
    lateinit var movement: Movement

    fun neighbors(): List<Elf> =
      (-1..1).flatMap { dy ->
        (-1..1).mapNotNull { dx ->
          if (dx == 0 && dy == 0) null else Elf(x + dx, y + dy)
        }
      }
  }

  data class Delta(val dx: Int, val dy: Int)

  sealed class Movement(val missingElfRelativePositions: List<Delta>) {
    abstract fun next(): Movement
    abstract fun movements(): List<Movement>

    object North : Movement(
      listOf(Delta(-1, -1), Delta(0, -1), Delta(1, -1))
    ) {
      override fun next(): Movement = South
      override fun movements() = listOf(North, South, West, East)
    }

    object South : Movement(
      listOf(Delta(-1, 1), Delta(0, 1), Delta(1, 1))
    ) {
      override fun next(): Movement = West
      override fun movements() = listOf(South, West, East, North)
    }

    object West : Movement(
      listOf(Delta(-1, -1), Delta(-1, 0), Delta(-1, 1))
    ) {
      override fun next(): Movement = East
      override fun movements() = listOf(West, East, North, South)
    }

    object East : Movement(
      listOf(Delta(1, -1), Delta(1, 0), Delta(1, 1))
    ) {
      override fun next(): Movement = North
      override fun movements() = listOf(East, North, South, West)
    }
  }
}
