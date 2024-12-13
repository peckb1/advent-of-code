package me.peckb.aoc._2024.calendar.day13

import com.microsoft.z3.BitVecNum
import com.microsoft.z3.BitVecSort
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import com.microsoft.z3.Status
import me.peckb.aoc._2024.calendar.day13.ClawGameDijkstra.GameNode
import javax.inject.Inject
import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import me.peckb.aoc.pathing.GenericIntDijkstra

class Day13 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.chunked(4)
      .mapNotNull { (a, b, p, _) ->
        val aSpeed = parse(a.split("A: ").last()).let{ (x, y) -> Speed(x, y)    }
        val bSpeed = parse(b.split("B: ").last()).let{ (x, y) -> Speed(x, y)    }
        val prize = parse(p.split(": ").last()).let{ (x, y) -> GameNode(x, y) }

        val game = ClawGameDijkstra(aSpeed, bSpeed, prize)

        val solutions = game.solve(GameNode(0, 0).withGame(game), prize.withGame(game))

        solutions[prize]
      }
      .sum()
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    input.chunked(4)
      .mapNotNull { (a, b, p, _) ->
        val aSpeed = parse(a.split("A: ").last()).let{ (x, y) -> Speed(x, y)    }
        val bSpeed = parse(b.split("B: ").last()).let{ (x, y) -> Speed(x, y)    }
        val prize = parse(p.split(": ").last()).let{ (x, y) -> x + BUFFER to y + BUFFER }

        Context().use { ctx ->
          val solver = ctx.mkSolver()

          val longType = ctx.mkBitVecSort(64)
          fun variableOf(name: String) = ctx.mkConst(name, longType)
          fun valueOf(value: Long) = ctx.mkNumeral(value, longType) as BitVecNum
          operator fun Expr<BitVecSort>.times(t: Expr<BitVecSort>) = ctx.mkBVMul(this, t)
          operator fun Expr<BitVecSort>.plus(t: Expr<BitVecSort>) = ctx.mkBVAdd(this, t)
          infix fun Expr<BitVecSort>.equalTo(t: Expr<BitVecSort>) = ctx.mkEq(this, t)
          infix fun Expr<BitVecSort>.greaterThan(t: Expr<BitVecSort>) = ctx.mkBVSGT(this, t)
          infix fun Expr<BitVecSort>.lessThan(t: Expr<BitVecSort>) = ctx.mkBVSLT(this, t)

          val zero = valueOf(0)
          val max = valueOf(500_000_000_000)
          val aPress = variableOf("x")
          val bPress = variableOf("y")
          val aXSpeed = valueOf(aSpeed.x.toLong())
          val aYSpeed = valueOf(aSpeed.y.toLong())
          val bXSpeed = valueOf(bSpeed.x.toLong())
          val bYSpeed = valueOf(bSpeed.y.toLong())
          val prizeX = valueOf(prize.first)
          val prizeY = valueOf(prize.second)

          solver.add(aPress greaterThan zero)
          solver.add(bPress greaterThan zero)
          solver.add(aPress lessThan max)
          solver.add(bPress lessThan max)
          solver.add((aXSpeed * aPress) + (bXSpeed * bPress) equalTo prizeX)
          solver.add((aYSpeed * aPress) + (bYSpeed * bPress) equalTo prizeY)

          val status = solver.check()

          if (status == Status.SATISFIABLE) {
            val model = solver.model
            val aButtonCost = (model.evaluate(aPress, true) as BitVecNum).long * A_COST
            val bButtonCost = (model.evaluate(bPress, true) as BitVecNum).long * B_COST

            aButtonCost + bButtonCost
          } else {
            null
          }
        }
      }
      .sum()
  }

  private fun parse(data: String): Pair<Int, Int> {
    return data.split(", ").map{ it.drop(2).toInt() }.let { (x, y) -> x to y }
  }

  companion object {
    private const val BUFFER = 10000000000000
    private const val A_COST = 3
    private const val B_COST = 1
  }
}

class ClawGameDijkstra(val speedA: Speed, val speedB: Speed, val destination: GameNode) : GenericIntDijkstra<GameNode>() {

  data class GameNode(val x: Int, val y: Int) : DijkstraNode<GameNode> {
    lateinit var game: ClawGameDijkstra

    fun withGame(game: ClawGameDijkstra) = apply { this.game = game }

    override fun neighbors(): Map<GameNode, Int> {
      return mutableMapOf<GameNode, Int>().apply {
        if (x + game.speedA.x <= game.destination.x && y + game.speedA.y <= game.destination.y) {
          put(GameNode(x + game.speedA.x, y + game.speedA.y).withGame(game), A_COST)
        }
        if (x + game.speedB.x <= game.destination.x && y + game.speedB.y <= game.destination.y) {
          put(GameNode(x + game.speedB.x, y + game.speedB.y).withGame(game), B_COST)
        }
      }
    }
  }

  companion object {
    private const val A_COST = 3
    private const val B_COST = 1
  }
}

data class Speed(val x: Int, val y: Int)
