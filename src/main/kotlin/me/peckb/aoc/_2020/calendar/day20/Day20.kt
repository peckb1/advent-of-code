package me.peckb.aoc._2020.calendar.day20

import me.peckb.aoc._2020.calendar.day20.Day20.Edge.*
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.text.RegexOption.DOT_MATCHES_ALL

class Day20 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  enum class Edge {
    NORTH, SOUTH, EAST, WEST
  }

  class Tile(val id: Int, var data: List<String>) {
    var edgeValues: Map<Edge, List<String>> = createEdgeValues(data)

    private fun createEdgeValues(data: List<String>): MutableMap<Edge, List<String>> {
      val result = mutableMapOf<Edge, List<String>>()

      result[NORTH] = listOf(data[0], data[0].reversed())
      result[SOUTH] = listOf(data[9], data[9].reversed())

      result[WEST] = data.map { it[0] }.let {
        val key = it.joinToString("")
        listOf(key, key.reversed())
      }

      result[EAST] = data.map { it[9] }.let {
        val key = it.joinToString("")
        listOf(key, key.reversed())
      }

      return result
    }
    
    fun rotateClockwise() = reset {
      data.indices.map { xIndex ->
        ((data.size - 1) downTo 0).joinToString("") { yIndex ->
          data[yIndex][xIndex].toString()
        }
      }
    }
    
    fun rotateCounterClockwise() = reset {
      ((data.size - 1) downTo 0).map { xIndex ->
        data.indices.joinToString("") { yIndex ->
          data[yIndex][xIndex].toString()
        }
      }
    }
    
    fun rotate180() = reset { data.map { it.reversed() }.reversed() }
    
    fun flipHorizontal() = reset { data.map { it.reversed() } }
    
    fun flipVertical() = reset { data.reversed() }

    override fun toString(): String {
      return "$id\n${data.joinToString("\n")}"
    }

    private fun reset(dataReset: () -> List<String>) = apply {
      data = dataReset()
      edgeValues = createEdgeValues(data)
    }

    fun trim() {
      data = data.drop(1).dropLast(1)
        .map { it.drop(1).dropLast(1) }
    }
  }

  data class Puzzle(
    val tiles: Map<Int, Tile>,
    val edgeMatches: MutableMap<String, MutableList<Tile>>,
    val edges: Map<Int, List<Tile>>,
    val corners: Map<Int, List<Tile>>,
  )

  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val puzzle = setup(input)

    puzzle.corners.keys.fold(1L) { acc, id -> acc * id }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val puzzle = setup(input)

    // assemble the puzzle!
    val puzzleIdsAssembled = mutableSetOf<Int>()
    val assembledPuzzle = mutableListOf<MutableList<Tile>>()
    val growthDirections = mutableListOf<Edge>()

    var firstPieceOfRow = puzzle.tiles[puzzle.corners.keys.first()]!!
    while(puzzleIdsAssembled.size != puzzle.tiles.size) {
      // pick a random corner which will become our "top left" corner
      val currentRow = mutableListOf<Tile>().apply {
        assembledPuzzle.add(this)
      }
      var currentTile = puzzle.tiles[firstPieceOfRow.id]!!.also {
        puzzleIdsAssembled.add(it.id)
        currentRow.add(it)
      }

      // we're going to the east as we build
      // TODO: we may need to guarantee that the first corner has an east, and if not flip it around :D
      var keys = currentTile.edgeValues[EAST]!!
      // find the tile that touches that side
      var nextTile = puzzle.edgeMatches[keys.first()]?.firstOrNull { t -> t.id != currentTile.id }

      while(nextTile != null) {
        // find out how we need to orient it
        // `keys.first()` is the top to bottom orientation of the `EAST` edge from above
        val (nextTileTouchingSide, touchingKeys) = nextTile.edgeValues.entries.first { it.value.contains(keys.first()) }
        val whichDirection = touchingKeys.indexOf(keys.first())

        when (nextTileTouchingSide) {
          NORTH -> {
            if (whichDirection == 0) nextTile.rotateCounterClockwise().flipVertical()
            else nextTile.rotateCounterClockwise()
          }

          SOUTH -> {
            if (whichDirection == 0) nextTile.rotateClockwise()
            else nextTile.rotateClockwise().flipVertical()
          }

          EAST -> {
            if (whichDirection == 0) nextTile.flipHorizontal()
            else nextTile.rotate180()
          }

          WEST -> {
            if (whichDirection == 0) { /* "do nothing" */ }
            else nextTile.flipVertical()
          }
        }

        puzzleIdsAssembled.add(nextTile.id)
        currentRow.add(nextTile)
        currentTile = nextTile

        keys = currentTile.edgeValues[EAST]!!
        // find the tile that touches that side
        nextTile = puzzle.edgeMatches[keys.first()]?.firstOrNull { t -> t.id != currentTile.id }
      }

      // the next row is then going to have the piece that is to the south of the current firstPieceOfRow
      val edge = listOf(NORTH, SOUTH).first {
        puzzle.edgeMatches[firstPieceOfRow.edgeValues[it]!!.first()]
          ?.firstOrNull { t -> t.id != firstPieceOfRow.id } != null
      }

      val nextFirstPieceOfRow = puzzle.edgeMatches[firstPieceOfRow.edgeValues[edge]!!.first()]
        ?.firstOrNull { t -> t.id != firstPieceOfRow.id }

      // but it may still need to be rotated
      nextFirstPieceOfRow?.let { newTile ->
        val (nextTileTouchingSide, touchingKeys) = newTile.edgeValues.entries.first { (_, otherKeys) ->
          otherKeys.contains(firstPieceOfRow.edgeValues[edge]!!.first())
        }
        val whichDirection = touchingKeys.indexOf(firstPieceOfRow.edgeValues[edge]!!.first())

        growthDirections.add(edge)

        when (edge) {
          SOUTH -> {
            when (nextTileTouchingSide) {
              NORTH -> {
                if (whichDirection == 0) { /* do nothing */ }
                else newTile.flipHorizontal()
              }
              SOUTH -> {
                if (whichDirection == 0) newTile.flipVertical()
                else newTile.rotate180()
              }
              EAST -> {
                if (whichDirection == 0) newTile.rotateCounterClockwise()
                else newTile.rotateCounterClockwise().flipHorizontal()
              }
              WEST -> {
                if (whichDirection == 0) newTile.rotateClockwise().flipHorizontal()
                newTile.rotateClockwise()
              }
            }
          }
          NORTH -> {
            when (nextTileTouchingSide) {
              NORTH -> {
                if (whichDirection == 0) newTile.flipVertical()
                else newTile.rotate180()
              }
              SOUTH -> {
                if (whichDirection == 0) { /* do nothing */ }
                else newTile.flipHorizontal()
              }
              EAST -> {
                if (whichDirection == 0) newTile.rotateCounterClockwise().flipVertical()
                else newTile.rotateClockwise()
              }
              WEST -> {
                if (whichDirection == 0) newTile.rotateCounterClockwise()
                else newTile.rotateCounterClockwise().flipHorizontal()
              }
            }
          }
          else -> throw IllegalStateException("New rows must have a north south match if they were oriented correctly")
        }
        firstPieceOfRow = newTile
      }
    }

    val southDirections = growthDirections.count { it == SOUTH }
    val northDirections = growthDirections.count { it == NORTH }

    val finalPuzzle= if (southDirections > northDirections) {
      combineTiles(assembledPuzzle)
    } else if (northDirections > southDirections) {
      combineTiles(assembledPuzzle.reversed())
    } else {
      throw IllegalStateException("No Growth Direction had a majority")
    }

    val orientation1 = finalPuzzle//.also { it.forEach { println(it) }; println() }
    val monsterCount1 = countMonsters(orientation1)
    val orientation2 = rotate(orientation1).also { it.forEach { println(it) }; println() }
    val monsterCount2 = countMonsters(orientation2)
    val orientation3 = rotate(orientation2)//.also { it.forEach { println(it) }; println() }
    val monsterCount3 = countMonsters(orientation3)
    val orientation4 = rotate(orientation3)//.also { it.forEach { println(it) }; println() }
    val monsterCount4 = countMonsters(orientation4)
    val orientation5 = flip(finalPuzzle)//.also { it.forEach { println(it) }; println() }
    val monsterCount5 = countMonsters(orientation5)
    val orientation6 = rotate(orientation5)//.also { it.forEach { println(it) }; println() }
    val monsterCount6 = countMonsters(orientation6)
    val orientation7 = rotate(orientation6)//.also { it.forEach { println(it) }; println() }
    val monsterCount7 = countMonsters(orientation7)
    val orientation8 = rotate(orientation7)//.also { it.forEach { println(it) }; println() }
    val monsterCount8 = countMonsters(orientation8)

    val monsterCount = listOf(
      monsterCount1, monsterCount2, monsterCount3, monsterCount4,
      monsterCount5, monsterCount6, monsterCount7, monsterCount8
    ).maxOf { it }

    val totalOnPieces = countOn(finalPuzzle)

    totalOnPieces - (monsterCount * 15)
  }

  private fun flip(data: List<String>): List<String> {
    return data.reversed()
  }

  private fun rotate(data: List<String>): List<String> {
    return data.indices.map { xIndex ->
      ((data.size - 1) downTo 0).joinToString("") { yIndex ->
        data[yIndex][xIndex].toString()
      }
    }
  }

  private fun countMonsters(data: List<String>): Int {
    return (1 until data.lastIndex).sumOf { index ->
      val row = data[index]

      val matchResults = CENTER_SEA_MONSTER_CENTER.findAll(row).toList()
      matchResults.sumOf { matchResult ->
        matchResult.groups.count { matchGroup ->
          if (matchGroup != null) {
            val previousRow = data[index - 1]
            val nextRow = data[index + 1]

            val startIndex = matchGroup.range.first
            val endIndex = matchGroup.range.last

            val upperSection = previousRow[endIndex - 1]
            val lowerSection = nextRow.substring(startIndex + 1, endIndex - 2)

            if (CENTER_SEA_MONSTER_LOWER.matchEntire(lowerSection) != null && upperSection == '#') {
              println("$row ($index): $startIndex - $endIndex")
              true
            } else {
              false
            }
          } else {
            false
          }
        }
      }
    }
  }

  private fun countOn(data: List<String>): Int {
    return data.sumOf { row ->
      row.count { it == '#' }
    }
  }

  private fun printPuzzle(assembledPuzzle: List<List<Tile>>) {
    println("Current Puzzle: ")
    assembledPuzzle.forEach { tileRow ->
      (0..9).forEach { rowIndexForTiles ->
        tileRow.forEach { tile ->
          print(tile.data[rowIndexForTiles])
          print(" ")
        }
        println()
      }
      println()
    }
  }

  private fun combineTiles(assembledPuzzle: List<MutableList<Tile>>): List<String> {
    printPuzzle(assembledPuzzle)

    assembledPuzzle.forEach { tileRow ->
      tileRow.forEach { tile ->
        tile.trim()
      }
    }

    val combinedTiles = mutableListOf<String>()

    assembledPuzzle.forEach { tileRow ->
      (0..7).forEach { rowIndexForTiles ->
        val sb = StringBuilder()
        tileRow.forEach { tile ->
          sb.append(tile.data[rowIndexForTiles])
        }
        combinedTiles.add(sb.toString())
      }
    }

    return combinedTiles
  }

  private fun toTile(data: List<String>): Tile {
    val tileId = data.first().split(" ").last().dropLast(1).toInt()
    val tileData = data.subList(1, 11)

    return Tile(tileId, tileData)
  }

  private fun setup(input: Sequence<String>): Puzzle {
    val tiles = input.chunked(12).map { toTile(it) }
      .associateBy { it.id }

    val edgeMatches = mutableMapOf<String, MutableList<Tile>>()

    tiles.values.forEach { tile ->
      tile.edgeValues.forEach { (_, edgeData) ->
        edgeData.forEach { edgeInformation ->
          edgeMatches.merge(edgeInformation, mutableListOf(tile)) { existingMatches, _ ->
            existingMatches.apply { add(tile) }
          }
        }
      }
    }

    val edges = edgeMatches.filter { (_, tiles) -> tiles.size == 1 }
      .flatMap { it.value }
      .groupBy { it.id }

    val corners = edges.filter { it.value.size == 4 }

    return Puzzle(tiles, edgeMatches, edges, corners)
  }

  companion object {
    val CENTER_SEA_MONSTER_CENTER = "#.{4}##.{4}##.{4}###".toRegex(DOT_MATCHES_ALL)
    val CENTER_SEA_MONSTER_LOWER = "#.{2}#.{2}#.{2}#.{2}#.{2}#".toRegex(DOT_MATCHES_ALL)
  }
}
