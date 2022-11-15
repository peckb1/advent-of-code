package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc._2019.calendar.day18.Day18.Section.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

typealias Cost = Int

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (caves, startingLocation) = createCaves(input)
    caves.print()
    caves[startingLocation.y][startingLocation.x] = EMPTY

    val reachableKeysA = searchForKeys(caves, startingLocation, emptySet())
      .sortedBy { it.cost }



    // a, f, b, j, g, n, h, d, l, o, e, p, c, i, k, m
    val (nextKeyA, newStartA, costToGetThereA) = reachableKeysA.first { it.key.identifier == 'a' }
    val reachableKeysB = searchForKeys(caves, newStartA, setOf(nextKeyA))

    val (nextKeyB, newStartB, costToGetThereB) = reachableKeysB.first { it.key.identifier == 'f' }
    val reachableKeysC = searchForKeys(caves, newStartB, setOf(nextKeyA, nextKeyB))

    val (nextKeyC, newStartC, costToGetThereC) = reachableKeysC.first { it.key.identifier == 'b' }
    val reachableKeysD = searchForKeys(caves, newStartC, setOf(nextKeyA, nextKeyB, nextKeyC))

    val (nextKeyD, newStartD, costToGetThereD) = reachableKeysD.first { it.key.identifier == 'j' }
    val reachableKeysE = searchForKeys(caves, newStartD, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD))

    val (nextKeyE, newStartE, costToGetThereE) = reachableKeysE.first { it.key.identifier == 'g' }
    val reachableKeysF = searchForKeys(caves, newStartE, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE))

    val (nextKeyF, newStartF, costToGetThereF) = reachableKeysF.first { it.key.identifier == 'n' }
    val reachableKeysG = searchForKeys(caves, newStartF, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF))

    val (nextKeyG, newStartG, costToGetThereG) = reachableKeysG.first { it.key.identifier == 'h' }
    val reachableKeysH = searchForKeys(caves, newStartG, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG))

    val (nextKeyH, newStartH, costToGetThereH) = reachableKeysH.first { it.key.identifier == 'd' }
    val reachableKeysI = searchForKeys(caves, newStartH, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH))

    val (nextKeyI, newStartI, costToGetThereI) = reachableKeysI.first { it.key.identifier == 'l' }
    val reachableKeysJ = searchForKeys(caves, newStartI, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI))

    val (nextKeyJ, newStartJ, costToGetThereJ) = reachableKeysJ.first { it.key.identifier == 'o' }
    val reachableKeysK = searchForKeys(caves, newStartJ, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ))

    val (nextKeyK, newStartK, costToGetThereK) = reachableKeysK.first { it.key.identifier == 'e' }
    val reachableKeysL = searchForKeys(caves, newStartK, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK))

    val (nextKeyL, newStartL, costToGetThereL) = reachableKeysL.first { it.key.identifier == 'p' }
    val reachableKeysM = searchForKeys(caves, newStartL, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL))

    val (nextKeyM, newStartM, costToGetThereM) = reachableKeysM.first { it.key.identifier == 'c' }
    val reachableKeysN = searchForKeys(caves, newStartM, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL, nextKeyM))

    val (nextKeyN, newStartN, costToGetThereN) = reachableKeysN.first { it.key.identifier == 'i' }
    val reachableKeysO = searchForKeys(caves, newStartN, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL, nextKeyM, nextKeyN))

    val (nextKeyO, newStartO, costToGetThereO) = reachableKeysO.first { it.key.identifier == 'k' }
    val reachableKeysP = searchForKeys(caves, newStartO, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL, nextKeyM, nextKeyN, nextKeyO))

    val (nextKeyP, newStartP, costToGetThereP) = reachableKeysP.first { it.key.identifier == 'm' }
    val reachableKeysDone = searchForKeys(caves, newStartP, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL, nextKeyM, nextKeyN, nextKeyO, nextKeyP))

    if (reachableKeysDone.isNotEmpty()) {
      throw IllegalStateException("Still need to find more keys $reachableKeysDone")
    }

    costToGetThereA + costToGetThereB + costToGetThereC + costToGetThereD + costToGetThereE + costToGetThereF + costToGetThereG + costToGetThereH + costToGetThereI + costToGetThereJ + costToGetThereK + costToGetThereL + costToGetThereM + costToGetThereN + costToGetThereO + costToGetThereP
  }

  private fun searchForKeys(
    caves: MutableList<MutableList<Section>>,
    currentLocation: Location,
    keysFound: Set<KEY>
  ): List<AttainableKeys> {
    val keyLocations = mutableSetOf<Pair<KEY, Location>>()

    val searcher = object : GenericIntDijkstra<Location>() {}

    val start = Location(currentLocation.x, currentLocation.y)
      .withCaves(caves)
      .withKeysFound(keysFound)
      .withKeyLocations(keyLocations)
    val paths = searcher.solve(start)

    return keyLocations.mapNotNull { (key, loc) ->
      paths[Location(loc.x, loc.y)]?.let { cost ->
        AttainableKeys(key, loc, cost)
      }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }

  data class Location(val x: Int, val y: Int) : DijkstraNode<Location> {
    private lateinit var caves: MutableList<MutableList<Section>>
    private lateinit var keysFound: Set<KEY>
    private lateinit var keyLocations: MutableSet<Pair<KEY, Location>>

    fun withCaves(caves: MutableList<MutableList<Section>> = mutableListOf()) = apply {
      this.caves = caves
    }

    fun withKeysFound(keysFound: Set<KEY>) = apply {
      this.keysFound = keysFound
    }

    fun withKeyLocations(keyLocations: MutableSet<Pair<KEY, Location>>) = apply {
      this.keyLocations = keyLocations
    }

    override fun neighbors(): Map<Location, Int> {
      val mySpot = caves[y][x]
      if (mySpot is KEY) {
        // if we have not found this key before
        if (!keysFound.contains(mySpot)) {
          // store it as a new key location
          keyLocations.add(mySpot to Location(x, y).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations))
          // and don't explore further from this spot
          return emptyMap()
        }
      }

      // if we did not return early from finding a key, check our neighbors
      val n = caves[y - 1][x] to Location(x, y - 1).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations)
      val e = caves[y][x + 1] to Location(x + 1, y).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations)
      val s = caves[y + 1][x] to Location(x, y + 1).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations)
      val w = caves[y][x - 1] to Location(x - 1, y).withCaves(caves).withKeysFound(keysFound).withKeyLocations(keyLocations)

      val neighborsToExplore = listOf(n, e, s, w).filter { (section, _) ->
        when (section) {
          is DOOR -> {
            keysFound.contains(KEY(section.identifier.lowercaseChar()))
          }
          is KEY -> true
          EMPTY -> true
          PERSON -> true
          WALL -> false
        }
      }

      return neighborsToExplore.map { it.second }.associateWith { 1 }
    }
  }

  data class AttainableKeys(val key: KEY, val location: Location, val cost: Cost)

  private fun createCaves(input: Sequence<String>): Pair<MutableList<MutableList<Section>>, Location> {
    val caves = mutableListOf<MutableList<Section>>()
    var location: Location = Location(-1, -1)

    input.forEachIndexed { y, line ->
      val row = mutableListOf<Section>()
      line.forEachIndexed { x, sectionChar ->
        val section = Section.fromChar(sectionChar)
        row.add(section).also {
          if (section is PERSON) location = Location(x, y)
        }
      }
      caves.add(row)
    }
    return caves to location
  }

  private fun List<List<Section>>.print() {
    forEach { row ->
      row.forEach { print(it) }
      println()
    }
  }

  sealed class Section(private val representation: Char) {
    object WALL : Section('#')
    data class KEY(val identifier: Char) : Section(identifier) {
      override fun toString() = super.toString()
    }
    data class DOOR(val identifier: Char) : Section(identifier) {
      override fun toString() = super.toString()
    }
    object EMPTY : Section('.')
    object PERSON : Section('@')

    override fun toString(): String {
      return representation.toString()
    }

    companion object {
      fun fromChar(c: Char): Section {
        return when {
          ('a' .. 'z').contains(c) -> KEY(c)
          ('A' .. 'Z').contains(c) -> DOOR(c)
          c == '#' -> WALL
          c == '.' -> EMPTY
          c == '@' -> PERSON
          else -> throw IllegalArgumentException("Unknown representation: $c")
        }
      }
    }
  }
}
