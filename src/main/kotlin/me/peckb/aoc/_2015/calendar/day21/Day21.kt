package me.peckb.aoc._2015.calendar.day21

import me.peckb.aoc._2015.calendar.day21.Day21.Player.Type.BOSS
import me.peckb.aoc._2015.calendar.day21.Day21.Player.Type.HERO
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.Int.Companion.MAX_VALUE
import kotlin.Int.Companion.MIN_VALUE
import kotlin.math.max
import kotlin.math.min

class Day21 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val boss = loadBoss(input)
    // HERO to win in the min cost possible
    findCost(boss, HERO, ::min, MAX_VALUE)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val boss = loadBoss(input)
    // BOSS to win in the max cost possible
    findCost(boss, BOSS, ::max, MIN_VALUE)
  }

  private fun findCost(boss: Player, expectedWinner: Player.Type, costComparator: (Int, Int) -> Int, worstCost: Int): Int {
    // must have one weapon
    val weapons = Weapon.values()
    // armours and rings are optional
    val armours = Array<Armour?>(Armour.values().size + 1) { null }
    val rings = Array<Ring?>(Ring.values().size + 1) { null }

    Weapon.values().reversed().forEachIndexed { i, w -> weapons[i] = w }
    Armour.values().reversed().forEachIndexed { i, a -> armours[i] = a }
    Ring.values().reversed().forEachIndexed { i, r -> rings[i] = r }

    var currentBestCost = worstCost
    weapons.forEach { weapon ->
      armours.forEach { armour ->
        rings.forEach { ringOne ->
          rings.forEach secondRing@ { ringTwo ->
            // you can't wear the same ring twice
            if (ringOne == ringTwo) return@secondRing

            val player = Player(PLAYER_HP, weapon, armour, ringOne, ringTwo)

            play(player, boss)

            when (expectedWinner) {
              HERO -> {
                if (boss.remainingHP() <= 0) {
                  currentBestCost = costComparator(player.equipmentCost(), currentBestCost)
                }
              }
              BOSS -> {
                if (player.remainingHP() <= 0) {
                  currentBestCost = costComparator(player.equipmentCost(), currentBestCost)
                }
              }
            }
            boss.resetHealth()
          }
        }
      }
    }

    return currentBestCost
  }

  private fun loadBoss(input: Sequence<String>): Player {
    return input.toList().let { line ->
      val hp = line[0].substringAfter(": ").toInt()
      val weapon = Weapon.values().find { w ->
        w.damage == line[1].substringAfter(": ").toInt()
      }
      val armour = Armour.values().find { a ->
        a.armour == line[2].substringAfter(": ").toInt()
      }
      Player(hp, weapon!!, armour)
    }
  }

  private fun play(player: Player, boss: Player) {
    var activePlayer = HERO
    while (player.remainingHP() >= 0 && boss.remainingHP() >= 0) {
      if (activePlayer == BOSS) {
        boss.attack(player)
      } else {
        player.attack(boss)
      }
      activePlayer = activePlayer.swap()
    }
  }

  interface Equipment {
    val cost: Int
    val damage: Int
    val armour: Int
  }

  enum class Weapon(_cost: Int, _damage: Int) : Equipment {
    DAGGER(8, 4),      // 2.000 per
    SHORTSWORD(10, 5), // 2.000 per
    WARHAMMER(25, 6),  // 4.167 per
    LONGSWORD(40, 7),  // 5.714 per
    GREATAXE(74, 8);   // 9.250 per

    override val cost: Int = _cost
    override val damage: Int = _damage
    override val armour: Int = 0
  }

  enum class Armour(_cost: Int, _armour: Int) : Equipment {
    LEATHER(13, 1),    // 13.000 per
    CHAINMAIL(31, 2),  // 15.500 per
    SPLINTMAIL(53, 3), // 17.667 per
    BANDEDMAIL(75, 4), // 18.750 per
    PLATEMAIL(102, 5); // 20.400 per

    override val cost: Int = _cost
    override val damage: Int = 0
    override val armour: Int = _armour
  }

  enum class Ring(_cost: Int, _damage: Int, _armour: Int) : Equipment {
    DAMAGE_PLUS_ONE(25, 1, 0),    // 25.000 per
    DAMAGE_PLUS_TWO(50, 2, 0),    // 25.000 per
    DAMAGE_PLUS_THREE(100, 3, 0), // 33.333 per
    ARMOUR_PLUS_ONE(20, 0, 1),    // 20.000 per
    ARMOUR_PLUS_TWO(40, 0, 2),    // 20.000 per
    ARMOUR_PLUS_THREE(80, 0, 3);  // 26.667 per

    override val cost: Int = _cost
    override val damage: Int = _damage
    override val armour: Int = _armour
  }

  private class Player(
    val baseHP: Int,
    var weapon: Weapon,
    var armour: Armour? = null,
    var ringOne: Ring? = null,
    var ringTwo: Ring? = null
  ) {

    enum class Type {
      HERO, BOSS;

      fun swap() = when (this) {
        HERO -> BOSS
        BOSS -> HERO
      }
    }

    var damageTaken = 0

    val equipment: List<Equipment?> get() = listOf(weapon, armour, ringOne, ringTwo)

    /**
     * @return remaining hit points
     */
    fun takeDamage(damageTaken: Int): Int {
      this.damageTaken += damageTaken
      return remainingHP()
    }

    fun remainingHP() = baseHP - damageTaken

    fun damage() = equipment.sumOf { it?.damage ?: 0 }
    fun armour() = equipment.sumOf { it?.armour ?: 0 }

    /**
     * returns the remaining HP of the player we attacked
     */
    fun attack(player: Player) {
      player.takeDamage(max(damage() - player.armour(), MIN_DAMAGE))
    }

    fun resetHealth() {
      this.damageTaken = 0
    }

    fun equipmentCost(): Int {
      return equipment.sumOf { it?.cost ?: 0 }
    }
  }

  companion object {
    const val MIN_DAMAGE = 1
    const val PLAYER_HP = 100
  }
}
