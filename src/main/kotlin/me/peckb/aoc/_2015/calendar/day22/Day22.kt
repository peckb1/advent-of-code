package me.peckb.aoc._2015.calendar.day22

import me.peckb.aoc._2015.calendar.day22.Game.Mode.EASY
import me.peckb.aoc._2015.calendar.day22.Game.Mode.HARD
import me.peckb.aoc._2015.calendar.day22.Game.Turn.HERO
import me.peckb.aoc._2015.calendar.day22.Game.Turn.WIN
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import javax.inject.Inject

class Day22 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val boss = loadInput(input.toList())
    val hero = Hero(50, 500, 0, 0, 0)
    val game = Game(hero, boss, HERO, EASY)

    val dijkstra = GameDijkstra()
    val solution = dijkstra.solve(game)
      .filter { it.key.turn == WIN }
      .minByOrNull { it.value }

    solution?.value
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val boss = loadInput(input.toList())
    val hero = Hero(50, 500, 0, 0, 0)
    val game = Game(hero, boss, HERO, HARD)

    val dijkstra = GameDijkstra()
    val solution = dijkstra.solve(game)
      .filter { it.key.turn == WIN }
      .minByOrNull { it.value }

    solution?.value
  }

  private fun loadInput(input: List<String>): Boss {
    val hp = input.first().substringAfter(": ").toInt()
    val damage = input.last().substringAfter(": ").toInt()

    return Boss(hp, damage, 0)
  }
}
