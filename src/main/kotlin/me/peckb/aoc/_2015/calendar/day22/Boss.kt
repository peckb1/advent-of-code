package me.peckb.aoc._2015.calendar.day22

import me.peckb.aoc._2015.calendar.day22.Game.Companion.POISON_DAMAGE
import kotlin.math.max

data class Boss(val hitPoints: Int, val damage: Int, val poisonTurns: Int) {
  fun advanceTurn(): Boss {
    val poisonDamage = if (poisonTurns > 0) POISON_DAMAGE else 0
    return Boss(hitPoints - poisonDamage, damage, max(0, poisonTurns - 1))
  }
}
