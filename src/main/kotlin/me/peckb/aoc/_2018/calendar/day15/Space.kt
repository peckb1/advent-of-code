package me.peckb.aoc._2018.calendar.day15

import me.peckb.aoc._2018.calendar.day15.Day15.Companion.DEFAULT_POWER

sealed class Space(var x: Int, var y: Int) {
  class Wall(_x: Int, _y: Int) : Space(_x, _y)

  class Empty(_x: Int, _y: Int): Space(_x, _y)

  sealed class Player(_x: Int, _y: Int, var attackPower: Int = DEFAULT_POWER, var hitPoints: Int = 200): Space(_x, _y) {
    class Elf(_x: Int, _y: Int, _attackPower: Int = DEFAULT_POWER) : Player(_x, _y, _attackPower)
    class Goblin(_x: Int, _y: Int) : Player(_x, _y)

    fun takeTurn(gameMap: List<MutableList<Space>>, enemies: List<Player>, findEnemy: (List<Space>) -> Player?) : Boolean {
      val remainingEnemies = enemies.filter { it.hitPoints > 0 }
      if (remainingEnemies.isEmpty()) return true

      var adjacentEnemy = maybeFindEnemy(gameMap, findEnemy)
      if (adjacentEnemy == null) move(gameMap, remainingEnemies)
      adjacentEnemy = maybeFindEnemy(gameMap, findEnemy)

      adjacentEnemy?.let {
        it.hitPoints -= attackPower
        if (it.hitPoints <= 0) gameMap[it.y][it.x] = Empty(it.x, it.y)
      }

      return false
    }

    private fun maybeFindEnemy(gameMap: List<MutableList<Space>>, findEnemy: (List<Space>) -> Player?): Player? {
      val u = gameMap[y - 1][x]
      val l = gameMap[y][x - 1]
      val r = gameMap[y][x + 1]
      val d = gameMap[y + 1][x]

      return findEnemy(listOf(u, l, r, d))
    }

    private fun move(gameMap: List<MutableList<Space>>, remainingEnemies: List<Player>) {
      val paths = GameDijkstra(gameMap).solve(this)
      val closestPaths = remainingEnemies.flatMap { enemy ->
        val u = gameMap[enemy.y - 1][enemy.x]
        val l = gameMap[enemy.y][enemy.x - 1]
        val r = gameMap[enemy.y][enemy.x + 1]
        val d = gameMap[enemy.y + 1][enemy.x]

        listOf(u, l, r, d).filterIsInstance<Empty>().map { paths[it] }
      }.filterNotNull().sortedBy { it.cost }

      if (closestPaths.isNotEmpty()) {
        val options = closestPaths.takeWhile { it.cost == closestPaths.first().cost }
        val (newX, newY) = options.sortedWith(Day15.pathComparator).first().steps.first()

        gameMap[y][x] = Empty(x, y)
        gameMap[newY][newX] = this.also { x = newX; y = newY; }
      }
    }
  }
}
