package me.peckb.aoc._2015.calendar.day22

import me.peckb.aoc._2015.calendar.day22.Game.Companion.DRAIN_BONUS
import me.peckb.aoc._2015.calendar.day22.Game.Companion.DRAIN_COST
import me.peckb.aoc._2015.calendar.day22.Game.Companion.DRAIN_DAMAGE
import me.peckb.aoc._2015.calendar.day22.Game.Companion.MISSILE_COST
import me.peckb.aoc._2015.calendar.day22.Game.Companion.MISSILE_DAMAGE
import me.peckb.aoc._2015.calendar.day22.Game.Companion.POISON_COST
import me.peckb.aoc._2015.calendar.day22.Game.Companion.POISON_LENGTH
import me.peckb.aoc._2015.calendar.day22.Game.Companion.RECHARGE_GAIN
import me.peckb.aoc._2015.calendar.day22.Game.Companion.SHIELD_BONUS
import me.peckb.aoc._2015.calendar.day22.Game.Companion.SHIELD_COST
import me.peckb.aoc._2015.calendar.day22.Game.Companion.SHIELD_LENGTH
import me.peckb.aoc._2015.calendar.day22.Game.Mode
import me.peckb.aoc._2015.calendar.day22.Game.Mode.EASY
import kotlin.math.max

data class Hero(val hitPoints: Int, val mana: Int, val armour: Int, val shieldTurns: Int, val rechargeTurns: Int) {
  fun advanceTurn(mode: Mode): Hero {
    val turnDamage = if (mode == EASY) 0 else 1
    val armour = if (shieldTurns > 0) SHIELD_BONUS else 0
    val manaRecharging = if (rechargeTurns > 0) RECHARGE_GAIN else 0
    return Hero(
      hitPoints - turnDamage,
      mana + manaRecharging,
      armour,
      max(0, shieldTurns - 1),
      max(0, rechargeTurns - 1)
    )
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
    return Hero(hitPoints, mana - Game.RECHARGE_COST, armour, shieldTurns, Game.RECHARGE_LENGTH)
  }
}
