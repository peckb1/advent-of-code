package me.peckb.aoc._2019.calendar.day14

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

typealias Chemical = String
typealias Amount = Long
typealias ChemicalAmount = Pair<Amount, Chemical>

class Day14 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::reaction) { input ->
    val reactions = setupReactions(input)
    mine(reactions)
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::reaction) { input ->
    val reactions = setupReactions(input)

    // starting at 1 took me ~75 seconds to run - so start closer to speed up tests ;)
    var maxFuel = 3_500_000L
    while(mine(reactions, fuelNeeded = maxFuel) < MAX_ORE) {
      maxFuel++
    }

    // since our last value pushed us over MAX_ORE, subtract one from the result
    maxFuel - 1
  }

  private fun setupReactions(input: Sequence<Reaction>) = mutableMapOf<Chemical, Reaction>().apply {
    input.forEach { reaction ->
      val outputChemical = reaction.output.second
      this[outputChemical] = reaction
    }
  }

  private fun mine(
    reactions: MutableMap<Chemical, Reaction>,
    surplusChemicals: MutableMap<Chemical, Amount> = mutableMapOf<Chemical, Amount>().withDefault { 0 },
    fuelNeeded: Long = 1
  ): Long {
    var oreMined = 0L
    val chemicalsNeeded = mutableMapOf<Chemical, Amount>().also { it[FUEL] = fuelNeeded }

    while(chemicalsNeeded.isNotEmpty()) {
      // what do we need to make next?
      val (neededChemical, amountOfNeededChemical) = chemicalsNeeded.firstNotNullOf { it }.also {
        chemicalsNeeded.remove(it.key)
      }


      // check if we have _any_ of it in storage that we can use
      val amountInSurplus = surplusChemicals.getValue(neededChemical)
      val chemicalsInStorageWeCanUse = min(amountInSurplus, amountOfNeededChemical)

      if (chemicalsInStorageWeCanUse >= amountOfNeededChemical) {
        // if we have an abundance in storage, just use our surplus
        surplusChemicals[neededChemical] = amountInSurplus - amountOfNeededChemical
      } else {
        // we don't have enough in storage to cover our needs, so let's make some!
        val reactionToMakeNeededChemical = reactions.getValue(neededChemical)
        val amountOfChemicalMadeFromReaction = reactionToMakeNeededChemical.output.first
        val remainingChemicalToMakeAfterStorageUsage = amountOfNeededChemical - chemicalsInStorageWeCanUse
        val extraChemicalsWouldBeMade = remainingChemicalToMakeAfterStorageUsage % amountOfChemicalMadeFromReaction != 0L
        val numberOfReactionsNeeded = if (extraChemicalsWouldBeMade) {
          (remainingChemicalToMakeAfterStorageUsage / amountOfChemicalMadeFromReaction) + 1
        } else {
          (remainingChemicalToMakeAfterStorageUsage / amountOfChemicalMadeFromReaction)
        }
        val amountOfChemicalCreatedFromReaction = numberOfReactionsNeeded * amountOfChemicalMadeFromReaction
        if (extraChemicalsWouldBeMade) {
          val surplusChemicalsThatWouldBeCreated = amountOfChemicalCreatedFromReaction - amountOfNeededChemical
          surplusChemicals[neededChemical] = amountInSurplus + surplusChemicalsThatWouldBeCreated
        } else {
          surplusChemicals[neededChemical] = amountInSurplus - chemicalsInStorageWeCanUse
        }

        reactionToMakeNeededChemical.input.forEach { (reactionInputAmount, reactionInputChemical) ->
          val amountOfInputNeededForReaction = numberOfReactionsNeeded * reactionInputAmount
          if (reactionInputChemical == ORE) {
            oreMined += amountOfInputNeededForReaction
          } else {
            chemicalsNeeded.merge(reactionInputChemical, amountOfInputNeededForReaction, Amount::plus)
          }
        }
      }
    }

    return oreMined
  }

  private fun reaction(line: String): Reaction {
    val (inputStr, outputStr) = line.split(" => ")
    val inputChemicalAmounts = inputStr.split(", ")
      .map { inputData -> inputData.toChemicalAmount() }
    val outputChemicalAmount = outputStr.toChemicalAmount()

    return Reaction(inputChemicalAmounts, outputChemicalAmount)
  }

  private fun String.toChemicalAmount(): ChemicalAmount = split(" ")
    .let { (amount, chemical) -> ChemicalAmount(amount.toLong(), chemical) }

  data class Reaction(val input: List<ChemicalAmount>, val output: ChemicalAmount)

  companion object {
    private const val FUEL = "FUEL"
    private const val ORE = "ORE"
    private const val MAX_ORE = 1_000_000_000_000
  }
}
