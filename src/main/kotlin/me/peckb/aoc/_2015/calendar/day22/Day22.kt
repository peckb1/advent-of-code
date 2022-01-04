package me.peckb.aoc._2015.calendar.day22

import me.peckb.aoc._2021.calendar.day23.Dijkstra
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.max

class Day22 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val boss = loadInput(input.toList())
    val hero = Hero(50, 500)

    fun tick() {
      println()
      hero.advanceTurn()
      boss.advanceTurn()
      println("$hero  $boss")
    }

    fun P() {
      tick()
      hero.castPoison(boss)
      println("$hero  $boss")
    }

    fun D() {
      tick()
      boss.attack(hero)
      println("$hero  $boss")
    }

    fun R() {
      tick()
      hero.castRecharge()
      println("$hero  $boss")
    }

    fun S() {
      tick()
      hero.castShield()
      println("$hero  $boss")
    }

    fun DR() {
      tick()
      hero.castDrain(boss)
      println("$hero  $boss")
    }

    fun MM() {
      tick()
      hero.castMagicMissile(boss)
      println("$hero  $boss")
    }

    // 1485 -- too high
    // P(); D()
    // R(); D()
    // S(); D()
    // P(); D()
    // R(); D()
    // S(); D()
    // P(); D()
    // R(); D()
    // MM(); D()
    // tick()

    // 1461 too high
    // P(); D()
    // R(); D()
    // S(); D()
    // P(); D()
    // R(); D()
    // S(); D()
    // MM(); D()
    // MM(); D()
    // S(); D()
    // MM(); D()
    // MM(); D()
    // MM(); D()
    // MM()

    // 1309 too high
    // P(); D()
    // R(); D()
    // S(); D()
    // P(); D()
    // R(); D()
    // S(); D()
    // P(); D()
    // MM(); D()
    // MM()
    // tick()

    P(); D()
    R(); D()
    MM(); D()
    P(); D()
    R(); D()
    S(); D()
    P(); D()
    DR(); D()
    MM(); tick()

    hero.totalManaSpent
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    -1
  }

  private fun loadInput(input: List<String>): Boss {
    val hp = input.first().substringAfter(": ").toInt()
    val damage = input.last().substringAfter(": ").toInt()

    return Boss(hp, damage)
  }

  abstract class Player(val baseHP: Int) {
    var damageTaken = 0

    fun takeDamage(damage: Int) {
      damageTaken += damage
    }

    open fun resetStats() { damageTaken = 0 }

    abstract fun advanceTurn()

    fun remainingHP() = baseHP - damageTaken
  }

  class Hero(hp: Int, var mana: Int) : Player(hp) {
    var rechargeTurnsRemaining = 0
    var shieldTurnsRemaining = 0
    var totalManaSpent = 0
    var armour = 0

    override fun resetStats() {
      super.resetStats()
      shieldTurnsRemaining = 0
      rechargeTurnsRemaining = 0
      totalManaSpent = 0
    }

    override fun advanceTurn() {
      if (shieldTurnsRemaining > 0) {
        shieldTurnsRemaining--
        if (shieldTurnsRemaining == 0) {
          armour -= SHIELD_BONUS
        }
      }
      if (rechargeTurnsRemaining > 0) {
        rechargeTurnsRemaining--
        mana += RECHARGE_GAIN
      }
    }

    fun castRecharge() {
      rechargeTurnsRemaining = RECHARGE_LENGTH
      mana -= RECHARGE_COST
      totalManaSpent += RECHARGE_COST
    }

    fun castShield() {
      shieldTurnsRemaining = SHIELD_LENGTH
      armour += SHIELD_BONUS
      mana -= SHIELD_COST
      totalManaSpent += SHIELD_COST
    }

    // 173 / (6 * 3) = 9.611 mana per damage
    fun castPoison(boss: Boss) {
      mana -= POISON_COST
      totalManaSpent += POISON_COST
      boss.applyPoison()
    }

    fun castDrain(boss: Boss) {
      mana -= DRAIN_COST
      damageTaken -= DRAIN_BONUS
      totalManaSpent += DRAIN_COST
      boss.takeDamage(DRAIN_DAMAGE)
    }

    fun castMagicMissile(boss: Boss) {
      mana -= MISSILE_COST
      totalManaSpent += MISSILE_COST
      boss.takeDamage(MISSILE_DAMAGE)
    }

    override fun toString(): String {
      return "Hero($baseHP) ${remainingHP()}, ($shieldTurnsRemaining)$armour, ($rechargeTurnsRemaining)$mana"
    }
  }

  class Boss(hp: Int, val damage: Int) : Player(hp) {
    var poisonTurnsRemaining = 0

    override fun advanceTurn() {
      if (poisonTurnsRemaining > 0) {
        poisonTurnsRemaining--
        takeDamage(POISON_DAMAGE)
      }
    }

    fun applyPoison() {
      poisonTurnsRemaining = POISON_LENGTH
    }

    fun attack(hero: Hero) {
      hero.takeDamage(max(damage - hero.armour, 1))
    }

    override fun toString(): String {
      return "Boss($baseHP): ${remainingHP()} P($poisonTurnsRemaining)"
    }
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
