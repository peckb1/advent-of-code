package me.peckb.aoc._2018.calendar.day15

sealed class Space(var x: Int, var y: Int) {
  class Wall(_x: Int, _y: Int) : Space(_x, _y)
  class Empty(_x: Int, _y: Int): Space(_x, _y)
  sealed class Player(_x: Int, _y: Int, var attackPower: Int = Day15.DEFAULT_POWER, var hitPoints: Int = 200): Space(_x, _y) {
    class Elf(_x: Int, _y: Int, _attackPower: Int = Day15.DEFAULT_POWER) : Player(_x, _y, _attackPower)
    class Goblin(_x: Int, _y: Int) : Player(_x, _y)

    fun takeTurn(gameMap: List<MutableList<Space>>, enemies: List<Player>, findEnemy: (List<Space>) -> Player?) : Boolean {
      val remainingEnemies = enemies.filter { it.hitPoints > 0 }
      if (remainingEnemies.isEmpty()) return true

      val myUp = gameMap[y - 1][x]
      val myLeft = gameMap[y][x - 1]
      val myRight = gameMap[y][x + 1]
      val myDown = gameMap[y + 1][x]
      var adjacentEnemy = findEnemy(listOf(myUp, myLeft, myRight, myDown))

      if (adjacentEnemy == null) {
        val paths = GameDijkstra(gameMap).solve(this)
        val closestPaths = remainingEnemies.flatMap enemyMap@ { enemy ->
          if (enemy.hitPoints <= 0) throw IllegalStateException("Why am I looking at an alive enemy?")

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

      adjacentEnemy = adjacentEnemy ?: run {
        val u = gameMap[y - 1][x]
        val l = gameMap[y][x - 1]
        val r = gameMap[y][x + 1]
        val d = gameMap[y + 1][x]
        findEnemy(listOf(u, l, r, d))
      }

      adjacentEnemy?.let {
        it.hitPoints -= attackPower
        if (it.hitPoints <= 0) gameMap[it.y][it.x] = Empty(it.x, it.y)
      }

      return false
    }
  }
}
