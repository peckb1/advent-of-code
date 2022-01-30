package me.peckb.aoc._2018.calendar.day24

import me.peckb.aoc._2018.calendar.day24.Day24.ArmyType.IMMUNE
import me.peckb.aoc._2018.calendar.day24.Day24.ArmyType.INFECTION
import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory

class Day24 @Inject constructor(private val generatorFactory: InputGeneratorFactory) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).read { input ->
    val reader = input.iterator().also { it.next() }
    val (immuneGroups, infectionGroups) = buildArmy(reader)

    battle(immuneGroups, infectionGroups)

    if (immuneGroups.isNotEmpty()) {
      immuneGroups.sumOf { it.units }
    } else {
      infectionGroups.sumOf { it.units }
    }
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).read { input ->
    val data = input.toList()
    var immuneGroups: MutableList<Group>
    var infectionGroups: MutableList<Group>

    var boost = 0
    do {
      boost++
      val reader = data.iterator().also { it.next() }
      buildArmy(reader, boost).also { (immune, infection) ->
        immuneGroups = immune
        infectionGroups = infection
      }
      battle(immuneGroups, infectionGroups)
    } while(infectionGroups.isNotEmpty())

    immuneGroups.sumOf { it.units }
  }

  private fun buildArmy(reader: Iterator<String>, immuneBoost: Int = 0): Pair<MutableList<Group>, MutableList<Group>> {
    val immuneGroups = mutableListOf<Group>()
    val infectionGroups = mutableListOf<Group>()

    var readingImmune = true
    while(readingImmune) {
      val line = reader.next()
      if (line.isNotEmpty()) {
        immuneGroups.add(createGroup(line, IMMUNE, immuneBoost))
      } else {
        readingImmune = false
        reader.next()
      }
    }

    while(reader.hasNext()) {
      val line = reader.next()
      infectionGroups.add(createGroup(line, INFECTION, 0))
    }

    return immuneGroups to infectionGroups
  }

  private fun createGroup(data: String, armyType: ArmyType, attackBoost: Int): Group {
    // 17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
    val numUnits = data.substringBefore(" units").toInt()
    val hitPoints = data.substringAfter("each with ").substringBefore(" hit points").toInt()
    val (weaknesses, immunities) = if (data.contains("(")) {
      findResistances(data.substringAfter("(").substringBefore(")"))
    } else {
      emptySet<AttackType>() to emptySet()
    }
    val (attackDamage, attackType) = data.substringAfter("that does ").substringBefore(" damage").split(" ")
      .let { (damage, type) -> (damage.toInt() + attackBoost) to AttackType.from(type) }
    val initiative = data.substringAfter("initiative ").toInt()

    return Group(numUnits, hitPoints, attackDamage, attackType, weaknesses, immunities, initiative, armyType)
  }

  private fun findResistances(dataArea: String): Pair<Set<AttackType>, Set<AttackType>> {
    // immune to fire; weak to bludgeoning, slashing
    val weaknesses = mutableSetOf<AttackType>()
    val immunities = mutableSetOf<AttackType>()
    val options = dataArea.split("; ")
    options.forEach { area ->
      val type = area.substringBefore(" ")
      val types = area.substringAfter("to ").split(", ").map { AttackType.from(it) }
      when (type) {
        "immune" -> immunities.addAll(types)
        "weak" -> weaknesses.addAll(types)
      }
    }

    return weaknesses to immunities
  }

  private fun battle(immuneGroups: MutableList<Group>, infectionGroups: MutableList<Group>) {
    var turns = 0
    while(immuneGroups.isNotEmpty() && infectionGroups.isNotEmpty() && turns < MAX_TURNS) {
      // targeting phase
      val immuneWithOrder = immuneGroups.sortedWith(compareBy({ it.effectivePower }, { it.initiative })).reversed()
      val infectedWithOrder = infectionGroups.sortedWith(compareBy({ it.effectivePower }, { it.initiative })).reversed()

      val defenderToAttackerGroup = mutableMapOf<Group, Group>()

      fun pickAttackers(attackingGroups: List<Group>, defendingGroups: List<Group>) {
        attackingGroups.forEach { attackingGroup ->
          // determine our attack order
          val orderToAttack = defendingGroups.sortedWith(compareBy(
            { attackingGroup.howMuchDamageDealt(it) }, { it.effectivePower }, { it.initiative })
          ).reversed()
          // pick the first group we are allowed to attack
          val groupToAttack = orderToAttack.firstOrNull {
            !defenderToAttackerGroup.containsKey(it) && attackingGroup.howMuchDamageDealt(it) > 0
          }
          // mark that group as being attacked by us
          groupToAttack?.also { defenderToAttackerGroup[it] = attackingGroup }
        }
      }

      pickAttackers(immuneWithOrder, infectedWithOrder)
      pickAttackers(infectedWithOrder, immuneWithOrder)

      // attacking phase
      val attackingOrder = defenderToAttackerGroup.entries.sortedByDescending { it.value.initiative }
      attackingOrder.forEach { (defender, attacker) ->
        if (attacker.units > 0) {
          val damageToDeal = attacker.howMuchDamageDealt(defender)
          val unitsToLose = damageToDeal / defender.hitPoints
          defender.units -= unitsToLose.toInt()
        }
        if (defender.units <= 0) {
          when (defender.armyType) {
            IMMUNE -> immuneGroups.remove(defender)
            INFECTION -> infectionGroups.remove(defender)
          }
        }
      }
      turns++
    }
  }

  enum class ArmyType {
    IMMUNE, INFECTION
  }

  enum class AttackType {
    COLD, RADIATION, FIRE, SLASHING, BLUDGEONING;

    companion object {
      fun from(data: String) = valueOf(data.uppercase())
    }
  }

  data class Group(
    var units: Int,
    val hitPoints: Int,
    val attackDamage: Int,
    val attackType: AttackType,
    val weaknesses: Set<AttackType>,
    val immunities: Set<AttackType>,
    val initiative: Int,
    val armyType: ArmyType
  ) {
    fun howMuchDamageDealt(defender: Group): Long {
      val modifier = if (defender.immunities.contains(attackType)) {
        0
      } else if (defender.weaknesses.contains(attackType)) {
        2
      } else {
        1
      }
      return effectivePower * modifier
    }

    val effectivePower get() = (units * attackDamage).toLong()
  }

  companion object {
    private const val MAX_TURNS = 2000
  }
}
