package me.peckb.aoc._2015.calendar.day22

import me.peckb.aoc._2015.calendar.day22.Day22.Mode.EASY
import me.peckb.aoc._2015.calendar.day22.Day22.Mode.HARD
import me.peckb.aoc._2015.calendar.day22.Day22.Move.DR
import me.peckb.aoc._2015.calendar.day22.Day22.Move.MM
import me.peckb.aoc._2015.calendar.day22.Day22.Move.PO
import me.peckb.aoc._2015.calendar.day22.Day22.Move.RE
import me.peckb.aoc._2015.calendar.day22.Day22.Move.SH
import me.peckb.aoc._2015.calendar.day22.Day22.Turn.BOSS
import me.peckb.aoc._2015.calendar.day22.Day22.Turn.HERO
import me.peckb.aoc._2015.calendar.day22.Day22.Turn.LOSS
import me.peckb.aoc._2015.calendar.day22.Day22.Turn.WIN
import me.peckb.aoc._2021.calendar.day23.Dijkstra
import me.peckb.aoc._2021.calendar.day23.DijkstraNodeWithCost
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max

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

  enum class Mode {
    EASY, HARD
  }

  data class Boss(val hitPoints: Int, val damage: Int, val poisonTurns: Int) {
    fun advanceTurn(): Boss {
      val poisonDamage = if (poisonTurns > 0) POISON_DAMAGE else 0
      return Boss(hitPoints - poisonDamage, damage, max(0, poisonTurns - 1))
    }
  }

  data class Hero(val hitPoints: Int, val mana: Int, val armour: Int, val shieldTurns: Int, val rechargeTurns: Int) {
    fun advanceTurn(mode: Mode): Hero {
      val turnDamage = if (mode == EASY) 0 else 1
      val armour = if (shieldTurns > 0) SHIELD_BONUS else 0
      val manaRecharging = if (rechargeTurns > 0) RECHARGE_GAIN else 0
      return Hero(hitPoints - turnDamage, mana + manaRecharging, armour, max(0, shieldTurns - 1), max(0, rechargeTurns - 1))
    }

    fun takeHit(damage: Int): Hero {
      return Hero(hitPoints - max(1, damage - armour), mana, armour, shieldTurns, rechargeTurns)
    }

    fun castMagicMissile(bossAfterEvents: Boss): Pair<Hero, Boss> {
      val newHero = Hero(hitPoints, mana - MISSILE_COST, armour, shieldTurns, rechargeTurns)
      val newBoss = Boss(bossAfterEvents.hitPoints - MISSILE_DAMAGE, bossAfterEvents.damage, bossAfterEvents.poisonTurns)
      return newHero to newBoss
    }

    fun castDrain(bossAfterEvents: Boss): Pair<Hero, Boss> {
      val newHero = Hero(hitPoints + DRAIN_BONUS, mana - DRAIN_COST, armour, shieldTurns, rechargeTurns)
      val newBoss = Boss(bossAfterEvents.hitPoints - DRAIN_DAMAGE, bossAfterEvents.damage, bossAfterEvents.poisonTurns)
      return newHero to newBoss
    }

    fun castShield(): Hero {
      return Hero(hitPoints, mana - SHIELD_COST, SHIELD_BONUS, SHIELD_LENGTH, rechargeTurns)
    }

    fun castPoison(bossAfterEvents: Boss): Pair<Hero, Boss> {
      val newHero = Hero(hitPoints, mana - POISON_COST, armour, shieldTurns, rechargeTurns)
      val newBoss = Boss(bossAfterEvents.hitPoints, bossAfterEvents.damage, POISON_LENGTH)
      return newHero to newBoss
    }

    fun castRecharge(): Hero {
      return Hero(hitPoints, mana - RECHARGE_COST, armour, shieldTurns, RECHARGE_LENGTH)
    }
  }

  enum class Turn { HERO, BOSS, WIN, LOSS }

  enum class Move {
    MM, DR, SH, PO, RE
  }

  data class Game(val hero: Hero, val boss: Boss, val turn: Turn, val mode: Mode) {
    private var movesMade: List<Move> = listOf()

    fun neighbors(): List<Pair<Game, Int>> {
      return when (turn) {
        HERO -> playerMoves()
        BOSS -> bossMoves()
        WIN -> listOf()
        LOSS -> listOf()
      }
    }

    private fun playerMoves(): List<Pair<Game, Int>> {
      val bossAfterEvents: Boss = boss.advanceTurn()
      val heroAfterEvents: Hero = hero.advanceTurn(mode)

      return if (heroAfterEvents.hitPoints <= 0) {
        listOf(Game(hero, boss, LOSS, mode) to 0)
      } else if (bossAfterEvents.hitPoints <= 0) {
        listOf(Game(hero, boss, WIN, mode) to 0)
      } else {
        val moves = mutableListOf<Pair<Game, Int>>()

        // cast Magic Missile?
        if (heroAfterEvents.mana >= MISSILE_COST) {
          val (newHero, newBoss) = heroAfterEvents.castMagicMissile(bossAfterEvents)
          val turn = if (newBoss.hitPoints <= 0) WIN else BOSS
          val game = Game(newHero, newBoss, turn, mode).withNewMove(movesMade, MM)
          moves.add(game to MISSILE_COST)
        }

        // cast Drain?
        if (heroAfterEvents.mana >= DRAIN_COST) {
          val (newHero, newBoss) = heroAfterEvents.castDrain(bossAfterEvents)
          val turn = if (newBoss.hitPoints <= 0) WIN else BOSS
          val game = Game(newHero, newBoss, turn, mode).withNewMove(movesMade, DR)
          moves.add(game to DRAIN_COST)
        }

        // cast Shield?
        if (heroAfterEvents.mana >= SHIELD_COST && heroAfterEvents.shieldTurns == 0) {
          val newHero = heroAfterEvents.castShield()
          val game = Game(newHero, bossAfterEvents, BOSS, mode).withNewMove(movesMade, SH)
          moves.add(game to SHIELD_COST)
        }

        // cast Poison?
        if (heroAfterEvents.mana >= POISON_COST && bossAfterEvents.poisonTurns == 0) {
          val (newHero, newBoss) = heroAfterEvents.castPoison(bossAfterEvents)
          val game = Game(newHero, newBoss, BOSS, mode).withNewMove(movesMade, PO)
          moves.add(game to POISON_COST)
        }

        // cast Recharge?
        if (heroAfterEvents.mana >= RECHARGE_COST && heroAfterEvents.rechargeTurns == 0) {
          val newHero = heroAfterEvents.castRecharge()
          val game = Game(newHero, bossAfterEvents, BOSS, mode).withNewMove(movesMade, RE)
          moves.add(game to RECHARGE_COST)
        }

        moves
      }
    }

    private fun withNewMove(movesMade: List<Move>, move: Move) = apply { this.movesMade = movesMade.plus(move) }

    private fun bossMoves(): List<Pair<Game, Int>> {
      val bossAfterEvents: Boss = boss.advanceTurn()
      val heroAfterEvents: Hero = hero.advanceTurn(mode)

      return if (bossAfterEvents.hitPoints <= 0) {
        listOf(Game(heroAfterEvents, bossAfterEvents, WIN, mode) to 0)
      } else {
        val damagedHero = heroAfterEvents.takeHit(bossAfterEvents.damage)
        val turn = if (damagedHero.hitPoints <= 0) LOSS else HERO
        val game = Game(damagedHero, bossAfterEvents, turn, mode).also { it.movesMade = movesMade }
        listOf(game to 0)
      }
    }
  }

  class GameWithCost(private val game: Game, val cost: Int) : DijkstraNodeWithCost<Game, Int> {
    override fun node() = game
    override fun cost() = cost
    override fun neighbors() = game.neighbors().map { GameWithCost(it.first, it.second) }
    override fun compareTo(other: DijkstraNodeWithCost<Game, Int>) = cost.compareTo(other.cost())
  }

  @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
  class GameDijkstra : Dijkstra<Game, Int, GameWithCost> {
    override fun Game.withCost(cost: Int) = GameWithCost(this, cost)
    override fun Int.plus(cost: Int) = this + cost
    override fun maxCost() = Int.MAX_VALUE
    override fun minCost() = 0
  }

  companion object {
    const val POISON_LENGTH = 6
    const val POISON_COST = 173
    const val POISON_DAMAGE = 3

    const val RECHARGE_LENGTH = 5
    const val RECHARGE_COST = 229
    const val RECHARGE_GAIN = 101

    const val SHIELD_LENGTH = 6
    const val SHIELD_COST = 113
    const val SHIELD_BONUS = 7

    const val MISSILE_COST = 53
    const val MISSILE_DAMAGE = 4

    const val DRAIN_COST = 73
    const val DRAIN_DAMAGE = 2
    const val DRAIN_BONUS = 2
  }
}
