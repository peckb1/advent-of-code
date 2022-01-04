package me.peckb.aoc._2015.calendar.day22

import me.peckb.aoc._2015.calendar.day22.Game.Turn.BOSS
import me.peckb.aoc._2015.calendar.day22.Game.Turn.HERO
import me.peckb.aoc._2015.calendar.day22.Game.Turn.LOSS
import me.peckb.aoc._2015.calendar.day22.Game.Turn.WIN

data class Game(val hero: Hero, val boss: Boss, val turn: Turn, val mode: Mode) {
  enum class Mode {
    EASY,
    HARD
  }

  enum class Turn {
    HERO,
    BOSS,
    WIN,
    LOSS
  }

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

    return when {
      heroAfterEvents.hitPoints <= 0 -> listOf(Game(hero, boss, LOSS, mode) to 0)
      bossAfterEvents.hitPoints <= 0 -> listOf(Game(hero, boss, WIN, mode) to 0)
      else -> {
        mutableListOf<Pair<Game, Int>>().apply {
          // cast Magic Missile?
          if (heroAfterEvents.mana >= MISSILE_COST) {
            val (newHero, newBoss) = heroAfterEvents.castMagicMissile(bossAfterEvents)
            val turn = if (newBoss.hitPoints <= 0) WIN else BOSS
            val game = Game(newHero, newBoss, turn, mode)
            add(game to MISSILE_COST)
          }

          // cast Drain?
          if (heroAfterEvents.mana >= DRAIN_COST) {
            val (newHero, newBoss) = heroAfterEvents.castDrain(bossAfterEvents)
            val turn = if (newBoss.hitPoints <= 0) WIN else BOSS
            val game = Game(newHero, newBoss, turn, mode)
            add(game to DRAIN_COST)
          }

          // cast Shield?
          if (heroAfterEvents.mana >= SHIELD_COST && heroAfterEvents.shieldTurns == 0) {
            val newHero = heroAfterEvents.castShield()
            val game = Game(newHero, bossAfterEvents, BOSS, mode)
            add(game to SHIELD_COST)
          }

          // cast Poison?
          if (heroAfterEvents.mana >= POISON_COST && bossAfterEvents.poisonTurns == 0) {
            val (newHero, newBoss) = heroAfterEvents.castPoison(bossAfterEvents)
            val game = Game(newHero, newBoss, BOSS, mode)
            add(game to POISON_COST)
          }

          // cast Recharge?
          if (heroAfterEvents.mana >= RECHARGE_COST && heroAfterEvents.rechargeTurns == 0) {
            val newHero = heroAfterEvents.castRecharge()
            val game = Game(newHero, bossAfterEvents, BOSS, mode)
            add(game to RECHARGE_COST)
          }
        }
      }
    }
  }

  private fun bossMoves(): List<Pair<Game, Int>> {
    val bossAfterEvents: Boss = boss.advanceTurn()
    val heroAfterEvents: Hero = hero.advanceTurn(mode)

    return if (bossAfterEvents.hitPoints <= 0) {
      listOf(Game(heroAfterEvents, bossAfterEvents, WIN, mode) to 0)
    } else {
      val damagedHero = heroAfterEvents.takeHit(bossAfterEvents.damage)
      val turn = if (damagedHero.hitPoints <= 0) LOSS else HERO
      val game = Game(damagedHero, bossAfterEvents, turn, mode)
      listOf(game to 0)
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
