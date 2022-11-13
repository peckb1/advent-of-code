package me.peckb.aoc._2019.calendar.day14

import javax.inject.Inject

import me.peckb.aoc.generators.InputGenerator.InputGeneratorFactory
import kotlin.math.min

typealias Chemical = String
typealias Amount = Int
typealias ChemicalAmount = Pair<Amount, Chemical>

class Day14 @Inject constructor(
  private val generatorFactory: InputGeneratorFactory,
) {
  fun partOne(filename: String) = generatorFactory.forFile(filename).readAs(::reaction) { input ->
    val reactions = mutableMapOf<Chemical, Reaction>()
    input.forEach { reaction ->
      val outputChemical = reaction.output.second
      reactions[outputChemical] = reaction
    }

    val oreMined = mine(reactions)

    oreMined
  }

  fun partTwo(filename: String) = generatorFactory.forFile(filename).readAs(::reaction) { input ->
    val reactions = mutableMapOf<Chemical, Reaction>()
    input.forEach { reaction ->
      val outputChemical = reaction.output.second
      reactions[outputChemical] = reaction
    }

    val chemicals = mutableMapOf<Chemical, Amount>().withDefault { 0 }
    var oreMined = mine(reactions, chemicals)
    var fuelGained = 0L
    var percentagesDone = 0
    while(oreMined < 1_000_000_000_000) {
      fuelGained++
      oreMined += mine(reactions, chemicals)
      val percentageComplete = (oreMined.toDouble() / 1_000_000_000_000.0) * 100
      if (percentageComplete.toInt() > percentagesDone) {
        percentagesDone++
        println("$percentagesDone% complete. $fuelGained FUEL so far")
      }
    }

    fuelGained
  }

  private fun mine(
    reactions: MutableMap<Chemical, Reaction>,
    surplusChemicals: MutableMap<Chemical, Amount> = mutableMapOf<Chemical, Amount>().withDefault { 0 }
  ): Long {
    var oreMined = 0L
    val chemicalsNeeded = mutableListOf<ChemicalAmount>().also { it.add(1 to FUEL)  }
//    val surplusChemicals = mutableMapOf<Chemical, Amount>().withDefault { 0 }

    while(chemicalsNeeded.isNotEmpty()) {
      // what do we need to make?
//      println("NEED: $chemicalsNeeded")
//      println("HAVE: $surplusChemicals")
      val (amountOfNeededChemical, neededChemical) = chemicalsNeeded.removeFirst()

      val amountInSurplus = surplusChemicals.getValue(neededChemical)
//      println("\t There is $amountInSurplus $neededChemical in surplus")

      // check if we have _any_ of it in storage
      val chemicalsInStorageWeCanUse = if (amountInSurplus > 0) {
        min(amountInSurplus, amountOfNeededChemical)
      } else {
        0
      }

      if (chemicalsInStorageWeCanUse >= amountOfNeededChemical) {
        // if we have an abundance in storage, just use our surplus
        surplusChemicals[neededChemical] = amountInSurplus - amountOfNeededChemical
//        println("\t We can use all $chemicalsInStorageWeCanUse $neededChemical from storage")
      } else {
//        println("\t We can use $chemicalsInStorageWeCanUse $neededChemical from storage")
        // we don't have enough in storage to cover our needs, so let's make some!
        val reactionToMakeNeededChemical = reactions.getValue(neededChemical)
        val amountOfChemicalMadeFromReaction = reactionToMakeNeededChemical.output.first
        val remainingChemicalToMakeAfterStorageUsage = amountOfNeededChemical - chemicalsInStorageWeCanUse
        val extraChemicalsWouldBeMade = remainingChemicalToMakeAfterStorageUsage % amountOfChemicalMadeFromReaction != 0
        val numberOfReactionsNeeded = if (extraChemicalsWouldBeMade) {
          (remainingChemicalToMakeAfterStorageUsage / amountOfChemicalMadeFromReaction) + 1
        } else {
          (remainingChemicalToMakeAfterStorageUsage / amountOfChemicalMadeFromReaction)
        }
        val amountOfChemicalCreatedFromReaction = numberOfReactionsNeeded * amountOfChemicalMadeFromReaction

//        println("\t We need $numberOfReactionsNeeded reaction(s) of $reactionToMakeNeededChemical")
//        println("\t This will make $amountOfChemicalCreatedFromReaction $neededChemical to cover our $amountOfNeededChemical $neededChemical")
//        if (extraChemicalsWouldBeMade) {
//          println("\t We will end up ${amountOfChemicalCreatedFromReaction - amountOfNeededChemical} $neededChemical that need to be stored")
//        } else {
//          println("\t We will not have to store any extra chemicals, but should cleanup the $chemicalsInStorageWeCanUse we borrowed")
//        }

        reactionToMakeNeededChemical.input.forEach { (reactionInputAmount, reactionInputChemical) ->
          val amountOfInputNeededForReaction = numberOfReactionsNeeded * reactionInputAmount
//          println("\t\t We need $amountOfInputNeededForReaction $reactionInputChemical for the $numberOfReactionsNeeded reactions needed")
//          if (extraChemicalsWouldBeMade) {
//            val surplusChemicalsThatWouldBeCreated = amountOfChemicalCreatedFromReaction - amountOfNeededChemical
//            surplusChemicals[neededChemical] = amountInSurplus + surplusChemicalsThatWouldBeCreated
////            println("\t\t We would create an extra $surplusChemicalsThatWouldBeCreated $neededChemical")
////            println("\t\t Our new total Surplus is ${surplusChemicals[neededChemical]}")
//          } else {
//            surplusChemicals[neededChemical] = amountInSurplus - chemicalsInStorageWeCanUse
////            println("\t\t Taking the $chemicalsInStorageWeCanUse out of storage for a new total of ${surplusChemicals[neededChemical]}")
//          }
          if (reactionInputChemical == ORE) {
            oreMined += amountOfInputNeededForReaction
//            println("\t\t\t Mining $amountOfInputNeededForReaction more order for $oreMined total")
          } else {
//            println("\t\t\t Adding to backlog")
            chemicalsNeeded.add(amountOfInputNeededForReaction to reactionInputChemical)
          }
        }

        if (extraChemicalsWouldBeMade) {
          val surplusChemicalsThatWouldBeCreated = amountOfChemicalCreatedFromReaction - amountOfNeededChemical
          surplusChemicals[neededChemical] = amountInSurplus + surplusChemicalsThatWouldBeCreated
//            println("\t\t We would create an extra $surplusChemicalsThatWouldBeCreated $neededChemical")
//            println("\t\t Our new total Surplus is ${surplusChemicals[neededChemical]}")
        } else {
          surplusChemicals[neededChemical] = amountInSurplus - chemicalsInStorageWeCanUse
//            println("\t\t Taking the $chemicalsInStorageWeCanUse out of storage for a new total of ${surplusChemicals[neededChemical]}")
        }
      }
    }

    return oreMined
  }

  private fun reaction(line: String): Reaction {
    // 7 A, 1 C => 1 D
    val (inputStr, outputStr) = line.split(" => ")
    val inputChemicalAmounts = inputStr.split(", ")
      .map { inputData -> inputData.toChemicalAmount() }
    val outputChemicalAmount = outputStr.toChemicalAmount()

    return Reaction(inputChemicalAmounts, outputChemicalAmount)
  }

  private fun String.toChemicalAmount(): ChemicalAmount = split(" ")
    .let { (amount, chemical) -> ChemicalAmount(amount.toInt(), chemical) }

  data class Reaction(val input: List<ChemicalAmount>, val output: ChemicalAmount)

  companion object {
    private const val FUEL = "FUEL"
    private const val ORE = "ORE"
  }
}
