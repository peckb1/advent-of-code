package me.peckb.aoc._2019.calendar.day18

import me.peckb.aoc._2019.calendar.day18.Day18.Section.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra
import me.peckb.aoc.pathing.GenericIntDijkstra.DijkstraNode
import java.lang.IllegalArgumentException

typealias Cost = Int

class Day18 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val (caves, startingLocation) = createCaves(input)
    caves.print()
    caves[startingLocation.y][startingLocation.x] = EMPTY

    val reachableKeysA = searchForKeys(caves, startingLocation, emptySet())
      .sortedBy { it.second.second }

    val (nextKeyA, infoA) = reachableKeysA.first()
    val (newStartA , costToGetThereA) = infoA
    val reachableKeysB = searchForKeys(caves, newStartA, setOf(nextKeyA))

    val (nextKeyB, infoB) = reachableKeysB.first()
    val (newStartB , costToGetThereB) = infoB
    val reachableKeysC = searchForKeys(caves, newStartB, setOf(nextKeyA, nextKeyB))

    val (nextKeyC, infoC) = reachableKeysC.first()
    val (newStartC , costToGetThereC) = infoC
    val reachableKeysD = searchForKeys(caves, newStartC, setOf(nextKeyA, nextKeyB, nextKeyC))

    val (nextKeyD, infoD) = reachableKeysD.first()
    val (newStartD , costToGetThereD) = infoD
    val reachableKeysE = searchForKeys(caves, newStartD, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD))

    val (nextKeyE, infoE) = reachableKeysE.first()
    val (newStartE , costToGetThereE) = infoE
    val reachableKeysF = searchForKeys(caves, newStartE, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE))

    val (nextKeyF, infoF) = reachableKeysF.first()
    val (newStartF , costToGetThereF) = infoF
    val reachableKeysG = searchForKeys(caves, newStartF, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF))

    val (nextKeyG, infoG) = reachableKeysG.first()
    val (newStartG , costToGetThereG) = infoG
    val reachableKeysH = searchForKeys(caves, newStartG, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG))

    val (nextKeyH, infoH) = reachableKeysH.first()
    val (newStartH , costToGetThereH) = infoH
    val reachableKeysI = searchForKeys(caves, newStartH, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH))

    val (nextKeyI, infoI) = reachableKeysI.first()
    val (newStartI , costToGetThereI) = infoI
    val reachableKeysJ = searchForKeys(caves, newStartI, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI))

    val (nextKeyJ, infoJ) = reachableKeysJ.first()
    val (newStartJ , costToGetThereJ) = infoJ
    val reachableKeysK = searchForKeys(caves, newStartJ, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ))

    val (nextKeyK, infoK) = reachableKeysK.first()
    val (newStartK , costToGetThereK) = infoK
    val reachableKeysL = searchForKeys(caves, newStartK, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK))

    val (nextKeyL, infoL) = reachableKeysL.first()
    val (newStartL , costToGetThereL) = infoL
    val reachableKeysM = searchForKeys(caves, newStartL, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL))

    val (nextKeyM, infoM) = reachableKeysM.first()
    val (newStartM , costToGetThereM) = infoM
    val reachableKeysN = searchForKeys(caves, newStartM, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL, nextKeyM))

    val (nextKeyN, infoN) = reachableKeysN.first()
    val (newStartN , costToGetThereN) = infoN
    val reachableKeysO = searchForKeys(caves, newStartN, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL, nextKeyM, nextKeyN))

    val (nextKeyO, infoO) = reachableKeysO.first()
    val (newStartO , costToGetThereO) = infoO
    val reachableKeysP = searchForKeys(caves, newStartO, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL, nextKeyM, nextKeyN, nextKeyO))

    val (nextKeyP, infoP) = reachableKeysP.first()
    val (newStartP , costToGetThereP) = infoP
    val reachableKeysDone = searchForKeys(caves, newStartP, setOf(nextKeyA, nextKeyB, nextKeyC, nextKeyD, nextKeyE, nextKeyF, nextKeyG, nextKeyH, nextKeyI, nextKeyJ, nextKeyK, nextKeyL, nextKeyM, nextKeyN, nextKeyO, nextKeyP))

    val totalCostForThisPath = costToGetThereA + costToGetThereB + costToGetThereC + costToGetThereD + costToGetThereE + costToGetThereF + costToGetThereG + costToGetThereH + costToGetThereI + costToGetThereJ + costToGetThereK + costToGetThereL + costToGetThereM + costToGetThereN + costToGetThereO + costToGetThereP
    -1
  }

  private fun searchForKeys(
    caves: MutableList<MutableList<Section>>,
    currentLocation: Location,
    keysFound: Set<KEY>
  ): List<Pair<KEY, Pair<Location, Cost>>> {
    val keyLocations = mutableSetOf<Pair<KEY, Location>>()

    val searcher = object : GenericIntDijkstra<Location>() {}

    val start = Location(currentLocation.x, currentLocation.y)
      .withCaves(caves)
      .withKeysFound(keysFound)
      .withKeyLocations(keyLocations)
    val paths = searcher.solve(start)

    return keyLocations.mapNotNull { (key, loc) ->
      val cost = paths[Location(loc.x, loc.y)]
      cost?.let {
        key to (loc to it)
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
    data class KEY(val identifier: Char) : Section(identifier)
    data class DOOR(val identifier: Char) : Section(identifier)
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
