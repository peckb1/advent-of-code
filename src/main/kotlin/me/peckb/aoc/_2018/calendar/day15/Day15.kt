package me.peckb.aoc._2018.calendar.day15

import me.peckb.aoc._2018.calendar.day15.Space.Player.Elf
import me.peckb.aoc._2018.calendar.day15.Space.Player.Goblin
import me.peckb.aoc._2018.calendar.day15.Space.Empty
import me.peckb.aoc._2018.calendar.day15.Space.Player
import me.peckb.aoc._2018.calendar.day15.Space.Wall
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day15 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val gameMap = input.mapIndexed { y, row ->
      row.mapIndexed { x, spaceChar ->
        when (spaceChar) {
          '#' -> Wall(x, y)
          '.' -> Empty(x, y)
          'E' -> Elf(x, y)
          'G' -> Goblin(x, y)
          else -> throw IllegalStateException("Invalid space $spaceChar")
        }
      }.toMutableList()
    }

    runGame(gameMap.toList())
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val inputList = input.toMutableList()
    var someElvesDied = true
    var result = 0
    var power = 4

    while(someElvesDied) {
      val originalElves = mutableListOf<Elf>()
      val gameMap = inputList.mapIndexed { y, row ->
        row.mapIndexed { x, spaceChar ->
          when (spaceChar) {
            '#' -> Wall(x, y)
            '.' -> Empty(x, y)
            'E' -> Elf(x, y, power).also { originalElves.add(it) }
            'G' -> Goblin(x, y)
            else -> throw IllegalStateException("Invalid space $spaceChar")
          }
        }.toMutableList()
      }

      result = runGame(gameMap.toList())
      val afterElfCount = gameMap.findPlayers().filterIsInstance<Elf>().size

      someElvesDied = afterElfCount != originalElves.size
      if (someElvesDied) power++
    }

    result
  }

  private fun runGame(gameMap: List<MutableList<Space>>): Int {
    var oneTeamLeft = false
    var turns = 0
    while(!oneTeamLeft) {
      // grab the players in "reading order"
      val players = gameMap.findPlayers()
      val (goblins, elves) = players.partition { it is Goblin }

      players.forEach playerLoop@ { player ->
        if (oneTeamLeft) return@playerLoop
        if (player.hitPoints < 0) return@playerLoop

        oneTeamLeft = when (player) {
          is Goblin -> player.takeTurn(gameMap, elves) { it.filterIsInstance<Elf>().sortedWith(playerComparator).firstOrNull() }
          is Elf -> player.takeTurn(gameMap, goblins) { it.filterIsInstance<Goblin>().sortedWith(playerComparator).firstOrNull() }
        }
      }

      turns ++
    }

    val remainingHP = gameMap.findPlayers().sumOf { it.hitPoints }
    val completedTurns = turns - 1
    return remainingHP * completedTurns
  }

  private fun List<List<Space>>.findPlayers(): List<Player> {
    val map = this
    return buildList {
      map.forEach { row ->
        row.forEach { space ->
          if (space is Player) add(space)
        }
      }
    }
  }

  companion object {
    const val DEFAULT_POWER = 3

    val playerComparator = Comparator<Player> { p1, p2 ->
      when (val hitPointComp = p1.hitPoints.compareTo(p2.hitPoints)) {
        0 -> {
          when (val yComp = p1.y.compareTo(p2.y)) {
            0 -> p1.x.compareTo(p2.x)
            else -> yComp
          }
        }
        else -> hitPointComp
      }
    }

    val pathComparator = Comparator<Path> { p1, p2 ->
      val p1Step = p1.steps.last()
      val p2Step = p2.steps.last()
      when (val yComparison = p1Step.y.compareTo(p2Step.y)) {
        0 -> p1Step.x.compareTo(p2Step.x)
        else -> yComparison
      }
    }
  }
}